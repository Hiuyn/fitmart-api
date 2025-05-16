package dev.fitmart.FItMart.components.permissions.service;


import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.permissions.mapping.PermissionRequest;
import dev.fitmart.FItMart.components.permissions.mapping.PermissionResponse;
import dev.fitmart.FItMart.components.permissions.model.Permission;
import dev.fitmart.FItMart.components.permissions.repository.PermissionRepository;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.exception.ApiException;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    @Autowired
    private FilterService filterService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse createPermission(PermissionRequest request) {
        if (permissionRepository.findByNameAndDeletedAtIsNull(request.getName()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Permission already exists: " + request.getName());
        }

        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());

        Permission savedPermission = permissionRepository.save(permission);
        return convertToResponse(savedPermission);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse getPermission(String id) {
        Permission permission = permissionRepository.findById(new ObjectId(id))
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Permission not found: " + id));
        return convertToResponse(permission);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Paginated<List<PermissionResponse>> getAllPermissions(int page, int limit, Map<String, String> filters, String q, int createdAtSort) {
        // Page is 0-based in Spring Data, but API uses 1-based
        Pageable pageable = limit == -1 ? PageRequest.of(0, Integer.MAX_VALUE) : PageRequest.of(page - 1, limit);

        List<Filter> filterCriteria = new ArrayList<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            Filter criteria = new Filter();
            criteria.setField(entry.getKey());
            criteria.setValue(entry.getValue());
            filterCriteria.add(criteria);
        }

        Page<Permission> permissionPage = filterService.applyFilter(Permission.class, "permisions", filterCriteria, pageable, q, limit, createdAtSort);

        List<PermissionResponse> permissionResponses = permissionPage.getContent().stream()
                .map(this::convertPermissionToResponse)
                .collect(Collectors.toList());

        Paginated<List<PermissionResponse>> response = new Paginated<>();
        response.setData(permissionResponses);

        Paginated.Pagination pagination = new Paginated.Pagination();
        pagination.setTotal(permissionPage.getTotalElements());
        pagination.setCount(permissionResponses.size());
        pagination.setPerPage(limit == -1 ? permissionResponses.size() : limit);
        pagination.setCurrentPage(limit == -1 ? 1 : page);
        pagination.setTotalPages(limit == -1 ? 1 : permissionPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    private PermissionResponse convertPermissionToResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(String.valueOf(permission.getId()));
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        response.setCreatedAt(permission.getCreatedAt());
        response.setUpdatedAt(permission.getUpdatedAt());
        response.setDeletedAt(permission.getDeletedAt());

        return response;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse updatePermission(String id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(new ObjectId(id))
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Permission not found: " + id));

        if (!permission.getName().equals(request.getName()) &&
                permissionRepository.findByNameAndDeletedAtIsNull(request.getName()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Permission name already exists: " + request.getName());
        }

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setUpdatedAt(LocalDateTime.now());

        Permission updatedPermission = permissionRepository.save(permission);
        return convertToResponse(updatedPermission);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePermission(String id) {
        Permission permission = permissionRepository.findById(new ObjectId(id))
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Permission not found: " + id));

        permission.setDeletedAt(LocalDateTime.now());
        permissionRepository.save(permission);
    }

    private PermissionResponse convertToResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId().toString());
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        response.setCreatedAt(permission.getCreatedAt());
        response.setUpdatedAt(permission.getUpdatedAt());
        return response;
    }
}
