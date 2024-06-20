package com.example.fachat.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MessageSession implements Serializable {
    private int sessionId;
    private List<Friend> friends;
    private String lastMessage;
}
