package dev.fitmart.FItMart.components.account.mapping;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequest {
    @NotBlank(message = "Username không được trống")
    @Size(min = 3, max = 50, message = "Username phải nằm trong khoảng từ 5 đến 50 ký tự")
    private String user_name;
    @NotBlank(message = "Email không được trống")
    @Email(message = "Email must be a valid format")
    private String email;
    @NotBlank(message = "Password không được trống")
    @Size(min = 6, max = 100, message = "Password phải nằm trong khoảng từ 6 đến 100 ký tự")
    private String password;
}
