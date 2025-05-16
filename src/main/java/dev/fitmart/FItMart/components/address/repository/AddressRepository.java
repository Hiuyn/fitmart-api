package dev.fitmart.FItMart.components.address.repository;

import dev.fitmart.FItMart.components.address.model.Address;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends MongoRepository<Address, ObjectId> {
}
