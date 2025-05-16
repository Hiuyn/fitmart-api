package dev.fitmart.FItMart.components.user;

import dev.fitmart.FItMart.components.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByRole(String role);
    Optional<UserModel> findByUuid(String uuid);
}
