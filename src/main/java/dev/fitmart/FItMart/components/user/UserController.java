package dev.fitmart.FItMart.components.user;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.user.mapping.RegisterRequest;
import dev.fitmart.FItMart.components.user.mapping.UserResponse;
import dev.fitmart.FItMart.components.user.service.UserService;
import dev.fitmart.FItMart.exception.BaseResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private final UserService userService;

    // CREATE
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseUtils.success(userService.registerUser(request));
    }

    @GetMapping("/check-admin")
    public ResponseEntity<BaseResponse<Boolean>> checkAdmin() {
        return ResponseUtils.success(userService.hasAdminUser());
    }

    @PostMapping("/users")
    public ResponseEntity<BaseResponse<UserResponse>> createUser(@Valid @RequestBody UserModel user) {
        UserModel createdUser = userService.createUser(user);
        return ResponseUtils.success(userService.convertUserToResponse(createdUser));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Paginated<List<UserResponse>>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams) {
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

        return ResponseUtils.success(userService.getAllUsers(page, limit, filters, q, createdAtSort));
    }

    @GetMapping("/users/{uuid}")
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable String uuid) {
        return ResponseUtils.success(userService.findUserByUuid(uuid));
    }

    @PutMapping("/users/{uuid}")
    public ResponseEntity<BaseResponse<UserResponse>> updateUser(@PathVariable String uuid, @Valid @RequestBody UserModel user) {
        return ResponseUtils.success(userService.updateUser(uuid, user));
    }

    @DeleteMapping("/users/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable String uuid) {
        userService.deleteUser(uuid);
        return ResponseUtils.noContent();
    }

//    @GetMapping("/auth/me")
//    public ResponseEntity<UserResponse> getCurrentUserProfile() {
//        String uuid = userService.findByUserId();
//        return ResponseEntity.ok(userService.findUserByUuid(uuid));
//    }

//    @PutMapping("/auth/me")
//    public ResponseEntity<UserResponse> updateCurrentUserProfile(@Valid @RequestBody UserModel user) {
//        String uuid = userService.findByUserId();
//        return ResponseEntity.ok(userService.updateUser(uuid, user));
//    }
}
