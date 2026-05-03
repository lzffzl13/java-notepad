package com.cloudnote.ui.panel;

import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.ui.renderer.NoteListRenderer;
import com.cloudnote.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class NoteListPanel extends JPanel {
    private final DefaultListModel<NoteVO> listModel;
    private final JList<NoteVO> noteList;
    private Runnable onCreateNew;
    private Runnable onDelete;
    private Runnable onToggleRecycle;
    private Runnable onRestore;
    private Runnable onDeleteForever;

    public NoteListPanel(Consumer<NoteVO> onNoteSelected, Runnable onCreateNew, Runnable onDelete,
                         Runnable onToggleRecycle, Runnable onRestore, Runnable onDeleteForever) {
        this.onCreateNew = onCreateNew;
        this.onDelete = onDelete;
        this.onToggleRecycle = onToggleRecycle;
        this.onRestore = onRestore;
        this.onDeleteForever = onDeleteForever;

        setLayout(new BorderLayout(0, AppTheme.GAP));

        // 操作按钮栏
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, AppTheme.GAP, 0));
        JButton btnNew = new JButton("新建笔记");
        JButton btnDelete = new JButton("删除");
        JButton btnRecycle = new JButton("回收站");
        btnNew.addActionListener(e -> onCreateNew.run());
        btnDelete.addActionListener(e -> onDelete.run());
        btnRecycle.addActionListener(e -> onToggleRecycle.run());
        btnPanel.add(btnNew);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRecycle);
        add(btnPanel, BorderLayout.NORTH);

        // 笔记列表
        listModel = new DefaultListModel<>();
        noteList = new JList<>(listModel);
        noteList.setCellRenderer(new NoteListRenderer());
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onNoteSelected.accept(noteList.getSelectedValue());
            }
        });

        // 右键菜单
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuNew = new JMenuItem("新建笔记");
        JMenuItem menuRestore = new JMenuItem("还原笔记");
        JMenuItem menuDelForever = new JMenuItem("彻底删除");
        JMenuItem menuMoveToTrash = new JMenuItem("移入回收站");
        menuNew.addActionListener(e -> onCreateNew.run());
        menuRestore.addActionListener(e -> onRestore.run());
        menuDelForever.addActionListener(e -> onDeleteForever.run());
        menuMoveToTrash.addActionListener(e -> onDelete.run());
        popup.add(menuNew);
        popup.add(menuRestore);
        popup.add(menuDelForever);
        popup.add(menuMoveToTrash);
        noteList.setComponentPopupMenu(popup);

        JScrollPane scrollPane = new JScrollPane(noteList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setNotes(List<NoteVO> notes) {
        listModel.clear();
        for (NoteVO note : notes) {
            listModel.addElement(note);
        }
    }

    public NoteVO getSelectedNote() {
        return noteList.getSelectedValue();
    }

    public void selectNote(NoteVO note) {
        noteList.setSelectedValue(note, true);
    }

    public void setRecycleMode(boolean recycleMode) {
        // 可以在这里调整按钮文字等
    }
}
