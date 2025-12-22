package com.bjfu.paperSystem.author.service;

public interface logService {
    void record(Integer userId, String action, Integer manuscriptId);
}