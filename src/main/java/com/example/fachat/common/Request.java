package com.example.fachat.common;

import lombok.Data;

@Data
public class Request {
    private String type;
    private int sessionId;
    private int friendId;
    private String reason;
    private String content;
}
