package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.javabeans.User;
import java.util.Map;
public interface messageService {
    Map<String, Object> getAuthorMessageCenter(User loginUser);
}