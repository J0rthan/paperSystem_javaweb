package com.bjfu.paperSystem.clientMessageUtils;

import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class test {
    @Autowired
    clientMessageService cService;

    @GetMapping("/test1")
    @ResponseBody
    public String insert() {
        cService.insertMessage(15, 10, "hello", LocalDateTime.now(), 8);

        return "ok";
    }
}
