package dev.fitmart.FItMart.components.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Username không được trống")
    @Size(min = 3, max = 50, message = "Username phải nằm trong khoảng từ 5 đến 50 ký tự")
    private String user_name;
    @NotBlank(message = "Email không được trống")
    @Email(message = "Email must be a valid format")
    private String email;
    @NotNull(message = "Password không được trống")
    private String password;
    private String role;
    private String avatar_url;
    private List<String> permissions;
    private Instant created_at;
    private Instant updated_at;
    private Instant deleted_at;
    private Map<String, String> metadata;
}
