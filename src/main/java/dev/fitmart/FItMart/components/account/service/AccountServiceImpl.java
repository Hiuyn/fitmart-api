package dev.fitmart.FItMart.components.account.service;

import dev.fitmart.FItMart.auth.AuthenticationFacade;
import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.account.mapping.AccountRequest;
import dev.fitmart.FItMart.components.account.mapping.AccountResponse;
import dev.fitmart.FItMart.components.account.repository.AccountRepository;
import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.user.UserModel;
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
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;
    @Autowired
    private FilterService filterService;

    @Override
    public AccountResponse registerAccount(AccountRequest request){
        // Check if email already exists
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists: " + request.getEmail());
        }

        Account newAccount = convertToEntity(request);
        newAccount = accountRepository.save(newAccount);
        return convertToResponse(newAccount);
    }

    private Account convertToEntity(AccountRequest request) {
        return Account.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .user_name(request.getUser_name())
                .created_at(Instant.now())
                .updated_at(Instant.now())
                .deleted_at(null)
                .avatar_url("")
                .address("")
                .build();
    }

    private AccountResponse convertToResponse(Account register) {
        return AccountResponse.builder()
                .uuid(register.getUuid())
                .user_name(register.getUser_name())
                .email(register.getEmail())
                .created_at(register.getCreated_at())
                .updated_at(register.getUpdated_at())
                .deleted_at(register.getDeleted_at())
                .avatar_url(register.getAvatar_url())
                .address(register.getAddress())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Paginated<List<AccountResponse>> getAllAccounts(int page, int limit, Map<String, String> filters, String q, int createdAtSort) {
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

        Page<Account> AccountPage = filterService.applyFilter(Account.class, "accounts", filterCriteria, pageable, q, limit, createdAtSort);

        List<AccountResponse> AccountResponses = AccountPage.getContent().stream()
                .map(this::convertAccountToResponse)
                .collect(Collectors.toList());

        Paginated<List<AccountResponse>> response = new Paginated<>();
        response.setData(AccountResponses);

        Paginated.Pagination pagination = new Paginated.Pagination();
        pagination.setTotal(AccountPage.getTotalElements());
        pagination.setCount(AccountResponses.size());
        pagination.setPerPage(limit == -1 ? AccountResponses.size() : limit);
        pagination.setCurrentPage(limit == -1 ? 1 : page);
        pagination.setTotalPages(limit == -1 ? 1 : AccountPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    public AccountResponse findAccountByUuid(String uuid) {
        Account Account = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found: " + uuid));
        return convertAccountToResponse(Account);
    }

    @Override
    public Account createAccount(Account Account) {
        if (accountRepository.findByEmail(Account.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists: " + Account.getEmail());
        }
        Account.setUuid(UuidGenerator.generateCustomUuid());
        Account.setPassword(passwordEncoder.encode(Account.getPassword()));
        Account.setCreated_at(Instant.now());
        Account.setUpdated_at(Instant.now());
        Account.setDeleted_at(null);
        return accountRepository.save(Account);
    }

    @Override
    public AccountResponse updateAccount(String uuid, Account accountUpdate) {
        Account currentAccount = accountRepository.findByEmail(accountUpdate.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + accountUpdate.getEmail()));

        // Find the Account to update
        Account Account = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found: " + uuid));

        // Check for email uniqueness if email is being updated
        if (accountUpdate.getEmail() != null && !accountUpdate.getEmail().equals(Account.getEmail())) {
            if (accountRepository.findByEmail(accountUpdate.getEmail()).isPresent()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists: " + Account.getEmail());
            }
            Account.setEmail(accountUpdate.getEmail());
        }
        if (accountUpdate.getUser_name() != null) Account.setUser_name(accountUpdate.getUser_name());
        if (accountUpdate.getPassword() != null && !accountUpdate.getPassword().isBlank()) {
            Account.setPassword(passwordEncoder.encode(accountUpdate.getPassword()));
        }
        if (accountUpdate.getAvatar_url() != null) Account.setAvatar_url(accountUpdate.getAvatar_url());
        if (accountUpdate.getDeleted_at() != null) Account.setDeleted_at(accountUpdate.getDeleted_at());

        Account.setUpdated_at(Instant.now());
        Account = accountRepository.save(Account);
        return convertAccountToResponse(Account);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(String uuid) {
        Account account = accountRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found: " + uuid));
        account.setDeleted_at(Instant.now());
        accountRepository.save(account);
    }

    @Override
    public AccountResponse convertAccountToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setUuid(account.getUuid());
        response.setUser_name(account.getUser_name());
        response.setEmail(account.getEmail());
        response.setAvatar_url(account.getAvatar_url());
        response.setAddress(account.getAddress());
        response.setCreated_at(account.getCreated_at());
        response.setUpdated_at(account.getUpdated_at());
        response.setDeleted_at(account.getDeleted_at());

        return response;
    }

    @Override
    public String findByAccountId() {
        String loggedInUserEmail  =  authenticationFacade.getAuthentication().getName();
        Account loggedInUser = accountRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("account not found"));
        return loggedInUser.getUuid();
    }
}
