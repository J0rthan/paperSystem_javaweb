package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class chiefEditorServiceImpl implements chiefEditorService{
    @Autowired
    private UserDao userDao;
}
