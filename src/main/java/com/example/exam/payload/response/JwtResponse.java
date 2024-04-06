package com.example.exam.payload.response;

import lombok.Data;
import lombok.Generated;

import java.util.List;

@Generated
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;

    public JwtResponse(String token, Long id, String username, String email, List<String> roles, List<String> permissions) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }
}
