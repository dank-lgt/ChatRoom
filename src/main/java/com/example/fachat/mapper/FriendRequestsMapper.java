package com.example.fachat.mapper;

import com.example.fachat.entity.FrRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface FriendRequestsMapper {
    //添加好友请求信息
    void addFriendRequest(Integer senderId, Integer receiverId, String reason);

    //搜寻好友请求
    List<FrRequest> selectFriendRequest(int receiverId);

    //判断是否有重复的id用户发送
    int selectRepeatId(int senderId,int receiverId);

    //更新好友请求
    void updateReason(int senderId,int receiverId,String reason);


    //已同意添加好友  修改好友请求信息
    void updateAddFriReq(int senderId,int receiverId);

    void upRejectFriReq(int senderId,int receiverId);


}
