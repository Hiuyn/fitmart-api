package dev.fitmart.FItMart.components.account.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private String uuid;
    private String user_name;
    private String email;
    private String avatar_url;
    private String address;
    private String phone;
    private Instant created_at;
    private Instant updated_at;
    private Instant deleted_at;
}
