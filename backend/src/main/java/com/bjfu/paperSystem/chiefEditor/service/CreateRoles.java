package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorUserDao;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorEditorial_BoardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateRoles {

    @Autowired
    private ChiefEditorUserDao userRepository;

    @Autowired
    private ChiefEditorEditorial_BoardDao editorialBoardRepository;

    /**
     * 统一创建角色逻辑
     * @param username 用户名
     * @param password 密码
     * @param fullName 真实姓名
     * @param email 邮箱
     * @param role 角色类型: editor(责编), REVIEWER(审稿人), editorIAL_ADMIN(编辑部管理员)
     * @param specialty 专长/研究方向 (用于责编和审稿人)
     * @param profile 简介 (仅用于责编)
     */
    @Transactional // 关键：事务注解，保证两个表的操作要么同时成功，要么同时失败
    public void createRole(String username, String password, String fullName, String email,
                           String role, String specialty, String profile) {

        // 1. === 第一步：先创建 User 对象 ===
        User user = new User();
        user.setUserName(username);
        user.setPassword(password); // 实际项目中建议这里用 MD5 加密
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRegisterTime(LocalDateTime.now());
        user.setStatus("exist"); // 默认激活账号

        // 根据角色设置 User 表特有的字段
        if ("editor".equals(role)) {
            user.setUserType("editor");
        } else if ("REVIEWER".equals(role)) {
            user.setUserType("REVIEWER");
            // 审稿人的研究方向直接存在 User 表的 investigationDirection 字段
            user.setInvestigationDirection(specialty);
        } else if ("editorIAL_ADMIN".equals(role)) {
            user.setUserType("editorIAL_ADMIN");
        }

        // 2. === 第二步：保存 User，拿到生成的 ID ===
        // 这一步非常关键！执行 save 后，user 对象里就会自动填入数据库生成的 user_id
        User savedUser = userRepository.save(user);

        // 3. === 第三步：如果是责编，关联写入 editorial_Board 表 ===
        if ("editor".equals(role)) {
            Editorial_Board board = new Editorial_Board();

            // --- 这里就是你问的“正确关联” ---
            // 我们把刚才保存好的、带有 ID 的 savedUser 对象塞给 board 对象
            // JPA 会自动把 user_id 填入 editorial_board 表的外键列
            board.setUser(savedUser);

            board.setPosition("editor"); // 职位标记
            board.setSpecialty(specialty); // 负责的领域
            board.setProfile(profile);     // 简介

            editorialBoardRepository.save(board);
        }
    }
}