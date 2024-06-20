package com.example.fachat.controller;

import com.example.fachat.common.AjaxResult;
import com.example.fachat.common.ApplicationVariable;
import com.example.fachat.entity.FrRequest;
import com.example.fachat.entity.MessageSession;
import com.example.fachat.entity.MessageSessionUserItem;
import com.example.fachat.entity.User;
import com.example.fachat.service.FriendRequestsService;
import com.example.fachat.service.FriendService;
import com.example.fachat.service.MessageSessionService;
import com.example.fachat.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendRequestsController {
    @Autowired
    FriendRequestsService friendRequestsService;

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendService;

    @Autowired
    MessageSessionService messageSessionService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("/selectfrireq")
    public AjaxResult selectFriendRequest(HttpServletRequest request){
        HttpSession session = request.getSession();
        if (session == null){
            return AjaxResult.fail(-1,"用户还未上线");
        }
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-1,"未检测到用户");
        }
        List<FrRequest> res = friendRequestsService.selectFriendRequest(user.getUserId());
        return AjaxResult.success(res);
    }

    @RequestMapping("/addFriendRequest")
    public AjaxResult addFriendRequest(HttpServletRequest request,String senderName){
        HttpSession session = request.getSession();
        if (session == null){
            return AjaxResult.fail(-1,"用户还未上线");
        }
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-1,"未检测到用户");
        }
        User sender = userService.login(senderName);
        System.out.println(sender);
        //修改好友请求   添加到朋友要执行2遍  执行message_session 表里插入记录
        friendRequestsService.updateAddFriReq(sender.getUserId(),user.getUserId());
        friendService.addFriend(sender.getUserId(), user.getUserId());
        friendService.addFriend(user.getUserId(), sender.getUserId());

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
        item2.setUserId(sender.getUserId());
        messageSessionService.addMessageSessionUser(item2);
        return AjaxResult.success(1);
    }

    @RequestMapping("/removeFriReq")
    public AjaxResult RemoveFriReq(HttpServletRequest request,String senderName){
        HttpSession session = request.getSession();
        if (session == null){
            return AjaxResult.fail(-1,"用户还未上线");
        }
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-2,"未检测到用户");
        }
        User sender = userService.login(senderName);
        friendRequestsService.upRejectFriReq(sender.getUserId(),user.getUserId());
        return AjaxResult.success(1);
    }
}
