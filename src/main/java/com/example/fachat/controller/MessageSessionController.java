package com.example.fachat.controller;

import com.example.fachat.common.AjaxResult;
import com.example.fachat.common.ApplicationVariable;
import com.example.fachat.entity.Friend;
import com.example.fachat.entity.MessageSession;
import com.example.fachat.entity.MessageSessionUserItem;
import com.example.fachat.entity.User;
import com.example.fachat.mapper.MessageMapper;
import com.example.fachat.service.MessageService;
import com.example.fachat.service.MessageSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class MessageSessionController {
    @Autowired
    public MessageSessionService messageSessionService;

    @Autowired
    private MessageService messageService;

    @RequestMapping("sessionList")
    public AjaxResult getSessionList(HttpServletRequest request){
        List<MessageSession> messageSessionList = new ArrayList<>();
        HttpSession session = request.getSession();
        if (session == null){
            return AjaxResult.fail(-1,"当前会话不存在！");
        }
        User user  = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-2,"当前用户不存在！");
        }

        List<Integer> sessionList = messageSessionService.getSessionIdByUserId(user.getUserId());
        for (int sessionId : sessionList){
            MessageSession messageSession = new MessageSession();
            messageSession.setSessionId(sessionId);
            List<Friend> friends = messageSessionService.getFriendsBySessionId(sessionId,user.getUserId());
            messageSession.setFriends(friends);
//            遍历会话id, 查询出每个会话的最后一条消息
            String message = messageService.getLastMessageBySessionId(sessionId);
            if (message == null){
                messageSession.setLastMessage("");
            }else {
                messageSession.setLastMessage(message);
            }
            messageSessionList.add(messageSession);
        }
        return AjaxResult.success(messageSessionList);
    }

    @RequestMapping("/session")
    @ResponseBody
    @Transactional
    public  AjaxResult addMessageSession(int toUserId,
            @SessionAttribute(ApplicationVariable.SESSION_KEY_USERINFO) User user){
        HashMap<String,Integer> map = new HashMap<>();
        // 1. 先给 message_session 表里插入记录. 使用这个参数的目的主要是为了能够获取到会话的 sessionId
        //    换而言之, MessageSession 里的 friends 和 lastMessage 属性此处都用不上.
        MessageSession messageSession = new MessageSession();
        messageSessionService.addMessageSession(messageSession);
        System.out.println(messageSession.getSessionId());
//        int sessionId = messageSessionService.getSessionId();
//        System.out.println("==========="+sessionId);
        // 2. 给 message_session_user 表插入记录.
        MessageSessionUserItem item1 = new MessageSessionUserItem();
        item1.setSessionId(messageSession.getSessionId());
        item1.setUserId(user.getUserId());
        messageSessionService.addMessageSessionUser(item1);
        // 3. 给 message_session_user 表插入记录.
        MessageSessionUserItem item2 = new MessageSessionUserItem();
        item2.setSessionId(messageSession.getSessionId());
        item2.setUserId(toUserId);
        messageSessionService.addMessageSessionUser(item2);
        System.out.println("[addMessageSession] 新增会话成功! sessionId=" + messageSession.getSessionId()
                + " userId1=" + user.getUserId() + " userId2=" + toUserId);

        map.put("sessionId", messageSession.getSessionId());
        return AjaxResult.success(map);
    }

}
