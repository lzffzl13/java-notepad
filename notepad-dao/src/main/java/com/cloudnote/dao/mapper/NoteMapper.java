package com.cloudnote.dao.mapper;

import com.cloudnote.model.entity.Note;
import com.cloudnote.model.entity.NoteImage;
import com.cloudnote.model.entity.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteMapper {

    public static Note fromResultSet(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setId(rs.getInt("id"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setCategory(rs.getString("category"));
        note.setCreateTime(rs.getTimestamp("create_time"));
        note.setUpdateTime(rs.getTimestamp("update_time"));
        note.setDeleted(rs.getInt("is_deleted") == 1);
        note.setImages(new ArrayList<>());
        note.setTags(new ArrayList<>());
        return note;
    }

    public static NoteImage imageFromResultSet(ResultSet rs) throws SQLException {
        NoteImage img = new NoteImage();
        img.setId(rs.getInt("id"));
        img.setNoteId(rs.getInt("note_id"));
        img.setImagePath(rs.getString("image_path"));
        img.setImageName(rs.getString("image_name"));
        img.setUploadTime(rs.getTimestamp("upload_time"));
        return img;
    }

    public static Tag tagFromResultSet(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));
        tag.setColor(rs.getString("color"));
        return tag;
    }
}
