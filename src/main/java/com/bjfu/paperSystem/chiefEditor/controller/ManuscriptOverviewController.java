package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.ManuscriptOverviewService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class ManuscriptOverviewController {

    @Autowired
    private ManuscriptOverviewService service;

    // 1. 页面展示与筛选
    @GetMapping("/manuscripts")
    public String manuscriptsOverview(
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session, Model model) {

        // 调用 Service 根据状态和关键字筛选
        List<Manuscript> list = service.getManuscriptsByStatus(status, keyword);

        model.addAttribute("manuscripts", list);
        model.addAttribute("currentStatus", status); // 回显当前选中的状态
        model.addAttribute("keyword", keyword); // 回显搜索关键字

        return "chiefeditor/manuscripts";
    }

    // 2. CSV 导出功能
    @GetMapping("/manuscripts/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        // 获取所有稿件（导出通常是导出全部，也可以改为按条件导出）
        List<Manuscript> list = service.getAllManuscripts();

        // 设置响应头，告诉浏览器这是一个要下载的文件
        response.setContentType("text/csv; charset=UTF-8");
        String fileName = URLEncoder.encode("稿件统计报表_" + System.currentTimeMillis() + ".csv", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 使用 OutputStreamWriter 并指定 UTF-8
        try (OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {

            // !!! 写入 BOM (Byte Order Mark) 防止 Excel 打开乱码 !!!
            osw.write('\ufeff');

            // 写入表头
            writer.println("稿件ID,标题,作者ID,编辑ID,当前状态,提交时间,最终决策");

            // 写入数据行
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Manuscript m : list) {
                String timeStr = m.getSubmitTime() != null ? m.getSubmitTime().format(dtf) : "";

                // 处理可能含有英文逗号的字段，用双引号包裹，并将内部双引号转义
                String title = m.getTitle() != null ? "\"" + m.getTitle().replace("\"", "\"\"") + "\"" : "";
                String decision = m.getDecision() != null ? "\"" + m.getDecision().replace("\"", "\"\"") + "\"" : "";
                String status = translateStatus(m.getStatus()); // 简单翻译一下状态方便阅读

                writer.printf("%d,%s,%d,%s,%s,%s,%s%n",
                        m.getManuscriptId(),
                        title,
                        m.getAuthorId(),
                        m.getEditorId() == null ? "" : m.getEditorId(),
                        status,
                        timeStr,
                        decision
                );
            }
        }
    }

    // 简单的辅助方法：导出时将英文状态转中文
    private String translateStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "Started Submission" -> "开始投稿";
            case "Incomplete Submission" -> "未提交草稿";
            case "Pending Review" -> "待审查";
            case "Pending Allocation" -> "待分配";
            case "With Editor" -> "编辑处理中";
            case "Under Review" -> "审稿中";
            case "Rejected" -> "拒稿";
            case "Accepted" -> "录用";
            case "Need Revision" -> "需要返修";
            default -> status;
        };
    }
    
    // 3. 稿件详情页面
    @GetMapping("/manuscripts/detail")
    public String manuscriptDetail(@RequestParam int id, Model model) {
        Manuscript manuscript = service.getManuscriptDetail(id);
        if (manuscript == null) {
            return "redirect:/chiefeditor/manuscripts";
        }
        model.addAttribute("manuscript", manuscript);
        return "chiefeditor/manuscript-detail";
    }
}