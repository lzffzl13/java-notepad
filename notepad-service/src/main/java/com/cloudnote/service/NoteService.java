package com.cloudnote.service;

import com.cloudnote.model.entity.Note;
import com.cloudnote.model.vo.NoteVO;
import java.util.List;

public interface NoteService {
    NoteVO getNoteById(int id);
    List<NoteVO> getAllNotes();
    List<NoteVO> getDeletedNotes();
    List<NoteVO> searchNotes(String keyword);
    List<NoteVO> getNotesByTag(int tagId);
    int createNote(Note note);
    boolean updateNote(Note note);
    boolean deleteNote(int id);
    boolean restoreNote(int id);
    boolean deletePermanently(int id);
}
