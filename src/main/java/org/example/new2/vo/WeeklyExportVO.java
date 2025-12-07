package org.example.new2.vo;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.util.Date;
import lombok.Data;

@Data
public class WeeklyExportVO {
    @ExcelProperty("用户ID")
    @ColumnWidth(15)
    private Long userId;

    @ExcelProperty("姓名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty("任务标题")
    @ColumnWidth(30)
    private String taskTitle;

    @ExcelProperty("时事得分")
    private Integer scoreNews;

    @ExcelProperty("专业得分")
    private Integer scorePro;

    @ExcelProperty("政治得分")
    private Integer scorePolitics;

    @ExcelProperty("总分")
    private Integer totalScore;

    @ExcelProperty("完成状态")
    @ColumnWidth(15)
    private String statusStr; // 给Excel看的文本：已完成/进行中

    @ExcelProperty("完成时间")
    @ColumnWidth(25)
    private Date finishTime;

    // 忽略这个字段，不导出到Excel，仅用于代码逻辑判断
    @ExcelIgnore
    private Integer rawStatus;
}
