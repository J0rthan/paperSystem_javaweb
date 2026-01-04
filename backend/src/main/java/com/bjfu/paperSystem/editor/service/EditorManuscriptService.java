package com.bjfu.paperSystem.editor.service;
import com.bjfu.paperSystem.dto.EditorManuscriptDTO;
import java.util.List;

public interface EditorManuscriptService {
    List<EditorManuscriptDTO> getMyManuscripts(int editorId);
}