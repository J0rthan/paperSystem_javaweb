package com.bjfu.paperSystem.PasswordResetCode.Service;

import com.bjfu.paperSystem.PasswordResetCode.Dao.PasswordResetCodeDao;
import com.bjfu.paperSystem.javabeans.PasswordResetCode;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.mailUtils.MailUtil;
import com.bjfu.paperSystem.superAdmin.dao.superAdminDao;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PasswordResetCodeService {
    @Autowired
    private superAdminService suService; // 你自己的查用户Service（按用户名查）

    @Autowired
    private superAdminDao suDao;

    @Autowired
    private PasswordResetCodeDao codeDao;
    @Autowired
    private MailUtil mailUtil1;   // 你项目里的发邮件工具

    @Transactional
    public void sendResetCode(String userName) {
        User user = suService.findUserByName(userName);
        if (user == null) throw new RuntimeException("用户名不存在");

        String email = user.getEmail();
        if (email == null || email.isBlank()) throw new RuntimeException("该用户未绑定邮箱");

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(1);

        PasswordResetCode entity = codeDao.findByUserId(user.getUserId())
                .orElseGet(() -> {
                    PasswordResetCode x = new PasswordResetCode();
                    x.setUserId(user.getUserId());
                    return x;
                });

        entity.setCode(code);
        entity.setCreatedAt(now);
        entity.setExpiresAt(expire);
        entity.setUsed(false);

        codeDao.save(entity);

        String mailBody = """
            您正在找回密码。
            验证码：%s
            有效期：1分钟
            """.formatted(code);

        mailUtil1.sendTextMail(email, "找回密码验证码", mailBody);
    }

    @Transactional
    public void resetPassword(String userName, String code, String newPassword) {
        User user = suService.findUserByName(userName);
        if (user == null) throw new RuntimeException("用户名不存在");

        PasswordResetCode prc = codeDao.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("请先获取验证码"));

        if (Boolean.TRUE.equals(prc.getUsed())) throw new RuntimeException("验证码已使用");
        if (prc.getExpiresAt().isBefore(LocalDateTime.now())) throw new RuntimeException("验证码已过期");
        if (!prc.getCode().equals(code)) throw new RuntimeException("验证码错误");

        // 你说不加密：直接存明文（仅按你的要求）
        user.setPassword(newPassword);
        suDao.save(user); // 你自己的保存方法（或 userDao.save）

        prc.setUsed(true);
        codeDao.save(prc); //
    }
}
