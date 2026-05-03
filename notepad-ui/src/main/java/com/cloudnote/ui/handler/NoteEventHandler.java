package com.cloudnote.ui.handler;

import com.cloudnote.model.entity.Note;
import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.service.NoteService;
import com.cloudnote.service.impl.NoteServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class NoteEventHandler {
    private static final Logger log = LoggerFactory.getLogger(NoteEventHandler.class);
    private final NoteService noteService = new NoteServiceImpl();

    public void loadAllNotes(Consumer<List<NoteVO>> callback) {
        new SwingWorker<List<NoteVO>, Void>() {
            @Override
            protected List<NoteVO> doInBackground() {
                return noteService.getAllNotes();
            }
            @Override
            protected void done() {
                try { callback.accept(get()); } catch (Exception e) { log.error("加载笔记失败", e); }
            }
        }.execute();
    }

    public void searchNotes(String keyword, Consumer<List<NoteVO>> callback) {
        new SwingWorker<List<NoteVO>, Void>() {
            @Override
            protected List<NoteVO> doInBackground() {
                return noteService.searchNotes(keyword);
            }
            @Override
            protected void done() {
                try { callback.accept(get()); } catch (Exception e) { log.error("搜索失败", e); }
            }
        }.execute();
    }

    public void saveNote(Note note, Consumer<Boolean> callback) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                if (note.getId() == 0) {
                    return noteService.createNote(note) > 0;
                } else {
                    return noteService.updateNote(note);
                }
            }
            @Override
            protected void done() {
                try { callback.accept(get()); } catch (Exception e) { log.error("保存失败", e); callback.accept(false); }
            }
        }.execute();
    }
}
