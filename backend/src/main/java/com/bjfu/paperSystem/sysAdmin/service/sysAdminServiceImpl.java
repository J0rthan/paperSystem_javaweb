package com.bjfu.paperSystem.sysAdmin.service;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.superAdmin.dao.systemManageDao;
import com.bjfu.paperSystem.sysAdmin.dao.sysAdminDao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class sysAdminServiceImpl implements sysAdminService {
    @Autowired
    private sysAdminDao sysDao;

    @Autowired
    private systemManageDao sysManageDao;

    @Override
    public void createAccount(User user) {
        // 设置状态为exist
        user.setStatus("exist");

        // 执行插入
        sysDao.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> userList = sysDao.findAll();

        return userList;
    }

    @Override
    public List<User> findAllExistUsers() {
        List<User> userList = sysDao.findByStatus("exist");

        return userList;
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = sysDao.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus("not_exist");
            sysDao.save(user);
        }
    }

    @Override
    public User findUserById(Integer userId) {
        return sysDao.findById(userId).orElse(null);
    }

    @Override
    public void modifyUser(User user) {
        User dbUser = sysDao.findById(user.getUserId()).orElse(null);
        dbUser.setUserName(user.getUserName());
        // 密码留空则不修改
        if (!user.getPassword().equals("")) {
            dbUser.setPassword(user.getPassword());
        }
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setUserType(user.getUserType());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());
        dbUser.setRegisterTime(user.getRegisterTime());
        dbUser.setStatus(user.getStatus());

        sysDao.save(dbUser);
    }

    @Override
    public List<Logs> findAllLogs() {
        List<Logs> logList = sysManageDao.findAll();

        return logList;
    }

    @Override
    public List<Logs> queryLogs(
            LocalDateTime opTime,
            Integer oporId,
            String opType,
            Integer paperId) {

        return sysManageDao.findAll((root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 操作时间
            if (opTime != null) {
                predicates.add(
                        cb.equal(root.get("opTime"), opTime)
                );
            }

            // 操作人 ID
            if (oporId != null) {
                predicates.add(
                        cb.equal(root.get("oporId"), oporId)
                );
            }

            // 操作类型（模糊查询更合理）
            if (opType != null && !opType.isEmpty()) {
                predicates.add(
                        cb.like(root.get("opType"), "%" + opType + "%")
                );
            }

            // 稿件 ID
            if (paperId != null) {
                predicates.add(
                        cb.equal(root.get("paperId"), paperId)
                );
            }

            // where 条件拼接
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
