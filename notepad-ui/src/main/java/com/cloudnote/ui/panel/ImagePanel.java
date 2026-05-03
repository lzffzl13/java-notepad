package com.cloudnote.ui.panel;

import com.cloudnote.common.util.ImageUtils;
import com.cloudnote.model.entity.NoteImage;
import com.cloudnote.ui.theme.AppTheme;
import com.cloudnote.ui.theme.ColorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(ImagePanel.class);
    private final JPanel thumbPanel;
    private final List<NoteImage> images = new ArrayList<>();
    private final Runnable onImageAdded;

    public ImagePanel(Runnable onImageAdded) {
        this.onImageAdded = onImageAdded;
        setLayout(new BorderLayout(0, AppTheme.GAP));
        setPreferredSize(new Dimension(0, 180));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorConstants.BORDER),
                "附件图片", TitledBorder.LEFT, TitledBorder.TOP, AppTheme.FONT_SMALL));

        JButton addBtn = new JButton("添加图片");
        addBtn.addActionListener(e -> addImage());
        add(addBtn, BorderLayout.NORTH);

        thumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, AppTheme.GAP, AppTheme.GAP));
        JScrollPane scrollPane = new JScrollPane(thumbPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public List<NoteImage> getImages() {
        return images;
    }

    public void displayImages(List<NoteImage> noteImages) {
        clear();
        if (noteImages == null) return;
        for (NoteImage img : noteImages) {
            images.add(img);
            addThumbnail(img.getImagePath(), img.getImageName());
        }
    }

    public void clear() {
        images.clear();
        thumbPanel.removeAll();
        thumbPanel.revalidate();
        thumbPanel.repaint();
    }

    private void addImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("图片文件", "jpg", "jpeg", "png", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            ImageUtils.ensureImageDir();
            Path imageDir = ImageUtils.getImageDir();

            for (File file : files) {
                try {
                    String newName = ImageUtils.generateImageName(file.getName());
                    Path target = imageDir.resolve(newName);
                    Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                    NoteImage img = new NoteImage(target.toString(), file.getName());
                    images.add(img);
                    addThumbnail(target.toString(), file.getName());
                    onImageAdded.run();
                } catch (IOException e) {
                    log.error("图片保存失败: {}", file.getName(), e);
                    JOptionPane.showMessageDialog(this, "图片保存失败: " + file.getName(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addThumbnail(String path, String name) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(AppTheme.IMAGE_THUMB_SIZE, AppTheme.IMAGE_THUMB_SIZE, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(img));
            label.setToolTipText(name);
            label.setBorder(BorderFactory.createLineBorder(ColorConstants.BORDER));
            thumbPanel.add(label);
            thumbPanel.revalidate();
            thumbPanel.repaint();
        } catch (Exception e) {
            log.warn("图片加载失败: {}", path, e);
            JLabel placeholder = new JLabel("图片丢失");
            placeholder.setPreferredSize(new Dimension(AppTheme.IMAGE_THUMB_SIZE, AppTheme.IMAGE_THUMB_SIZE));
            placeholder.setBorder(BorderFactory.createLineBorder(ColorConstants.DANGER));
            placeholder.setHorizontalAlignment(SwingConstants.CENTER);
            thumbPanel.add(placeholder);
        }
    }
}
