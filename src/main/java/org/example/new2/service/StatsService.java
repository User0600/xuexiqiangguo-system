package org.example.new2.service;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.example.new2.mapper.StatsMapper;
import org.example.new2.vo.WeeklyExportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StatsService {
    @Autowired
    private StatsMapper statsMapper;

    /**
     * 导出 Excel 到浏览器
     */
    public void exportToExcel(Long taskId, HttpServletResponse response) throws IOException {
        // 1. 查询数据
        List<WeeklyExportVO> data = statsMapper.selectWeeklyReport(taskId);

        // 2. 数据清洗 (把状态数字转成文字)
        for (WeeklyExportVO vo : data) {
            if (vo.getRawStatus() != null && vo.getRawStatus() == 1) {
                vo.setStatusStr("已完成");
            } else {
                vo.setStatusStr("进行中");
            }
            // 兜底处理：如果名字是空，显示未知
            if (vo.getUsername() == null) vo.setUsername("用户" + vo.getUserId());
        }

        // 3. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("周学习情况报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 4. 写出
        EasyExcel.write(response.getOutputStream(), WeeklyExportVO.class)
                .sheet("学习数据")
                .doWrite(data);
    }

    // 获取列表数据给前端展示用
    public List<WeeklyExportVO> getWeeklyData(Long taskId) {
        return statsMapper.selectWeeklyReport(taskId);
    }
}
