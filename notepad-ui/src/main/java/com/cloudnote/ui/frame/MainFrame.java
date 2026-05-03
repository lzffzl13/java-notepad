package com.cloudnote.ui.frame;

import com.cloudnote.model.entity.Note;
import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.service.NoteService;
import com.cloudnote.service.TagService;
import com.cloudnote.service.impl.NoteServiceImpl;
import com.cloudnote.service.impl.TagServiceImpl;
import com.cloudnote.ui.handler.NoteEventHandler;
import com.cloudnote.ui.handler.TagEventHandler;
import com.cloudnote.ui.panel.*;
import com.cloudnote.ui.theme.AppTheme;
import com.cloudnote.ui.theme.ColorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private final NoteService noteService;
    private final TagService tagService;

    private NoteListPanel noteListPanel;
    private EditorPanel editorPanel;
    private ImagePanel imagePanel;
    private TagPanel tagPanel;
    private SearchPanel searchPanel;

    private NoteVO currentNote;
    private boolean isModified = false;
    private boolean isRecycleMode = false;

    public MainFrame() {
        this.noteService = new NoteServiceImpl();
        this.tagService = new TagServiceImpl();
        initUI();
        loadNotes();
        loadTags();
    }

    private void initUI() {
        setTitle("云记事本");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(ColorConstants.BG_LIGHT);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExit();
            }
        });

        // Ctrl+S 快捷键
        KeyStroke saveKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveKey, "save");
        getRootPane().getActionMap().put("save", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { saveCurrentNote(); }
        });

        // 初始化面板
        searchPanel = new SearchPanel(this::searchNotes, this::loadNotes);
        tagPanel = new TagPanel(this::filterByTag, this::loadNotes);
        noteListPanel = new NoteListPanel(this::onNoteSelected, this::createNewNote, this::deleteCurrentNote, this::toggleRecycleMode, this::restoreSelectedNote, this::deletePermanently);
        editorPanel = new EditorPanel(this::saveCurrentNote, this::exportNote);
        imagePanel = new ImagePanel(this::onImageAdded);

        // 左侧面板
        JPanel leftPanel = new JPanel(new BorderLayout(0, AppTheme.GAP));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(AppTheme.PADDING, AppTheme.PADDING, AppTheme.PADDING, 0));
        leftPanel.setPreferredSize(new Dimension(AppTheme.LEFT_PANEL_WIDTH, 0));
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(noteListPanel, BorderLayout.CENTER);
        leftPanel.add(tagPanel, BorderLayout.SOUTH);

        // 右侧面板
        JPanel rightPanel = new JPanel(new BorderLayout(0, AppTheme.GAP));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(AppTheme.PADDING, AppTheme.PADDING, AppTheme.PADDING, AppTheme.PADDING));
        rightPanel.add(editorPanel, BorderLayout.CENTER);
        rightPanel.add(imagePanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(AppTheme.LEFT_PANEL_WIDTH);
        splitPane.setBorder(null);
        add(splitPane);

        log.info("界面初始化完成");
    }

    private void loadNotes() {
        SwingWorker<List<NoteVO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NoteVO> doInBackground() {
                return isRecycleMode ? noteService.getDeletedNotes() : noteService.getAllNotes();
            }
            @Override
            protected void done() {
                try {
                    noteListPanel.setNotes(get());
                } catch (Exception e) {
                    log.error("加载笔记列表失败", e);
                    JOptionPane.showMessageDialog(MainFrame.this, "加载笔记失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadTags() {
        SwingWorker<List<com.cloudnote.model.entity.Tag>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<com.cloudnote.model.entity.Tag> doInBackground() {
                return tagService.getAllTags();
            }
            @Override
            protected void done() {
                try {
                    tagPanel.setTags(get());
                } catch (Exception e) {
                    log.error("加载标签失败", e);
                }
            }
        };
        worker.execute();
    }

    private void searchNotes(String keyword) {
        SwingWorker<List<NoteVO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NoteVO> doInBackground() {
                return noteService.searchNotes(keyword);
            }
            @Override
            protected void done() {
                try {
                    noteListPanel.setNotes(get());
                } catch (Exception e) {
                    log.error("搜索失败", e);
                }
            }
        };
        worker.execute();
    }

    private void filterByTag(int tagId) {
        SwingWorker<List<NoteVO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NoteVO> doInBackground() {
                return noteService.getNotesByTag(tagId);
            }
            @Override
            protected void done() {
                try {
                    noteListPanel.setNotes(get());
                } catch (Exception e) {
                    log.error("按标签筛选失败", e);
                }
            }
        };
        worker.execute();
    }

    private void onNoteSelected(NoteVO note) {
        if (note == null) return;
        if (isModified && !confirmDiscard()) {
            noteListPanel.selectNote(currentNote);
            return;
        }
        currentNote = note;
        editorPanel.displayNote(note);
        imagePanel.displayImages(note.getImages());
        isModified = false;
        setTitle("云记事本 - " + note.getTitle());
    }

    private void createNewNote() {
        if (isModified && !confirmDiscard()) return;
        currentNote = null;
        editorPanel.clear();
        imagePanel.clear();
        isModified = false;
        setTitle("云记事本 - 新建笔记");
    }

    private void saveCurrentNote() {
        String title = editorPanel.getTitle();
        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "标题不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                if (currentNote == null) {
                    Note note = new Note(title, editorPanel.getContent());
                    note.setImages(imagePanel.getImages());
                    int id = noteService.createNote(note);
                    return id > 0;
                } else {
                    Note note = new Note(title, editorPanel.getContent());
                    note.setId(currentNote.getId());
                    note.setImages(imagePanel.getImages());
                    return noteService.updateNote(note);
                }
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        isModified = false;
                        JOptionPane.showMessageDialog(MainFrame.this, "保存成功！");
                        loadNotes();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    log.error("保存笔记异常", e);
                    JOptionPane.showMessageDialog(MainFrame.this, "保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteCurrentNote() {
        if (currentNote == null) {
            JOptionPane.showMessageDialog(this, "请先选择笔记");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this, "移入回收站？", "确认", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    noteService.deleteNote(currentNote.getId());
                    return null;
                }
                @Override
                protected void done() {
                    createNewNote();
                    loadNotes();
                }
            };
            worker.execute();
        }
    }

    private void restoreSelectedNote() {
        NoteVO note = noteListPanel.getSelectedNote();
        if (note == null) return;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                noteService.restoreNote(note.getId());
                return null;
            }
            @Override
            protected void done() {
                loadNotes();
                JOptionPane.showMessageDialog(MainFrame.this, "已还原");
            }
        };
        worker.execute();
    }

    private void deletePermanently() {
        NoteVO note = noteListPanel.getSelectedNote();
        if (note == null) return;
        int c = JOptionPane.showConfirmDialog(this, "确定永久删除？无法恢复！", "警告", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    noteService.deletePermanently(note.getId());
                    return null;
                }
                @Override
                protected void done() {
                    loadNotes();
                }
            };
            worker.execute();
        }
    }

    private void toggleRecycleMode() {
        isRecycleMode = !isRecycleMode;
        noteListPanel.setRecycleMode(isRecycleMode);
        loadNotes();
    }

    private void exportNote() {
        if (currentNote == null) {
            JOptionPane.showMessageDialog(this, "没有可导出的内容");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("HTML文件", "html"));
        chooser.setSelectedFile(new java.io.File(currentNote.getTitle() + ".html"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    try (var out = new java.io.FileOutputStream(chooser.getSelectedFile())) {
                        new com.cloudnote.service.impl.ExportServiceImpl().exportToHtml(currentNote, out);
                        return true;
                    } catch (Exception e) {
                        log.error("导出失败", e);
                        return false;
                    }
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(MainFrame.this, "导出成功！");
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, "导出失败！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainFrame.this, "导出异常", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void onImageAdded() {
        isModified = true;
    }

    private boolean confirmDiscard() {
        return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
                "有未保存更改，是否放弃？", "确认", JOptionPane.YES_NO_OPTION);
    }

    private void confirmExit() {
        if (isModified && !confirmDiscard()) return;
        System.exit(0);
    }
}
