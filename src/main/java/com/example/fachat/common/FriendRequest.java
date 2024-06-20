package com.example.fachat.common;

import lombok.Data;

@Data
public class FriendRequest  extends Request{
    private String type = "FriendRequest";
    private int friendId;
    private String reason;
}
