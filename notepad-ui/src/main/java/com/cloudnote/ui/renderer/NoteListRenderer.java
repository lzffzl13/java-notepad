package com.cloudnote.ui.renderer;

import com.cloudnote.common.util.StringUtils;
import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.ui.theme.AppTheme;
import com.cloudnote.ui.theme.ColorConstants;

import javax.swing.*;
import java.awt.*;

public class NoteListRenderer extends DefaultListCellRenderer {
    private static final int PREVIEW_LEN = 30;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        if (value instanceof NoteVO note) {
            JLabel titleLabel = new JLabel(note.getTitle());
            titleLabel.setFont(AppTheme.FONT_LIST_TITLE);

            String preview = StringUtils.truncate(note.getContent(), PREVIEW_LEN);
            JLabel previewLabel = new JLabel(preview);
            previewLabel.setFont(AppTheme.FONT_SMALL);
            previewLabel.setForeground(ColorConstants.TEXT_SECONDARY);

            JLabel timeLabel = new JLabel(note.getUpdateTimeStr());
            timeLabel.setFont(AppTheme.FONT_LIST_TIME);
            timeLabel.setForeground(ColorConstants.TEXT_SECONDARY);
            timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            JPanel textPanel = new JPanel(new BorderLayout(0, 2));
            textPanel.setOpaque(false);
            textPanel.add(titleLabel, BorderLayout.NORTH);
            textPanel.add(previewLabel, BorderLayout.CENTER);

            panel.add(textPanel, BorderLayout.CENTER);
            panel.add(timeLabel, BorderLayout.EAST);
        }

        if (isSelected) {
            panel.setBackground(ColorConstants.SELECTED_BG);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, ColorConstants.PRIMARY),
                    BorderFactory.createEmptyBorder(8, 7, 8, 10)));
        } else {
            panel.setBackground(Color.WHITE);
        }

        return panel;
    }
}
