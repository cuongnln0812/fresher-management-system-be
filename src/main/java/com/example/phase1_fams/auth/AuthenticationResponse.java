package com.example.phase1_fams.auth;

import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
    private String token;
    private String tokenType = "Bearer";
}