package com.cloudnote.service.impl;

import com.cloudnote.common.exception.BusinessException;
import com.cloudnote.common.exception.ErrorCode;
import com.cloudnote.common.util.StringUtils;
import com.cloudnote.dao.repository.TagRepository;
import com.cloudnote.dao.repository.impl.TagRepositoryImpl;
import com.cloudnote.model.entity.Tag;
import com.cloudnote.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TagServiceImpl implements TagService {
    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);
    private final TagRepository tagRepository = new TagRepositoryImpl();

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getTagById(int id) {
        return tagRepository.findById(id);
    }

    @Override
    public int createTag(Tag tag) {
        if (StringUtils.isEmpty(tag.getName())) {
            throw new BusinessException(ErrorCode.TITLE_EMPTY, "标签名不能为空");
        }
        Tag existing = tagRepository.findByName(tag.getName());
        if (existing != null) {
            throw new BusinessException(ErrorCode.TAG_DUPLICATE, tag.getName());
        }
        return tagRepository.save(tag);
    }

    @Override
    public boolean updateTag(Tag tag) {
        return tagRepository.update(tag);
    }

    @Override
    public boolean deleteTag(int id) {
        return tagRepository.delete(id);
    }

    @Override
    public void setNoteTags(int noteId, List<Integer> tagIds) {
        tagRepository.setNoteTags(noteId, tagIds);
    }

    @Override
    public void addNoteTag(int noteId, int tagId) {
        tagRepository.addNoteTag(noteId, tagId);
    }
}
