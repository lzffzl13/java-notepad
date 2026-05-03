package com.cloudnote.ui.panel;

import com.cloudnote.model.entity.Tag;
import com.cloudnote.ui.theme.AppTheme;
import com.cloudnote.ui.theme.ColorConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class TagPanel extends JPanel {
    private final Consumer<Integer> onTagSelected;
    private final Runnable onShowAll;

    public TagPanel(Consumer<Integer> onTagSelected, Runnable onShowAll) {
        this.onTagSelected = onTagSelected;
        this.onShowAll = onShowAll;
        setLayout(new FlowLayout(FlowLayout.LEFT, AppTheme.GAP, AppTheme.GAP));
    }

    public void setTags(List<Tag> tags) {
        removeAll();

        JButton allBtn = new JButton("全部");
        allBtn.setBackground(ColorConstants.PRIMARY);
        allBtn.setForeground(Color.WHITE);
        allBtn.setFocusPainted(false);
        allBtn.setBorderPainted(false);
        allBtn.setOpaque(true);
        allBtn.addActionListener(e -> onShowAll.run());
        add(allBtn);

        for (Tag tag : tags) {
            JButton btn = new JButton(tag.getName());
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            try {
                btn.setBackground(Color.decode(tag.getColor()));
                btn.setForeground(Color.WHITE);
            } catch (NumberFormatException e) {
                btn.setBackground(ColorConstants.BG_LIGHT);
                btn.setForeground(ColorConstants.TEXT_PRIMARY);
            }
            btn.addActionListener(e -> onTagSelected.accept(tag.getId()));
            add(btn);
        }

        revalidate();
        repaint();
    }
}
