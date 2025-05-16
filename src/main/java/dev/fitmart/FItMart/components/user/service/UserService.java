package dev.fitmart.FItMart.components.user.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.user.UserModel;
import dev.fitmart.FItMart.components.user.mapping.RegisterRequest;
import dev.fitmart.FItMart.components.user.mapping.UserResponse;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserResponse registerUser(RegisterRequest request);
    String findByUserId();

    Paginated<List<UserResponse>> getAllUsers(int page, int limit, Map<String, String> filters, String q, int createdAtSort);
    UserResponse findUserByUuid(String uuid);
    UserModel createUser(UserModel user);
    UserResponse updateUser(String uuid, UserModel user);
    void deleteUser(String uuid);
    UserResponse convertUserToResponse(UserModel user);
    boolean hasAdminUser();
}
