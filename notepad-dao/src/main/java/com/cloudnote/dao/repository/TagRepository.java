package com.cloudnote.dao.repository;

import com.cloudnote.model.entity.Tag;
import java.util.List;

public interface TagRepository {
    List<Tag> findAll();
    Tag findById(int id);
    Tag findByName(String name);
    int save(Tag tag);
    boolean update(Tag tag);
    boolean delete(int id);
    List<Tag> findByNoteId(int noteId);
    List<Integer> findNoteIdsByTagId(int tagId);
    void setNoteTags(int noteId, List<Integer> tagIds);
    void addNoteTag(int noteId, int tagId);
    void deleteNoteTags(int noteId);
}
