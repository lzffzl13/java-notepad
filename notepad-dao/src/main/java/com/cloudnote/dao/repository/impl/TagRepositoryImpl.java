package com.cloudnote.dao.repository.impl;

import com.cloudnote.common.exception.ErrorCode;
import com.cloudnote.common.exception.ServiceException;
import com.cloudnote.dao.mapper.NoteMapper;
import com.cloudnote.dao.pool.DataSource;
import com.cloudnote.dao.repository.TagRepository;
import com.cloudnote.model.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepositoryImpl implements TagRepository {
    private static final Logger log = LoggerFactory.getLogger(TagRepositoryImpl.class);

    @Override
    public List<Tag> findAll() {
        String sql = "SELECT * FROM tags ORDER BY name";
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tags.add(NoteMapper.tagFromResultSet(rs));
            }
        } catch (SQLException e) {
            log.error("查询所有标签失败", e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return tags;
    }

    @Override
    public Tag findById(int id) {
        String sql = "SELECT * FROM tags WHERE id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return NoteMapper.tagFromResultSet(rs);
            }
        } catch (SQLException e) {
            log.error("查询标签失败, id={}", id, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return null;
    }

    @Override
    public Tag findByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return NoteMapper.tagFromResultSet(rs);
            }
        } catch (SQLException e) {
            log.error("查询标签失败, name={}", name, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return null;
    }

    @Override
    public int save(Tag tag) {
        String sql = "INSERT INTO tags (name, color) VALUES (?, ?)";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tag.getName());
            ps.setString(2, tag.getColor());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                tag.setId(rs.getInt(1));
            }
            log.info("标签保存成功, id={}, name={}", tag.getId(), tag.getName());
            return tag.getId();
        } catch (SQLException e) {
            log.error("保存标签失败, name={}", tag.getName(), e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public boolean update(Tag tag) {
        String sql = "UPDATE tags SET name=?, color=? WHERE id=?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tag.getName());
            ps.setString(2, tag.getColor());
            ps.setInt(3, tag.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("更新标签失败, id={}", tag.getId(), e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("删除标签失败, id={}", id, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public List<Tag> findByNoteId(int noteId) {
        String sql = "SELECT t.* FROM tags t JOIN note_tags nt ON t.id = nt.tag_id WHERE nt.note_id = ?";
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(NoteMapper.tagFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            log.error("查询笔记标签失败, noteId={}", noteId, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return tags;
    }

    @Override
    public List<Integer> findNoteIdsByTagId(int tagId) {
        String sql = "SELECT note_id FROM note_tags WHERE tag_id = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tagId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("note_id"));
                }
            }
        } catch (SQLException e) {
            log.error("查询标签关联笔记失败, tagId={}", tagId, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
        return ids;
    }

    @Override
    public void setNoteTags(int noteId, List<Integer> tagIds) {
        try (Connection conn = DataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM note_tags WHERE note_id = ?")) {
                    ps.setInt(1, noteId);
                    ps.executeUpdate();
                }
                if (tagIds != null && !tagIds.isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO note_tags (note_id, tag_id) VALUES (?, ?)")) {
                        for (int tagId : tagIds) {
                            ps.setInt(1, noteId);
                            ps.setInt(2, tagId);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                conn.commit();
                log.info("笔记标签设置成功, noteId={}, tagIds={}", noteId, tagIds);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("设置笔记标签失败, noteId={}", noteId, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public void addNoteTag(int noteId, int tagId) {
        String sql = "INSERT INTO note_tags (note_id, tag_id) VALUES (?, ?)";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            ps.setInt(2, tagId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("添加笔记标签失败, noteId={}, tagId={}", noteId, tagId, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }

    @Override
    public void deleteNoteTags(int noteId) {
        String sql = "DELETE FROM note_tags WHERE note_id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("删除笔记标签失败, noteId={}", noteId, e);
            throw new ServiceException(ErrorCode.DB_ERROR, e);
        }
    }
}
