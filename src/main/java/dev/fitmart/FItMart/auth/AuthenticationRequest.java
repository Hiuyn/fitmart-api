package dev.fitmart.FItMart.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotEmpty
    @NotNull
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotEmpty
    @NotNull
    @NotBlank(message = "Password must not be blank")
    private String password;
}
