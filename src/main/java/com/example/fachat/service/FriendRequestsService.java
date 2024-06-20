package com.example.fachat.service;

import com.example.fachat.entity.FrRequest;
import com.example.fachat.mapper.FriendRequestsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class FriendRequestsService {
    @Autowired
    private FriendRequestsMapper friendRequestsMapper;

    public void addFriendRequest(Integer senderId, Integer receiverId, String reason){
        friendRequestsMapper.addFriendRequest(senderId, receiverId, reason);
    }

    public List<FrRequest> selectFriendRequest(int receiverId){
        return friendRequestsMapper.selectFriendRequest(receiverId);
    }

    public int selectRepeatId(int senderId,int receiverId){
        return friendRequestsMapper.selectRepeatId(senderId,receiverId);
    }

    public void updateReason(int senderId,int receiverId,String reason){
        friendRequestsMapper.updateReason(senderId, receiverId, reason);
    }

    public void  updateAddFriReq(int senderId,int receiverId){
        friendRequestsMapper.updateAddFriReq(senderId, receiverId);
    }
    public void  upRejectFriReq(int senderId,int receiverId){
        friendRequestsMapper.upRejectFriReq(senderId, receiverId);
    }
}
