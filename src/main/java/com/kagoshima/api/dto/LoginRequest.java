package com.kagoshima.api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private String password;
}
