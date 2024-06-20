package com.example.fachat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private int messageId;
    private int fromId;
    private String fromName;
    private int sessionId;
    private String content;
    private int status;
    @JsonFormat(pattern = "yyyy-MMMM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime postTime;
}
