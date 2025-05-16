package dev.fitmart.FItMart.components.user.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private String uuid;
    private String user_name;
    private String email;
    private String password;
    private String role;
    private List<String> permissions;
    private String avatar_url;
}
