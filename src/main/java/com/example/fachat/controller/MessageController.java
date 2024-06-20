package com.example.fachat.controller;

import com.example.fachat.common.AjaxResult;
import com.example.fachat.entity.Message;
import com.example.fachat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    @RequestMapping("/message")
    public AjaxResult getMessgage(int sessionId,int limit){
        List<Message> messageList = messageService.getMessage(sessionId,limit);
        //数据库查询出来的数据是降序排列（降序是为拿到最新的会话数据内容） 在此要将数据倒置 恢复成按时间升序排列
        Collections.reverse(messageList);
        return AjaxResult.success(messageList);
    }
}
