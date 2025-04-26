package dev.fitmart.FItMart.components.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private String uuid;
    private String user_name;
    private String email;
    private String password;
}
