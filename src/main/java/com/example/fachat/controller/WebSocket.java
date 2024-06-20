package com.example.fachat.controller;

import com.example.fachat.common.*;
import com.example.fachat.entity.Friend;
import com.example.fachat.entity.FriendRequests;
import com.example.fachat.entity.Message;
import com.example.fachat.entity.User;
import com.example.fachat.service.FriendRequestsService;
import com.example.fachat.service.MessageService;
import com.example.fachat.service.MessageSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
public class WebSocket extends TextWebSocketHandler {
    //此处的Hash要考虑线程安全问题
    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private MessageSessionService messageSessionService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FriendRequestsService friendRequestsService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket连接成功");
        User user = (User) session.getAttributes().get(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return;
        }
        //存下session
//        System.out.println("userId = "+user.getUserId());
        onlineUserManager.online(user.getUserId(),session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("WebSocket收到消息 "+message.toString());
        //session里面已经记录互相通信的双方 处理消息的接受，转发，以及消息的保存记录
        User user = (User) session.getAttributes().get(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            System.out.println("[WebSocketAPI] user == null! 未登录用户, 无法进行消息转发");
            return;
        }
        Request request = objectMapper.readValue(message.getPayload(), Request.class);
        if (request.getType().equals("message") ) {
            MessageRequest messageRequest = objectMapper.readValue(message.getPayload(), MessageRequest.class);
            transferMessage(user, messageRequest);
        } else if (request.getType().equals("FriendRequest")) {
            FriendRequest friendRequest =  objectMapper.readValue(message.getPayload(), FriendRequest.class);
//            System.out.println(friendRequest);
            transferFriReq(user, friendRequest);
        } else {
            System.out.println("[WebSocketAPI] req.type 有误! " + message.getPayload());
        }
    }


    // 通过这个方法来完成好友请求的工作.
    private void transferFriReq(User user, FriendRequest friendRequest) throws IOException {
        FriReqResp friReqResp = new FriReqResp();
        friReqResp.setType("FriendRequest");
        friReqResp.setFromId(user.getUserId());
        friReqResp.setFromName(user.getUsername());
        //这是要添加用户的userId
        friReqResp.setFriendId(friendRequest.getFriendId());
        friReqResp.setReason(friendRequest.getReason());

        String respJson = objectMapper.writeValueAsString(friReqResp);
        System.out.println("[transferFriReq] respJson: " + respJson);
        //如果数据库存在同一个发送请求：发送人  和  接收人 重复  就只需更新 reason
        if (friendRequestsService.selectRepeatId(user.getUserId(), friReqResp.getFriendId()) > 0){
            friendRequestsService.updateReason(user.getUserId(),friReqResp.getFriendId(),friendRequest.getReason());
        }else {
            //向好友请求表  保存好友请求信息
            friendRequestsService.addFriendRequest(user.getUserId(), friendRequest.getFriendId(), friendRequest.getReason());
        }
        WebSocketSession webSocketSession = onlineUserManager.getSession(friendRequest.getFriendId());
        if (webSocketSession == null){
            // 如果该用户未在线, 则不发送.
            return;
        }
        webSocketSession.sendMessage(new TextMessage(respJson));

    }

    // 通过这个方法来完成消息实际的转发工作.
    // 第一个参数就表示这个要转发的消息, 是从谁那来的.
    private void transferMessage(User fromUser, MessageRequest req) throws IOException {
        // 先构造一个待转发的响应对象. MessageResponse
        MessageResponse response =  new MessageResponse();
        response.setType("message");
        response.setFromId(fromUser.getUserId());
        response.setFromName(fromUser.getUsername());
        response.setSessionId(req.getSessionId());
        response.setContent(req.getContent());

        String respJson = objectMapper.writeValueAsString(response);
        System.out.println("[transferMessage] respJson: " + respJson);

//        根据请求中的 sessionId, 获取到这个 MessageSession 里都有哪些用户. 通过查询数据库就能够知道了.
        List<Friend> friends = messageSessionService.getFriendsBySessionId(req.getSessionId(), fromUser.getUserId());
        // 此处注意!!! 上述数据库查询, 会把当前发消息的用户给排除掉. 而最终转发的时候, 则需要也把发送消息的人也发一次.
        // 把当前用户也添加到上述 List 里面
        Friend mine = new Friend();
        mine.setFriendId(fromUser.getUserId());
        mine.setFriendName(fromUser.getUsername());
        friends.add(mine);

//        循环遍历上述的这个列表, 给列表中的每个用户都发一份响应消息
        //    注意: 这里除了给查询到的好友们发, 也要给自己也发一个. 方便实现在自己的客户端上显示自己发送的消息.
        //    注意: 一个会话中, 可能有多个用户(群聊). 虽然客户端是没有支持群聊的(前端写起来相对麻烦), 后端无论是 API 还是 数据库
        //    都是支持群聊的. 此处的转发逻辑也一样让它支持群聊.
        for (Friend friend :friends){
            WebSocketSession webSocketSession = onlineUserManager.getSession(friend.getFriendId());
            if (webSocketSession == null){
                // 如果该用户未在线, 则不发送.
                continue;
            }
            webSocketSession.sendMessage(new TextMessage(respJson));
        }

        Message message = new Message();
        message.setFromId(fromUser.getUserId());
        message.setSessionId(req.getSessionId());
        message.setContent(req.getContent());
        messageService.add(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("WebSocket连接异常"+exception.toString());
        User user = (User) session.getAttributes().get(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return;
        }
        onlineUserManager.offline(user.getUserId(),session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket连接关闭"+status.toString());
        User user = (User) session.getAttributes().get(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return;
        }
        onlineUserManager.offline(user.getUserId(), session);
    }
}
