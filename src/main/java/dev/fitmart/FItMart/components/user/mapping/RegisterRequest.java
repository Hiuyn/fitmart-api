package dev.fitmart.FItMart.components.user.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private String uuid;

    private String user_name;

    private String email;

    private String password;
}
