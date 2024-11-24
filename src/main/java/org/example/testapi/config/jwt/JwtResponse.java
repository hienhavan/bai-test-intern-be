package org.example.testapi.config.jwt;

import lombok.Getter;

@Getter
public class JwtResponse {
    private final int id;
    private final String token;
    private final String name;
    private final String email;
    private final String role;

    public JwtResponse(int id, String token,String email, String name, String role) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.email = email;
        this.role = role;
    }


}

