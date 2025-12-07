package org.example.new2.controller;

import org.example.new2.entity.Admin;
import org.example.new2.dto.RegisterDTO;
import org.example.new2.dto.LoginDTO;
import org.example.new2.entity.ResponseMessage;
import org.example.new2.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin") // ✅ 统一使用 /api/admin 前缀
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @PostMapping("/register")
    public ResponseMessage<Admin> register(@Validated @RequestBody RegisterDTO dto) {
        return adminService.register(dto);
    }

    @PostMapping("/login")
    public ResponseMessage<String> login(@Validated @RequestBody LoginDTO dto) {
        return adminService.login(dto);
    }
}