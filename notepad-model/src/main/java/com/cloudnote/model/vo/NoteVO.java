package com.cloudnote.model.vo;

import com.cloudnote.model.entity.Note;
import com.cloudnote.model.entity.NoteImage;
import com.cloudnote.model.entity.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteVO {
    private int id;
    private String title;
    private String content;
    private String category;
    private String createTimeStr;
    private String updateTimeStr;
    private List<NoteImage> images;
    private List<Tag> tags;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static NoteVO from(Note note) {
        NoteVO vo = new NoteVO();
        vo.id = note.getId();
        vo.title = note.getTitle();
        vo.content = note.getContent();
        vo.category = note.getCategory();
        vo.createTimeStr = note.getCreateTime() != null ? SDF.format(note.getCreateTime()) : "";
        vo.updateTimeStr = note.getUpdateTime() != null ? SDF.format(note.getUpdateTime()) : "";
        vo.images = note.getImages();
        vo.tags = note.getTags();
        return vo;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getCreateTimeStr() { return createTimeStr; }
    public String getUpdateTimeStr() { return updateTimeStr; }
    public List<NoteImage> getImages() { return images; }
    public List<Tag> getTags() { return tags; }

    @Override
    public String toString() { return title; }
}
