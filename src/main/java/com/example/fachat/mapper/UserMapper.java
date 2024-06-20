package com.example.fachat.mapper;

import com.example.fachat.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    //注册
    int insert(User user);

    //登录
    User selectByName(@Param("username") String username);

    List<User> searchUser(String search,Integer userId);

    int updatePhotoById(Integer userId,String avatar);

    User getAvatarByUseId(Integer userId);
}
