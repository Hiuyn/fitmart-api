package dev.fitmart.FItMart.auth;

import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.user.UserModel;
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
    public ResponseEntity<BaseResponse<AuthenticationResponse<UserModel>>> login(@Valid @RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        UserModel user = userDetailsService.getUserModelByEmail(request.getEmail());
        return ResponseUtils.success(new AuthenticationResponse<UserModel>(jwtToken, user));
    }

    @PostMapping("/accounts/login")
    public ResponseEntity<BaseResponse<AuthenticationResponse<Account>>> accountLogin(@Valid @RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        Account account = userDetailsService.getAccountByEmail(request.getEmail());
        return ResponseUtils.success(new AuthenticationResponse<>(jwtToken, account));
    }
}
