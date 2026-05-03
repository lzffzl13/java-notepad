package com.cloudnote.dao.repository;

import com.cloudnote.model.entity.Note;
import java.util.List;

public interface NoteRepository {
    Note findById(int id);
    List<Note> findAll();
    List<Note> findDeleted();
    List<Note> search(String keyword);
    List<Note> findByTagId(int tagId);
    int save(Note note);
    boolean update(Note note);
    boolean softDelete(int id);
    boolean restore(int id);
    boolean deletePermanently(int id);
}
