package dev.fitmart.FItMart.components.user;

import dev.fitmart.FItMart.auth.AuthenticationFacade;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public UserResponse registerUser(UserRequest request){
        UserModel newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    @Override
    public String findByUserId() {
      String loggedInUserEmail  =  authenticationFacade.getAuthentication().getName();
      UserModel loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("user not found"));
      return loggedInUser.getUuid();
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
                .metadata(new HashMap<>())
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
