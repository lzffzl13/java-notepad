package com.cloudnote.service.impl;

import com.cloudnote.common.exception.BusinessException;
import com.cloudnote.common.exception.ErrorCode;
import com.cloudnote.common.util.StringUtils;
import com.cloudnote.dao.repository.NoteRepository;
import com.cloudnote.dao.repository.impl.NoteRepositoryImpl;
import com.cloudnote.model.entity.Note;
import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NoteServiceImpl implements NoteService {
    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    private final NoteRepository noteRepository = new NoteRepositoryImpl();

    @Override
    public NoteVO getNoteById(int id) {
        Note note = noteRepository.findById(id);
        if (note == null) {
            throw new BusinessException(ErrorCode.NOTE_NOT_FOUND, "id=" + id);
        }
        return NoteVO.from(note);
    }

    @Override
    public List<NoteVO> getAllNotes() {
        return toVOList(noteRepository.findAll());
    }

    @Override
    public List<NoteVO> getDeletedNotes() {
        return toVOList(noteRepository.findDeleted());
    }

    @Override
    public List<NoteVO> searchNotes(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return getAllNotes();
        }
        return toVOList(noteRepository.search(keyword));
    }

    @Override
    public List<NoteVO> getNotesByTag(int tagId) {
        return toVOList(noteRepository.findByTagId(tagId));
    }

    @Override
    public int createNote(Note note) {
        if (StringUtils.isEmpty(note.getTitle())) {
            throw new BusinessException(ErrorCode.TITLE_EMPTY);
        }
        int id = noteRepository.save(note);
        log.info("创建笔记成功, id={}", id);
        return id;
    }

    @Override
    public boolean updateNote(Note note) {
        if (StringUtils.isEmpty(note.getTitle())) {
            throw new BusinessException(ErrorCode.TITLE_EMPTY);
        }
        boolean result = noteRepository.update(note);
        log.info("更新笔记成功, id={}", note.getId());
        return result;
    }

    @Override
    public boolean deleteNote(int id) {
        return noteRepository.softDelete(id);
    }

    @Override
    public boolean restoreNote(int id) {
        return noteRepository.restore(id);
    }

    @Override
    public boolean deletePermanently(int id) {
        return noteRepository.deletePermanently(id);
    }

    private List<NoteVO> toVOList(List<Note> notes) {
        List<NoteVO> voList = new ArrayList<>();
        for (Note note : notes) {
            voList.add(NoteVO.from(note));
        }
        return voList;
    }
}
