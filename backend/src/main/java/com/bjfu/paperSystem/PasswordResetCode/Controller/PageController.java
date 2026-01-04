package com.bjfu.paperSystem.PasswordResetCode.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/passwordreset")
public class PageController {
    @GetMapping("mainpage")
    public String toPage(
            @RequestParam("loginUserName") String userName,
            Model model
    ) {
        model.addAttribute("userName", userName);
        return "/PasswordReset/mainpage";
    }
}
