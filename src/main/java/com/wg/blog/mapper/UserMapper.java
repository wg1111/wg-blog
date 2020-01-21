package com.wg.blog.mapper;

import com.wg.blog.model.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User user);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //根据用户名和密码获取用户
    User getUserByUsernameAndPassword(String username,String password);

    //根据邮箱查询数据库是否已有这个用户
    User getUserByEmail(String email);

    //根据用户名查询用户检查该用户名是否可用
    User getUserByName(String username);
}
