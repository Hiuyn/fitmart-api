package dev.fitmart.FItMart.components.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private final UserService userService;

    // CREATE
    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRequest request) {


        return userService.registerUser(request);
    }
}
