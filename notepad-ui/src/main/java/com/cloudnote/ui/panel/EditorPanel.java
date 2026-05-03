package com.cloudnote.ui.panel;

import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.ui.theme.AppTheme;
import com.cloudnote.ui.theme.ColorConstants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EditorPanel extends JPanel {
    private final JTextField titleField;
    private final JTextArea contentArea;

    public EditorPanel(Runnable onSave, Runnable onExport) {
        setLayout(new BorderLayout(0, AppTheme.GAP));

        // 顶部：标题 + 按钮
        JPanel topPanel = new JPanel(new BorderLayout(AppTheme.GAP, 0));
        titleField = new JTextField();
        titleField.setFont(AppTheme.FONT_TITLE);
        titleField.putClientProperty("JTextField.placeholderText", "输入标题...");
        topPanel.add(titleField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, AppTheme.GAP, 0));
        JButton exportBtn = new JButton("导出");
        JButton saveBtn = new JButton("保存 (Ctrl+S)");
        saveBtn.setBackground(ColorConstants.PRIMARY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        exportBtn.addActionListener(e -> onExport.run());
        saveBtn.addActionListener(e -> onSave.run());
        btnPanel.add(exportBtn);
        btnPanel.add(saveBtn);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 内容编辑区
        contentArea = new JTextArea();
        contentArea.setFont(AppTheme.FONT_BODY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setMargin(new Insets(AppTheme.PADDING, AppTheme.PADDING, AppTheme.PADDING, AppTheme.PADDING));
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorConstants.BORDER),
                "内容", TitledBorder.LEFT, TitledBorder.TOP, AppTheme.FONT_SMALL));
        add(contentScroll, BorderLayout.CENTER);
    }

    public String getTitle() { return titleField.getText(); }
    public String getContent() { return contentArea.getText(); }

    public void displayNote(NoteVO note) {
        titleField.setText(note.getTitle());
        contentArea.setText(note.getContent());
        contentArea.setCaretPosition(0);
    }

    public void clear() {
        titleField.setText("");
        contentArea.setText("");
    }
}
