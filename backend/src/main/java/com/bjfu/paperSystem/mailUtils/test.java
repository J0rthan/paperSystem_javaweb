package com.bjfu.paperSystem.mailUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class test {
    private final mailUtil mailUtil;

    public test(mailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    @GetMapping("/testMail")
    @ResponseBody
    public String testMail() {
        mailUtil.sendTextMail("1211083651@qq.com", "测试邮件", "MailUtil 注入成功");
        return "ok";
    }
}