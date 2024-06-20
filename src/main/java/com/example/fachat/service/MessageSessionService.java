package com.example.fachat.service;

import com.example.fachat.entity.Friend;
import com.example.fachat.entity.MessageSession;
import com.example.fachat.entity.MessageSessionUserItem;
import com.example.fachat.mapper.MessageSessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageSessionService {
    @Autowired
    private MessageSessionMapper messageSessionMapper;

    public List<Integer> getSessionIdByUserId(int userId){
        return messageSessionMapper.getSessionIdByUserId(userId);
    }

    public  List<Friend> getFriendsBySessionId(int SessionId, int selfId){
        return messageSessionMapper.getFriendsBySessionId(SessionId,selfId);
    }

    public int addMessageSession(MessageSession messageSession){
        return messageSessionMapper.addMessageSession(messageSession);
    }

    public void addMessageSessionUser(MessageSessionUserItem messageSessionUserItem){
        messageSessionMapper.addMessageSessionUser(messageSessionUserItem);
    }

//    public int getSessionId(){
//        return messageSessionMapper.getSessionId();
//    }
}
