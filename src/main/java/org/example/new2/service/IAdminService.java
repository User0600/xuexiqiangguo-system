package org.example.new2.service;

import org.example.new2.entity.Admin;
import org.example.new2.dto.LoginDTO;
import org.example.new2.dto.RegisterDTO;
import org.example.new2.entity.ResponseMessage;

public interface IAdminService {
    ResponseMessage<Admin> register(RegisterDTO dto);
    ResponseMessage<String> login(LoginDTO dto);
    Admin findByUsername(String username); // 供 JwtFilter 调用
}