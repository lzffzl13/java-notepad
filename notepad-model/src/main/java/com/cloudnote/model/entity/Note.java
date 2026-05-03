package com.cloudnote.model.entity;

import java.util.Date;
import java.util.List;

public class Note {
    private int id;
    private String title;
    private String content;
    private String category;
    private Date createTime;
    private Date updateTime;
    private boolean deleted;
    private List<NoteImage> images;
    private List<Tag> tags;

    public Note() {}

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public List<NoteImage> getImages() { return images; }
    public void setImages(List<NoteImage> images) { this.images = images; }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }

    @Override
    public String toString() { return title; }
}
