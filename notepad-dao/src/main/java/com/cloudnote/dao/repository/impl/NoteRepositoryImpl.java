package com.cloudnote.dao.repository.impl;

import com.cloudnote.common.exception.ErrorCode;
import com.cloudnote.common.exception.ServiceException;
import com.cloudnote.dao.mapper.NoteMapper;
import com.cloudnote.dao.pool.DataSource;
import com.cloudnote.dao.repository.NoteRepository;
import com.cloudnote.dao.repository.TagRepository;
import com.cloudnote.model.entity.Note;
import com.cloudnote.model.entity.NoteImage;
import com.cloudnote.model.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class NoteRepositoryImpl implements NoteRepository {
    private static final Logger log = LoggerFactory.getLogger(NoteRepositoryImpl.class);
    private final TagRepository tagRepository = new TagRepositoryImpl();

    @Override
    public Note findById(int id) {
        String sql = "SELECT * FROM notes WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Note note = NoteMapper.fromResultSet(rs);
                    note.setImages(findImagesByNoteId(conn, id));
                    note.setTags(tagRepository.findByNoteId(id));
                    return note;
                }
            }
        } catch (SQLException e) {
            log.error("查询笔记失败, id={}", id, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return null;
    }

    @Override
    public List<Note> findAll() {
        String noteSql = "SELECT * FROM notes WHERE is_deleted = 0 ORDER BY update_time DESC";
        String imageSql = "SELECT * FROM note_images WHERE note_id = ?";
        return queryNotesWithImages(noteSql, imageSql, null);
    }

    @Override
    public List<Note> findDeleted() {
        String noteSql = "SELECT * FROM notes WHERE is_deleted = 1 ORDER BY update_time DESC";
        String imageSql = "SELECT * FROM note_images WHERE note_id = ?";
        return queryNotesWithImages(noteSql, imageSql, null);
    }

    @Override
    public List<Note> search(String keyword) {
        String noteSql = "SELECT * FROM notes WHERE is_deleted = 0 AND (title LIKE ? OR content LIKE ?) ORDER BY update_time DESC";
        String imageSql = "SELECT * FROM note_images WHERE note_id = ?";
        String pattern = "%" + keyword + "%";
        return queryNotesWithImages(noteSql, imageSql, ps -> {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
        });
    }

    @Override
    public List<Note> findByTagId(int tagId) {
        String noteSql = "SELECT n.* FROM notes n JOIN note_tags nt ON n.id = nt.note_id " +
                "WHERE n.is_deleted = 0 AND nt.tag_id = ? ORDER BY n.update_time DESC";
        String imageSql = "SELECT * FROM note_images WHERE note_id = ?";
        return queryNotesWithImages(noteSql, imageSql, ps -> ps.setInt(1, tagId));
    }

    @FunctionalInterface
    private interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private List<Note> queryNotesWithImages(String noteSql, String imageSql, ParamSetter setter) {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = DataSource.getConnection()) {
            // 1. 查所有笔记
            Map<Integer, Note> noteMap = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(noteSql)) {
                if (setter != null) setter.set(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Note note = NoteMapper.fromResultSet(rs);
                        noteMap.put(note.getId(), note);
                    }
                }
            }
            if (noteMap.isEmpty()) return notes;

            // 2. 批量查图片（一次性查所有相关笔记的图片）
            String placeholders = String.join(",", Collections.nCopies(noteMap.size(), "?"));
            String batchImageSql = "SELECT * FROM note_images WHERE note_id IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(batchImageSql)) {
                int i = 1;
                for (int noteId : noteMap.keySet()) ps.setInt(i++, noteId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        NoteImage img = NoteMapper.imageFromResultSet(rs);
                        Note note = noteMap.get(img.getNoteId());
                        if (note != null) note.getImages().add(img);
                    }
                }
            }

            // 3. 批量查标签
            String batchTagSql = "SELECT nt.note_id, t.* FROM tags t JOIN note_tags nt ON t.id = nt.tag_id WHERE nt.note_id IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(batchTagSql)) {
                int i = 1;
                for (int noteId : noteMap.keySet()) ps.setInt(i++, noteId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int noteId = rs.getInt("note_id");
                        Tag tag = NoteMapper.tagFromResultSet(rs);
                        Note note = noteMap.get(noteId);
                        if (note != null) note.getTags().add(tag);
                    }
                }
            }

            notes.addAll(noteMap.values());
        } catch (SQLException e) {
            log.error("查询笔记列表失败", e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return notes;
    }

    private List<NoteImage> findImagesByNoteId(Connection conn, int noteId) throws SQLException {
        String sql = "SELECT * FROM note_images WHERE note_id = ?";
        List<NoteImage> images = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    images.add(NoteMapper.imageFromResultSet(rs));
                }
            }
        }
        return images;
    }

    @Override
    public int save(Note note) {
        String sql = "INSERT INTO notes (title, content, category) VALUES (?, ?, ?)";
        try (Connection conn = DataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int noteId;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, note.getTitle());
                    ps.setString(2, note.getContent());
                    ps.setString(3, note.getCategory());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        noteId = rs.getInt(1);
                    }
                }
                if (note.getImages() != null && !note.getImages().isEmpty()) {
                    saveImages(conn, noteId, note.getImages());
                }
                conn.commit();
                note.setId(noteId);
                log.info("笔记保存成功, id={}, title={}", noteId, note.getTitle());
                return noteId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("保存笔记失败, title={}", note.getTitle(), e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public boolean update(Note note) {
        String sql = "UPDATE notes SET title=?, content=?, category=? WHERE id=?";
        try (Connection conn = DataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int affected;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, note.getTitle());
                    ps.setString(2, note.getContent());
                    ps.setString(3, note.getCategory());
                    ps.setInt(4, note.getId());
                    affected = ps.executeUpdate();
                }
                if (affected > 0) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM note_images WHERE note_id = ?")) {
                        ps.setInt(1, note.getId());
                        ps.executeUpdate();
                    }
                    if (note.getImages() != null && !note.getImages().isEmpty()) {
                        saveImages(conn, note.getId(), note.getImages());
                    }
                    conn.commit();
                    log.info("笔记更新成功, id={}", note.getId());
                    return true;
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("更新笔记失败, id={}", note.getId(), e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public boolean softDelete(int id) {
        return executeUpdate("UPDATE notes SET is_deleted = 1 WHERE id = ?", id, "软删除");
    }

    @Override
    public boolean restore(int id) {
        return executeUpdate("UPDATE notes SET is_deleted = 0 WHERE id = ?", id, "还原");
    }

    @Override
    public boolean deletePermanently(int id) {
        return executeUpdate("DELETE FROM notes WHERE id = ?", id, "永久删除");
    }

    private boolean executeUpdate(String sql, int id, String action) {
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean result = ps.executeUpdate() > 0;
            if (result) log.info("笔记{}成功, id={}", action, id);
            return result;
        } catch (SQLException e) {
            log.error("笔记{}失败, id={}", action, id, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    private void saveImages(Connection conn, int noteId, List<NoteImage> images) throws SQLException {
        String sql = "INSERT INTO note_images (note_id, image_path, image_name) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (NoteImage img : images) {
                ps.setInt(1, noteId);
                ps.setString(2, img.getImagePath());
                ps.setString(3, img.getImageName());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
