package dev.fitmart.FItMart.components.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.*;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {
    @Id
    private ObjectId id;
    // Getter and Setter
    @Indexed(unique = true)
    private String uuid;
    private String user_name;
    private String email;
    private String password;
    private String role;
    private String avatar_url;
    private Instant created_at;
    private Instant updated_at;
    private Instant deleted_at;
    private List<Object> metadata;
}
