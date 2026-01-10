package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.javabeans.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.io.IOException;
public interface authorService {
    Map<String, List<Manuscript>> getCategorizedManuscripts(int authorId);
    void fullSubmit(Manuscript manuscript, String action, User user);
    Manuscript getManuscriptByIdAndAuthor(int manuscriptId, int authorId);
    void deleteManuscript(int manuscriptId);
    User getUserById(int userId);
    String updateProfile(User user, int loginUserId);
    User findUserById(int userId);
    List<Versions> getVersionsByManuscriptId(int manuscriptId);
    void submitRevision(int manuscriptId,
                        MultipartFile cleanFile,
                        MultipartFile markedFile,
                        MultipartFile replyFile,
                        String responseText,
                        User user) throws IOException;
}