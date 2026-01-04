package com.bjfu.paperSystem.PasswordResetCode.Controller;

import com.bjfu.paperSystem.PasswordResetCode.Service.PasswordResetCodeService;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class PasswordResetCodeController {
    @Autowired private PasswordResetCodeService resetService;

    @PostMapping("/sendResetCode")
    @ResponseBody
    public String sendResetCode(@RequestParam String userName) {
        // System.out.println(1111111111);
        resetService.sendResetCode(userName);
        return "验证码已发送，请查收邮箱";
    }

    @PostMapping("/resetPassword")
    @ResponseBody   // 如果你想返回字符串；如果想跳转页面就去掉并 return redirect
    public String resetPassword(@RequestParam String userName,
                                @RequestParam String code,
                                @RequestParam String newPassword) {
        resetService.resetPassword(userName, code, newPassword);
        return "密码修改成功";
    }
}
