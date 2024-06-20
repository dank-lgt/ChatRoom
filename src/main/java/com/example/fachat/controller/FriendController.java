package com.example.fachat.controller;

import com.example.fachat.common.AjaxResult;
import com.example.fachat.common.ApplicationVariable;
import com.example.fachat.entity.Friend;
import com.example.fachat.entity.User;
import com.example.fachat.service.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController {
    @Autowired
    private FriendService friendService;

    @RequestMapping("/friendList")
    public AjaxResult selectFriendList(HttpServletRequest request){
        HttpSession session = request.getSession();
        if (session == null){
            return AjaxResult.fail(-1,"当前会话不存在！");
        }
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-2,"当前用户不存在！");
        }
        List<Friend> friendList  = friendService.selectFriendList(user.getUserId());
        return AjaxResult.success(friendList);
    }
}
