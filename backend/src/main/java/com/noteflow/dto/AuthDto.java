package com.noteflow.dto;

import lombok.Data;

public class AuthDto {

    @Data
    public static class SendOtpRequest {
        private String email;
    }

    @Data
    public static class VerifyOtpRequest {
        private String email;
        private String otp;
        private String name;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private UserDto user;
        private boolean isNewUser;
        public boolean getIsNewUser() { return isNewUser; }
    }
}
