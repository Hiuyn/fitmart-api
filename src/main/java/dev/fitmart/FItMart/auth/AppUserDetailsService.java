package dev.fitmart.FItMart.auth;

import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.account.repository.AccountRepository;
import dev.fitmart.FItMart.components.user.UserModel;
import dev.fitmart.FItMart.components.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            return new User(account.getEmail(), account.getPassword(), Collections.emptyList());
        }

       UserModel user = userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found"));
       return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }
}
