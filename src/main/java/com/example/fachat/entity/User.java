package com.example.fachat.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User  implements Serializable {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private LocalDateTime created_at;
}
