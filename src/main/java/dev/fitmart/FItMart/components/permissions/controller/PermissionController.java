package dev.fitmart.FItMart.components.permissions.controller;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.permissions.mapping.PermissionRequest;
import dev.fitmart.FItMart.components.permissions.mapping.PermissionResponse;
import dev.fitmart.FItMart.components.permissions.service.PermissionService;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/permissions")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.createPermission(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable String id) {
        PermissionResponse response = permissionService.getPermission(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Paginated<List<PermissionResponse>>> getAllPermissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams
    ) {
        int createdAtSort = (created_at == -1 || created_at == 1 || created_at == 0) ? created_at : -1;

        // Create filter map from dynamic query parameters
        Map<String, String> filters = new HashMap<>();

        // Add filterField and filterValue if provided (for backwards compatibility)
        if (filterField != null && filterValue != null) {
            filters.put(filterField, filterValue);
        }

        // Add other query parameters as filters, excluding reserved ones
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("page") && !key.equals("limit") && !key.equals("q") &&
                    !key.equals("created_at") && !key.equals("filterField") && !key.equals("filterValue")) {
                filters.put(key, entry.getValue());
            }
        }

        Paginated<List<PermissionResponse>> response = permissionService.getAllPermissions(page, limit, filters, q, createdAtSort);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable String id, @Valid @RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
