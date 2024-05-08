package com.example.ex3.objects;

public class TokenResponse {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
    public TokenResponse(String accessToken){
        this.accessToken = accessToken;
    }
}
