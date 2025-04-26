package dev.fitmart.FItMart.model;

import dev.fitmart.FItMart.components.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    private ObjectId id;
    private String uuid;                // FMn4k9x2r5
    private String phone;            // 0987654321
    private String addressLine1;     // 15 ngoc hoi
    private String addressLine2;     // 54 ngoc hoi
    private String city;             // Hà Nội
    private String province;         // Hà Nội
    private String postalCode;      // 100000
    private String country;         // VN
    private String createdAt;      // 2025-04-21T10:00:00Z
    private String updatedAt;      // 2025-04-21T10:00:00Z
    private String deletedAt;
    private List<UserModel> userModelId;
}
