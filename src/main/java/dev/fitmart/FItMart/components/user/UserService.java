package dev.fitmart.FItMart.components.user;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    String findByUserId();
}
