package com.bjfu.paperSystem.author.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Versions;
import org.springframework.web.multipart.MultipartFile;

public interface authorService {
    void submitPaper(Manuscript ms, Versions ver, MultipartFile file );
}
