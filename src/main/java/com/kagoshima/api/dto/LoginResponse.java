package com.kagoshima.api.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String code;
    private String name;
    private String role;
    private String email;
    private String department;
    private LocalDateTime loginAt;
}
