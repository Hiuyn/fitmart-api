package dev.fitmart.FItMart.components.address.model;

import dev.fitmart.FItMart.components.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    private ObjectId id;
    private String uuid;                // FMn4k9x2r5
    private String phone;               // 0987654321
    @Field("account_id")
    private String accountId;
    @Field("address_line1")
    private String addressLine1;     // 15 ngoc hoi
    @Field("address_line2")
    private String addressLine2;     // 54 ngoc hoi
    private String city;             // Hà Nội
    private String province;         // Hà Nội
    @Field("postal_code")
    private String postalCode;      // 100000
    private String country;         // VN
    @Field("created_at")
    private LocalDateTime createdAt;      // 2025-04-21T10:00:00Z
    @Field("updated_at")
    private LocalDateTime updatedAt;      // 2025-04-21T10:00:00Z
    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
