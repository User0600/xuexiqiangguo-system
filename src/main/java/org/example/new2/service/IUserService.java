package org.example.new2.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage; // 引入 MP 的分页接口
import org.example.new2.dto.LoginDTO;
import org.example.new2.dto.RegisterDTO;
import org.example.new2.dto.UserProfileDTO;
import org.example.new2.dto.ChangePasswordDTO;
import org.example.new2.entity.ResponseMessage;
import org.example.new2.entity.User;

public interface IUserService extends IService<User> {
    User findById(Long id);
    ResponseMessage<User> register(RegisterDTO dto);
    ResponseMessage<String> login(LoginDTO dto);

    // 修改返回值类型：Page -> IPage
    ResponseMessage<IPage<User>> getUsers(String keyword, Integer page, Integer size);

    ResponseMessage<User> getUserById(Long id);
    ResponseMessage<User> editUser(User user);
    ResponseMessage<Void> deleteUser(Long id);
    User findByUsername(String username);
    ResponseMessage<User> createUser(User user);
    /**
     * 修改个人资料
     */
    ResponseMessage<User> updateProfile(Long userId, UserProfileDTO dto);

    /**
     * 修改密码
     */
    ResponseMessage<String> changePassword(Long userId, ChangePasswordDTO dto);
}