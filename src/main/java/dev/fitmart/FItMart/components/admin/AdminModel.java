package dev.fitmart.FItMart.components.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminModel {
    @Id
    private ObjectId id;
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
