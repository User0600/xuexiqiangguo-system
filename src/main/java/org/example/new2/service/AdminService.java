package org.example.new2.service; // ✅ 修正包路径

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.new2.mapper.AdminMapper;
import org.example.new2.entity.Admin;
import org.example.new2.dto.LoginDTO;
import org.example.new2.dto.RegisterDTO;
import org.example.new2.entity.ResponseMessage;
import org.example.new2.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService implements IAdminService {
    @Autowired
    private AdminMapper adminMapper; // 替换 Repository

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public ResponseMessage<Admin> register(RegisterDTO dto) {
        // 检查用户名
        Long count = adminMapper.selectCount(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getUsername()));

        if (count > 0) {
            return ResponseMessage.error("管理员账号已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setPhone(dto.getPhone());
        admin.setEmail(dto.getEmail());
        admin.setAdminKey("Y");

        adminMapper.insert(admin);
        return ResponseMessage.success(admin);
    }

    @Override
    public ResponseMessage<String> login(LoginDTO dto) {
        // 查询 Admin
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getUsername()));

        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            return ResponseMessage.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");
        return ResponseMessage.success(token);
    }

    @Override
    public Admin findByUsername(String username) {
        return adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username));
    }
}