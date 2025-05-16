package dev.fitmart.FItMart.components.account.controller;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.account.mapping.AccountRequest;
import dev.fitmart.FItMart.components.account.mapping.AccountResponse;
import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    @Autowired
    private final AccountService accountService;

    @PostMapping("/register")
    public AccountResponse register(@RequestBody AccountRequest request) {
        return accountService.registerAccount(request);
    }

    @PostMapping()
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return new ResponseEntity<>(accountService.convertAccountToResponse(createdAccount), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Paginated<List<AccountResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams) {
        int createdAtSort = (created_at == -1 || created_at == 1 || created_at == 0) ? created_at : -1;

        // Create filter map from dynamic query parameters
        Map<String, String> filters = new HashMap<>();

        // Add filterField and filterValue if provided (for backwards compatibility)
        if (filterField != null && filterValue != null) {
            filters.put(filterField, filterValue);
        }

        // Add other query parameters as filters, excluding reserved ones
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("page") && !key.equals("limit") && !key.equals("q") &&
                    !key.equals("created_at") && !key.equals("filterField") && !key.equals("filterValue")) {
                filters.put(key, entry.getValue());
            }
        }

        return ResponseEntity.ok(accountService.getAllAccounts(page, limit, filters, q, createdAtSort));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<AccountResponse> getUserById(@PathVariable String uuid) {
        return ResponseEntity.ok(accountService.findAccountByUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable String uuid, @Valid @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(uuid, account));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String uuid) {
        accountService.deleteAccount(uuid);
        return ResponseEntity.noContent().build();
    }
}
