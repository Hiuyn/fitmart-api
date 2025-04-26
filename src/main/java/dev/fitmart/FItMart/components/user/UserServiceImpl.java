package dev.fitmart.FItMart.components.user;

import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request){
        UserModel newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    private UserModel convertToEntity(UserRequest request) {
        return UserModel.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .user_name(request.getUser_name())
                .created_at(Instant.now())
                .updated_at(Instant.now())
                .deleted_at(null)
                .role("")
                .metadata(new ArrayList<>())
                .build();
    }

    private UserResponse convertToResponse(UserModel registerUser) {
        return UserResponse.builder()
                .uuid(registerUser.getUuid())
                .user_name(registerUser.getUser_name())
                .email(registerUser.getEmail())
                .build();
    }
}
