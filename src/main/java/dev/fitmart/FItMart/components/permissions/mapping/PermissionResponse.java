package dev.fitmart.FItMart.components.permissions.mapping;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
