package dev.fitmart.FItMart.components.permissions.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.permissions.mapping.PermissionRequest;
import dev.fitmart.FItMart.components.permissions.mapping.PermissionResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;

import java.util.List;
import java.util.Map;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);
    PermissionResponse getPermission(String id);
    Paginated<List<PermissionResponse>> getAllPermissions(int page, int limit, Map<String, String> filters, String q, int createdAtSort);
    PermissionResponse updatePermission(String id, PermissionRequest request);
    void deletePermission(String id);
}
