package dev.fitmart.FItMart.components.permissions.repository;

import dev.fitmart.FItMart.components.permissions.model.Permission;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<Permission, ObjectId> {
    Optional<Permission> findByNameAndDeletedAtIsNull(String name);
    List<Permission> findAllByDeletedAtIsNull();
}
