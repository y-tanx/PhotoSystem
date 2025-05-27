package com.picture.service;
import com.picture.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    /**
     * 注册用户
     * @param user
     * @return
     */
    Integer addUser(User user);
    /**
     * 用户登录
     * @param user
     * @return
     */
    Integer selectUserId(User user);
    /**
     * 查询用户资料
     * @param id
     * @return
     */
    User selectUserById(int id);
    /**
     * 修改用户资料
     * @param user
     */
    void updateUser(User user);

    /**
     * 查询用户名是否存在
     *
     * @param UserName 用户名
     * @return
     */
    String selectUserName(String UserName);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 是否修改成功
     */
    boolean resetPassword(User user);

}
