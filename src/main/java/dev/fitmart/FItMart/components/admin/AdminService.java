package dev.fitmart.FItMart.components.admin;

import dev.fitmart.FItMart.libs.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository userRepository;

    public List<AdminModel> allUser() {
        return userRepository.findAll();
    }

    public Optional<AdminModel> getUser(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    // Create
    public AdminModel createUser(AdminModel user) {
        if (user.getMetadata() == null) {
            user.setMetadata(new ArrayList<>());
        }
        user.setUuid(UuidGenerator.generateCustomUuid());
        user.setCreated_at(Instant.now());
        user.setUpdated_at(Instant.now());
        user.setDeleted_at(null);
        return userRepository.save(user);
    }

    // Update
    public Optional<AdminModel> updateUser(String uuid, AdminModel updatedUser) {
        Optional<AdminModel> optionalUser = userRepository.findByUuid(uuid);
        if (optionalUser.isPresent()) {
            AdminModel existingUser = optionalUser.get();

            existingUser.setUuid(UuidGenerator.generateCustomUuid());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setUser_name(updatedUser.getUser_name());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setAvatar_url(updatedUser.getAvatar_url());
            existingUser.setMetadata(updatedUser.getMetadata());
            existingUser.setCreated_at(Instant.now());
            existingUser.setUpdated_at(Instant.now());

            userRepository.save(existingUser);
            return Optional.of(existingUser);
        } else {
            return Optional.empty();
        }
    }

    // Delete
    public boolean deleteUser(String uuid) {
        Optional<AdminModel> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

}
