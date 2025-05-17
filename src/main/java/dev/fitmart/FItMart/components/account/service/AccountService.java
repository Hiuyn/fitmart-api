package dev.fitmart.FItMart.components.account.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.account.mapping.AccountRequest;
import dev.fitmart.FItMart.components.account.mapping.AccountResponse;
import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.user.mapping.RegisterRequest;
import dev.fitmart.FItMart.components.user.mapping.UserResponse;

import java.util.List;
import java.util.Map;

public interface AccountService {
    AccountResponse registerAccount(AccountRequest request);
    String findByAccountId();
    Paginated<List<AccountResponse>> getAllAccounts(int page, int limit, Map<String, String> filters, String q, int createdAtSort);
    AccountResponse findAccountByUuid(String uuid);
    Account createAccount(Account account);
    AccountResponse updateAccount(String uuid, Account account);
    void deleteAccount(String uuid);
    AccountResponse convertAccountToResponse(Account account);
}
