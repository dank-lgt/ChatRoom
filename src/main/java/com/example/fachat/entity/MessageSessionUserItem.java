package com.example.fachat.entity;

import lombok.Data;

//使用这个类表示message_session_user表
@Data
public class MessageSessionUserItem {
    private int sessionId;
    private int userId;
}
