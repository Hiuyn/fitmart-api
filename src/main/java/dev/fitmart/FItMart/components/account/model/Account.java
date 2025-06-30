package dev.fitmart.FItMart.components.account.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Username không được trống")
    @Size(min = 3, max = 50, message = "Username phải nằm trong khoảng từ 5 đến 50 ký tự")
    private String user_name;
    @NotBlank(message = "Email không được trống")
    @Email(message = "Email must be a valid format")
    private String email;
    @NotNull(message = "Password không được trống")
    private String password;
    private String avatar_url;
    private String address;
    private String phone;
    private Instant created_at;
    private Instant updated_at;
    private Instant deleted_at;
}
