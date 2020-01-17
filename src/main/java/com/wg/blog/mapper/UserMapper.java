package com.wg.blog.mapper;

import com.wg.blog.model.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //根据用户名和密码获取用户
    User getUserByUsernameAndPassword(String username,String password);
}
