package com.cloudnote.service;

import com.cloudnote.model.vo.NoteVO;
import java.io.OutputStream;

public interface ExportService {
    void exportToHtml(NoteVO note, OutputStream out);
}
