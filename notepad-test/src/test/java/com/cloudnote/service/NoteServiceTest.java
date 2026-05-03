package com.cloudnote.service;

import com.cloudnote.common.exception.BusinessException;
import com.cloudnote.model.entity.Note;
import com.cloudnote.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteServiceTest {
    private static NoteService noteService;

    @BeforeAll
    static void setUp() {
        noteService = new NoteServiceImpl();
    }

    @Test
    void createNoteWithEmptyTitleShouldThrow() {
        Note note = new Note("", "content");
        assertThrows(BusinessException.class, () -> noteService.createNote(note));
    }

    @Test
    void createNoteWithNullTitleShouldThrow() {
        Note note = new Note(null, "content");
        assertThrows(BusinessException.class, () -> noteService.createNote(note));
    }
}
