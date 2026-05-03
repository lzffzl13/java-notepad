package com.cloudnote.ui.handler;

import com.cloudnote.model.entity.Tag;
import com.cloudnote.service.TagService;
import com.cloudnote.service.impl.TagServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class TagEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TagEventHandler.class);
    private final TagService tagService = new TagServiceImpl();

    public void loadAllTags(Consumer<List<Tag>> callback) {
        new SwingWorker<List<Tag>, Void>() {
            @Override
            protected List<Tag> doInBackground() {
                return tagService.getAllTags();
            }
            @Override
            protected void done() {
                try { callback.accept(get()); } catch (Exception e) { log.error("加载标签失败", e); }
            }
        }.execute();
    }

    public void createTag(Tag tag, Consumer<Integer> callback) {
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return tagService.createTag(tag);
            }
            @Override
            protected void done() {
                try { callback.accept(get()); } catch (Exception e) { log.error("创建标签失败", e); }
            }
        }.execute();
    }
}
