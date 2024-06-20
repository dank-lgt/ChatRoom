package com.example.fachat.common;

import lombok.Data;

@Data
public class MessageRequest extends Request {
    private String type= "message";
    private int sessionId;
    private String content;
}
