package com.cloudnote.service;

import com.cloudnote.model.entity.Tag;
import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(int id);
    int createTag(Tag tag);
    boolean updateTag(Tag tag);
    boolean deleteTag(int id);
    void setNoteTags(int noteId, List<Integer> tagIds);
    void addNoteTag(int noteId, int tagId);
}
