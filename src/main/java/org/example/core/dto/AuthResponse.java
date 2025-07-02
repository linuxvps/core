// 📁 org/example/core/dto/AuthResponse.java
package org.example.core.dto;

public class AuthResponse {
    private String jwt;

    public AuthResponse() {}

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}