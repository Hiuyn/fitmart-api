package dev.fitmart.FItMart.components.permissions.mapping;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class PermissionRequest {
    @NotBlank(message = "Name không được trống")
    private String name;
    private String description;
}
