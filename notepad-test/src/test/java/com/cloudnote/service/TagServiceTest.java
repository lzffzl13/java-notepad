package com.cloudnote.service;

import com.cloudnote.common.exception.BusinessException;
import com.cloudnote.model.entity.Tag;
import com.cloudnote.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagServiceTest {
    private static TagService tagService;

    @BeforeAll
    static void setUp() {
        tagService = new TagServiceImpl();
    }

    @Test
    void createTagWithEmptyNameShouldThrow() {
        Tag tag = new Tag("");
        assertThrows(BusinessException.class, () -> tagService.createTag(tag));
    }

    @Test
    void createTagWithNullNameShouldThrow() {
        Tag tag = new Tag(null);
        assertThrows(BusinessException.class, () -> tagService.createTag(tag));
    }
}
