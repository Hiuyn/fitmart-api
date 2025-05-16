package dev.fitmart.FItMart.components.account.repository;

import dev.fitmart.FItMart.components.account.model.Account;
import dev.fitmart.FItMart.components.user.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUuid(String uuid);
}
