package dev.fitmart.FItMart.components.user.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String uuid;
    private String user_name;
    private String email;
    private String role;
    private List<String> permissions;
    private String avatar_url;
    private Instant created_at;
    private Instant updated_at;
    private Instant deleted_at;
    private Map<String, String> metadata;
}
