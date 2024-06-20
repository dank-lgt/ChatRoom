package com.example.fachat.common;

import com.example.fachat.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class OnlineUserManager {
    private ConcurrentHashMap<Integer,WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void online(int userId, WebSocketSession socketSession){
        if (sessions.get(userId) != null){
            // 此时说明用户已经在线了, 就登录失败, 不会记录这个映射关系.
            // 不记录这个映射关系, 后续就收不到任何消息 (毕竟, 咱们此处是通过映射关系来实现消息转发的)
            System.out.println("["+userId+"]用户已上线，登陆失败");
            return;
        }
        sessions.put(userId,socketSession);
        System.out.println("["+userId+"]用户上线成功！");
    }

    public void offline(int userId,WebSocketSession session){
        WebSocketSession existSession = sessions.get(userId);
        if (existSession == session){
            sessions.remove(userId);
            System.out.println("["+userId+"] 用户下线");
        }
    }

    //  根据 userId 获取到 WebSocketSession
    public WebSocketSession getSession(int userId){
        return sessions.get(userId);
    }
}
