package dev.fitmart.FItMart.auth;

import dev.fitmart.FItMart.components.account.mapping.AccountResponse;
import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.user.UserModel;
import dev.fitmart.FItMart.components.user.mapping.UserResponse;
import dev.fitmart.FItMart.exception.BaseResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import dev.fitmart.FItMart.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthenticationResponse<UserResponse>>> login(@Valid @RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        UserModel user = userDetailsService.getUserModelByEmail(request.getEmail());
        AuthenticationResponse<UserResponse> response = convertToResponse(jwtToken, user);

        return ResponseUtils.success(response);
    }

    @PostMapping("/accounts/login")
    public ResponseEntity<BaseResponse<AuthenticationResponse<AccountResponse>>> accountLogin(@Valid @RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        Account account = userDetailsService.getAccountByEmail(request.getEmail());
        AuthenticationResponse<AccountResponse> response = convertToResponse(jwtToken, account);
        return ResponseUtils.success(response);

    }

    private <T, R> AuthenticationResponse<R> convertToResponse(String token, T entity) {
        if (entity instanceof UserModel user) {
            UserResponse userResponse = new UserResponse(
                    user.getUuid(),
                    user.getEmail(),
                    user.getUser_name(),
                    user.getRole(),
                    user.getPermissions(),
                    user.getAvatar_url(),
                    user.getCreated_at(),
                    user.getUpdated_at(),
                    user.getDeleted_at(),
                    user.getMetadata()
            );
            return new AuthenticationResponse<>(token, (R) userResponse);
        } else if (entity instanceof Account account) {
            AccountResponse accountResponse = new AccountResponse(
                    account.getUuid(),
                    account.getEmail(),
                    account.getUser_name(),
                    account.getAddress(),
                    account.getAddress(),
                    account.getAvatar_url(),
                    account.getCreated_at(),
                    account.getUpdated_at(),
                    account.getDeleted_at()
            );
            return new AuthenticationResponse<>(token, (R) accountResponse);
        }
        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getName());
    }

}
