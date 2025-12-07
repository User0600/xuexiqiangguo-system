package org.example.new2.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.new2.dto.*;
import org.example.new2.entity.ResponseMessage;
import org.example.new2.entity.User;
import org.example.new2.mapper.UserMapper;
import org.example.new2.service.IUserService;
import org.example.new2.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 核心用户业务实现类
 * 继承 ServiceImpl 自动获得 MP 的基础 CRUD 能力
 * 实现 IUserService 获得自定义业务能力
 */
//@Service
// ✅ 修改后 (指定 Bean 名称)
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // --- 1. 创建用户 (管理员用) ---
    @Override
    @Transactional
    public ResponseMessage<User> createUser(User user) {
        try {
            if (!StringUtils.hasText(user.getUsername())) return ResponseMessage.error("用户名不能为空");
            if (!StringUtils.hasText(user.getPassword())) return ResponseMessage.error("密码不能为空");

            if (this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())) > 0) {
                return ResponseMessage.error("用户名已存在");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 默认头像
            if (!StringUtils.hasText(user.getAvatar())) {
                user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + user.getUsername());
            }
            if (user.getAdminKey() == null) user.setAdminKey("N");

            this.save(user); // MP 保存

            user.setPassword(null);
            return ResponseMessage.success(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.error("创建失败: " + e.getMessage());
        }
    }

    // --- 2. 注册 ---
    @Override
    public ResponseMessage<User> register(RegisterDTO dto) {
        if (this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())) > 0) {
            return ResponseMessage.error("用户名已存在");
        }
        if (this.count(new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())) > 0) {
            return ResponseMessage.error("手机号已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAdminKey("");
        user.setAvatar(dto.getAvatar());

        this.save(user);
        return ResponseMessage.success(user);
    }

    // --- 3. 登录 ---
    @Override
    public ResponseMessage<String> login(LoginDTO dto) {
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseMessage.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getUsername(), "USER");
        return ResponseMessage.success(token);
    }

    // --- 4. 分页查询 ---
    @Override
    public ResponseMessage<IPage<User>> getUsers(String keyword, Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getPhone, keyword));
        }
        IPage<User> userPage = this.page(pageParam, wrapper);
        return ResponseMessage.success(userPage);
    }

    // --- 5. 获取详情 ---
    @Override
    public ResponseMessage<User> getUserById(Long id) {
        User user = this.getById(id);
        return user != null ? ResponseMessage.success(user) : ResponseMessage.error("用户不存在");
    }

    // --- 6. 编辑用户 (管理员) ---
    @Override
    @Transactional
    public ResponseMessage<User> editUser(User user) {
        if (this.getById(user.getId()) == null) {
            return ResponseMessage.error("用户不存在");
        }
        this.updateById(user);
        return ResponseMessage.success(this.getById(user.getId()));
    }

    // --- 7. 删除用户 ---
    @Override
    @Transactional
    public ResponseMessage<Void> deleteUser(Long id) {
        if (this.getById(id) == null) {
            return ResponseMessage.error("用户不存在");
        }
        this.removeById(id);
        return ResponseMessage.success(null);
    }

    // --- 8. 辅助查找 ---
    @Override
    public User findById(Long id) {
        return this.getById(id);
    }

    @Override
    public User findByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    // --- 🔥 9. 修改个人资料 (新功能) ---
    @Override
    public ResponseMessage<User> updateProfile(Long userId, UserProfileDTO dto) {
        User user = this.getById(userId);
        if (user == null) return ResponseMessage.error("用户不存在");

        if (StringUtils.hasText(dto.getUsername())) user.setUsername(dto.getUsername());
        if (StringUtils.hasText(dto.getPhone())) user.setPhone(dto.getPhone());
        if (StringUtils.hasText(dto.getEmail())) user.setEmail(dto.getEmail());
        if (StringUtils.hasText(dto.getAvatar())) user.setAvatar(dto.getAvatar());

        this.updateById(user);
        user.setPassword(null);
        return ResponseMessage.success(user);
    }

    // --- 🔥 10. 修改密码 (新功能) ---
    @Override
    public ResponseMessage<String> changePassword(Long userId, ChangePasswordDTO dto) {
        User user = this.getById(userId);
        if (user == null) return ResponseMessage.error("用户不存在");

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return ResponseMessage.error("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
        return ResponseMessage.success("密码修改成功，请重新登录");
    }
}
