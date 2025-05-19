package dev.fitmart.FItMart.components.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "accounts")
public class Account {
    @Id
    private ObjectId id;
    // Getter and Setter
    @Indexed(unique = true)
    private String uuid;
    private String user_name;
    private String email;
    private String password;
    private String avatar_url;
    private String address;
    private String phone;
    private Instant created_at;
    private Instant updated_at;
    private Instant deleted_at;
}
