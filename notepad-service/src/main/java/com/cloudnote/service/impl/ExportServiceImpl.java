package com.cloudnote.service.impl;

import com.cloudnote.common.exception.ErrorCode;
import com.cloudnote.common.exception.ServiceException;
import com.cloudnote.model.entity.NoteImage;
import com.cloudnote.model.vo.NoteVO;
import com.cloudnote.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ExportServiceImpl implements ExportService {
    private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);

    @Override
    public void exportToHtml(NoteVO note, OutputStream out) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>")
                    .append("<meta charset='UTF-8'>")
                    .append("<title>").append(note.getTitle()).append("</title>")
                    .append("<style>")
                    .append("body{font-family:'Microsoft YaHei',Arial,sans-serif;max-width:800px;margin:50px auto;padding:20px}")
                    .append("img{max-width:100%;margin:10px 0}")
                    .append(".meta{color:#666;font-size:12px}")
                    .append("h1{border-bottom:2px solid #3498db;padding-bottom:10px}")
                    .append("</style></head><body>");

            html.append("<h1>").append(note.getTitle()).append("</h1>");
            html.append("<p class='meta'>创建时间：").append(note.getCreateTimeStr()).append("</p>");
            html.append("<p class='meta'>更新时间：").append(note.getUpdateTimeStr()).append("</p>");
            html.append("<hr>");
            html.append("<div>").append(note.getContent().replace("\n", "<br>")).append("</div>");

            if (note.getImages() != null && !note.getImages().isEmpty()) {
                html.append("<h3>附件图片：</h3>");
                for (NoteImage img : note.getImages()) {
                    html.append("<img src='").append(img.getImagePath()).append("' alt='").append(img.getImageName()).append("'><br>");
                }
            }

            html.append("</body></html>");
            out.write(html.toString().getBytes(StandardCharsets.UTF_8));
            log.info("笔记导出成功, title={}", note.getTitle());
        } catch (IOException e) {
            log.error("笔记导出失败, title={}", note.getTitle(), e);
            throw new ServiceException(ErrorCode.EXPORT_FAILED, e);
        }
    }
}
