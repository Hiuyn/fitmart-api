package dev.fitmart.FItMart.components.user.service;

import dev.fitmart.FItMart.auth.AuthenticationFacade;
import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.user.UserModel;
import dev.fitmart.FItMart.components.user.UserRepository;
import dev.fitmart.FItMart.components.user.mapping.RegisterRequest;
import dev.fitmart.FItMart.components.user.mapping.UserResponse;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;
    @Autowired
    private FilterService filterService;

    @Override
    public UserResponse registerUser(RegisterRequest request){
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists: " + request.getEmail());
        }

        // Check if an ADMIN already exists
        if (userRepository.existsByRole("ADMIN")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "An ADMIN user already exists");
        }
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

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Paginated<List<UserResponse>> getAllUsers(int page, int limit, Map<String, String> filters, String q, int createdAtSort) {
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

        Page<UserModel> userPage = filterService.applyFilter(UserModel.class, "users", filterCriteria, pageable, q, limit, createdAtSort);

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::convertUserToResponse)
                .collect(Collectors.toList());

        Paginated<List<UserResponse>> response = new Paginated<>();
        response.setData(userResponses);

        Paginated.Pagination pagination = new Paginated.Pagination();
        pagination.setTotal(userPage.getTotalElements());
        pagination.setCount(userResponses.size());
        pagination.setPerPage(limit == -1 ? userResponses.size() : limit);
        pagination.setCurrentPage(limit == -1 ? 1 : page);
        pagination.setTotalPages(limit == -1 ? 1 : userPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    public UserResponse findUserByUuid(String uuid) {
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found: " + uuid));
        return convertUserToResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserModel createUser(UserModel user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists: " + user.getEmail());
        }
        user.setUuid(UuidGenerator.generateCustomUuid());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated_at(Instant.now());
        user.setUpdated_at(Instant.now());
        user.setDeleted_at(null);
        return userRepository.save(user);
    }

    @Override
    public UserResponse updateUser(String uuid, UserModel userUpdate) {
        UserModel currentUser = userRepository.findByEmail(userUpdate.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userUpdate.getEmail()));

        // Find the user to update
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found: " + uuid));

        // Check if non-ADMIN is trying to update another user's profile
        if (!currentUser.getRole().equals("ADMIN") && !currentUser.getUuid().equals(uuid)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only update your own profile");
        }

        // Check for email uniqueness if email is being updated
        if (userUpdate.getEmail() != null && !userUpdate.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(userUpdate.getEmail()).isPresent()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists: " + user.getEmail());
            }
            user.setEmail(userUpdate.getEmail());
        }

        // ADMIN can update all fields
        if (currentUser.getRole().equals("ADMIN")) {
            if (userUpdate.getUser_name() != null) user.setUser_name(userUpdate.getUser_name());
            if (userUpdate.getPassword() != null && !userUpdate.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
            }
            if (userUpdate.getAvatar_url() != null) user.setAvatar_url(userUpdate.getAvatar_url());
            if (userUpdate.getRole() != null) user.setRole(userUpdate.getRole());
            if (userUpdate.getPermissions() != null) user.setPermissions(userUpdate.getPermissions());
            if (userUpdate.getMetadata() != null) user.setMetadata(userUpdate.getMetadata());
        } else {
            // Non-ADMIN can only update email, user_name, password, avatar_url
            if (userUpdate.getUser_name() != null) user.setUser_name(userUpdate.getUser_name());
            if (userUpdate.getPassword() != null && !userUpdate.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
            }
            if (userUpdate.getAvatar_url() != null) user.setAvatar_url(userUpdate.getAvatar_url());
        }

        user.setUpdated_at(Instant.now());
        user = userRepository.save(user);
        return convertUserToResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String uuid) {
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found: " + uuid));
        user.setDeleted_at(Instant.now());
        userRepository.save(user);
    }

    @Override
    public UserResponse convertUserToResponse(UserModel user) {
        UserResponse response = new UserResponse();
        response.setUuid(user.getUuid());
        response.setUser_name(user.getUser_name());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAvatar_url(user.getAvatar_url());
        response.setPermissions(user.getPermissions());
        response.setMetadata(user.getMetadata());
        response.setCreated_at(user.getCreated_at());
        response.setUpdated_at(user.getUpdated_at());
        response.setDeleted_at(user.getDeleted_at());

        return response;
    }

    private UserModel convertToEntity(RegisterRequest request) {
        return UserModel.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .user_name(request.getUser_name())
                .created_at(Instant.now())
                .updated_at(Instant.now())
                .deleted_at(null)
                .role("ADMIN")
                .permissions(new ArrayList<>())
                .avatar_url("")
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

    @Override
    public boolean hasAdminUser() {
        return userRepository.existsByRole("ADMIN");
    }
}
