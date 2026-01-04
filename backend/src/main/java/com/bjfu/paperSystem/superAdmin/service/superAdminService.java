package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.User;

import java.time.LocalDateTime;
import java.util.List;

public interface superAdminService {
    // 创建一个用户
    public void createAccount(User user);

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

    // 查找所有的记录
    public List<Logs> findAllLogs();

    // 根据不同的返回结果动态查询记录
    public List<Logs> queryLogs(LocalDateTime opTime,
                                Integer oporId,
                                String opType,
                                Integer paperId);

    // 根据用户类型查找用户
    public List<User> findUserByType(String userType);
}
