package com.example.fachat.common;

import lombok.Data;

@Data
public class FriReqResp {
    private String type = "FriendRequest";
    private int fromId;
    private String fromName;
    private int friendId;
    private String reason;
}
