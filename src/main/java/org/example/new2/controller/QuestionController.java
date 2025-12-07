package org.example.new2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.new2.common.Result;
import org.example.new2.dto.QuestionDTO;
import org.example.new2.entity.QuestionBank;
import org.example.new2.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
@CrossOrigin// 允许跨域，方便Vue调试
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    /**
     * 1. 提交题目 (员工/管理员)
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody QuestionDTO dto) {
        QuestionBank question = new QuestionBank();
        BeanUtils.copyProperties(dto, question);

        // 默认状态设为 0-待审核 (实际逻辑可判断当前用户若是管理员则直接设为1)
        question.setStatus(0);
        question.setIsDeleted(0);

        questionService.save(question);
        return Result.success("题目提交成功，等待审核");
    }

    /**
     * 2. 分页查询题目列表
     * @param page 页码
     * @param size 每页条数
     * @param category 分类 (可选)
     * @param status 状态 (可选)
     */
    @GetMapping("/list")
    public Result<Page<QuestionBank>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer status
    ) {
        // 构建分页参数
        Page<QuestionBank> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category != null, QuestionBank::getCategory, category)
                .eq(status != null, QuestionBank::getStatus, status)
                .orderByDesc(QuestionBank::getId); // 按ID倒序

        Page<QuestionBank> resultPage = questionService.page(pageParam, wrapper);
        return Result.success(resultPage);
    }

    /**
     * 3. 题目审核 (管理员)
     * @param id 题目ID
     * @param pass true-通过, false-驳回
     */
    @PutMapping("/audit/{id}")
    public Result<String> audit(@PathVariable Long id, @RequestParam Boolean pass) {
        QuestionBank question = questionService.getById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }

        if (pass) {
            question.setStatus(1); // 入库
        } else {
            question.setStatus(2); // 驳回
        }

        questionService.updateById(question);
        return Result.success(pass ? "审核通过" : "已驳回");
    }

    /**
     * 4. 删除题目 (逻辑删除)
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = questionService.removeById(id); // MP会自动处理逻辑删除
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 5. 查看题目详情
     */
    @GetMapping("/{id}")
    public Result<QuestionBank> detail(@PathVariable Long id) {
        return Result.success(questionService.getById(id));
    }
}
