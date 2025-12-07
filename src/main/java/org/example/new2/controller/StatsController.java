package org.example.new2.controller;
import jakarta.servlet.http.HttpServletResponse;
import org.example.new2.common.Result;
import org.example.new2.service.StatsService;
import org.example.new2.vo.WeeklyExportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    /**
     * 1. 获取周报表数据 (JSON格式，用于前端展示)
     */
    @GetMapping("/weekly/{taskId}")
    public Result<List<WeeklyExportVO>> getWeeklyData(@PathVariable Long taskId) {
        return Result.success(statsService.getWeeklyData(taskId));
    }

    /**
     * 2. 导出 Excel (文件流下载)
     */
    @GetMapping("/weekly/{taskId}/export")
    public void exportExcel(@PathVariable Long taskId, HttpServletResponse response) throws IOException {
        statsService.exportToExcel(taskId, response);
    }

}
