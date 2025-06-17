package dev.fitmart.FItMart.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse<T> {

    private String token;
    private T data;
}
