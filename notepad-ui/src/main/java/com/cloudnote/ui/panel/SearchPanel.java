package com.cloudnote.ui.panel;

import com.cloudnote.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SearchPanel extends JPanel {
    private final JTextField searchField;

    public SearchPanel(Consumer<String> onSearch, Runnable onClear) {
        setLayout(new BorderLayout(AppTheme.GAP, 0));
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "搜索笔记...");
        searchField.addActionListener(e -> onSearch.accept(searchField.getText().trim()));

        JButton searchBtn = new JButton("搜索");
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                onClear.run();
            } else {
                onSearch.accept(keyword);
            }
        });

        add(searchField, BorderLayout.CENTER);
        add(searchBtn, BorderLayout.EAST);
    }
}
