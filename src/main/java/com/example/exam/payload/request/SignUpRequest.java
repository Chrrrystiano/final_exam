package com.example.exam.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Generated;

import java.util.Set;

@Generated
@Data
public class SignUpRequest {
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> roles;

    private Set<String> permissions;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
