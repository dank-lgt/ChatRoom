package com.example.fachat.mapper;

import com.example.fachat.entity.Friend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<Friend> selectFriendList(int userId);

    //添加好友
    void addFriend(int userId,int friendId);

}
