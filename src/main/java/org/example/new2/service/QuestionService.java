package org.example.new2.service;

import org.example.new2.entity.QuestionBank;
import org.example.new2.mapper.QuestionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class QuestionService extends ServiceImpl<QuestionMapper,QuestionBank> implements IQuestionService {
    // ServiceImpl 已经实现了大部分基础逻辑
}
