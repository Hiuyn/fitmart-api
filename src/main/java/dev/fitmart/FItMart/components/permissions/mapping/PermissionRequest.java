package dev.fitmart.FItMart.components.permissions.mapping;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class PermissionRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
}
