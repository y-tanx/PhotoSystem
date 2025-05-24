package com.picture.service;
import com.picture.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    /**
     * 用户登录
     * @param user
     * @return
     */
    Integer selectUserId(User user);
}
