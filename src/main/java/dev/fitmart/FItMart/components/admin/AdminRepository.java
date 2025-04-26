package dev.fitmart.FItMart.components.admin;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<AdminModel, String> {
    Optional<AdminModel> findByUuid(String uuid);
}
