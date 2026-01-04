package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class editorServiceImpl implements editorService{
    @Autowired
    private UserDao userDao;
}
