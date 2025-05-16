package dev.fitmart.FItMart.components.account.mapping;

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
    private String uuid;
    private String user_name;
    private String email;
    private String password;
}
