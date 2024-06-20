package com.example.fachat.service;

import com.example.fachat.entity.Message;
import com.example.fachat.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    public String getLastMessageBySessionId(int sessionId){
        return messageMapper.getLastMessageBySessionId(sessionId);
    }

    public List<Message> getMessage(int sessionId,int limit){
        return messageMapper.getMessageBySessionId(sessionId, limit);
    }

    public void add(Message message){
        messageMapper.add(message);
    }
}
