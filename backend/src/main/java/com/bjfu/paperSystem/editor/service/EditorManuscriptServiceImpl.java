package com.bjfu.paperSystem.editor.service;
import com.bjfu.paperSystem.editor.dao.EditorManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EditorManuscriptServiceImpl implements EditorManuscriptService {
    @Autowired private EditorManuscriptDao manuscriptDao;

    @Override
    public List<Manuscript> getMyManuscripts(int editorId) {
        return manuscriptDao.findByEditorId(editorId);
    }
}