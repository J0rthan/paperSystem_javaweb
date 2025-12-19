package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.Login.javabeans.User;

import java.util.List;

public interface superAdminService {
    // 创建一个用户
    public void createAccount(String username, String password, String user_type);

    // 查找所有用户（包括被禁用的）
    public List<User> findAllUsers();

    // 查找所有未被禁用的用户
    public List<User> findAllExistUsers();

    // 根据主键查找用户
    public User findUserById(Integer id);

    // 删除一个用户
    public void deleteUser(Integer userId);

    // 修改一个用户
    public void modifyUser(User user);
}
