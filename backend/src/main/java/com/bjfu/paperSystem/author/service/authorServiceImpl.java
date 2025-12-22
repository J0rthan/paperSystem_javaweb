package com.bjfu.paperSystem.author.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.dao.VersionsDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Versions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class authorServiceImpl implements authorService {

    @Autowired
    private ManuscriptDao manuscriptRepo;

    @Autowired
    private VersionsDao versionsRepo;

    @Override
    @Transactional
    public void submitPaper(Manuscript ms, Versions ver, MultipartFile file) {
        String uploadPath = "D:/paperSystem_files/";
        java.io.File folder = new java.io.File(uploadPath);
        if (!folder.exists()) folder.mkdirs();
        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.io.File dest = new java.io.File(folder, fileName);
            try {
                file.transferTo(dest);
                ver.setFilePathOriginal(dest.getAbsolutePath());
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        ms.setStatus("SUBMITTED");
        ms.setSubmitTime(java.time.LocalDateTime.now());
        manuscriptRepo.save(ms);
        ver.setManuscriptId(ms.getManuscriptId());
        ver.setVersionNumber(1);
        versionsRepo.save(ver);
    }
}