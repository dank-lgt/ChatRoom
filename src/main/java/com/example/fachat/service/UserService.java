package com.example.fachat.service;

import com.example.fachat.entity.User;
import com.example.fachat.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public User login(String username){
        return userMapper.selectByName(username);
    }

    public int reg(User user){
        return userMapper.insert(user);
    }

    public List<User> searchUser(String search ,int userId){
        return userMapper.searchUser(search,userId);
    }

    public int updatePhotoById(int userId,String avatar){
        return  userMapper.updatePhotoById(userId,avatar);
    }

    public User getAvatarByUseId(int userId){
        return userMapper.getAvatarByUseId(userId);
    }
}
