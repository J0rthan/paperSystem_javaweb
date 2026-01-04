package com.bjfu.paperSystem.editor.service;
import com.bjfu.paperSystem.javabeans.Manuscript;
import java.util.List;

public interface EditorManuscriptService {
    List<Manuscript> getMyManuscripts(int editorId);
}