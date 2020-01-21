package com.wg.blog.service;

import com.wg.blog.model.User;
import org.springframework.stereotype.Service;

// @Service
public interface UserService {
    User getUserByUsernameAndPassword(String username,String password);
    User getUserByEmail(String email);
    User getUserByName(String username);
    int insert(User user);

    int update(User user);
}
