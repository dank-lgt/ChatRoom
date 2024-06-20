package com.example.fachat.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRequests {
    private int requestId;
    private int senderId;
    private int receiverId;
    private String reason;
    private String status ;
    private LocalDateTime request_at;
}
