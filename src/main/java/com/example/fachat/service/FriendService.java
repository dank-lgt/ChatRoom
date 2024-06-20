package com.example.fachat.service;

import com.example.fachat.entity.Friend;
import com.example.fachat.mapper.FriendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    @Autowired
    private FriendMapper friendMapper;
    public List<Friend> selectFriendList(int userId){
        return friendMapper.selectFriendList(userId);
    }

    public void addFriend(int userId,int friendId){
        friendMapper.addFriend(userId, friendId);
    }
}
