package dev.fitmart.FItMart.components.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {
    @Autowired
    private AdminService userService;

    @GetMapping
    public ResponseEntity<List<AdminModel>> getAllUser() {
        return new ResponseEntity<List<AdminModel>>(userService.allUser(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Optional<AdminModel>> getUser(@PathVariable String uuid){
        return new ResponseEntity<Optional<AdminModel>>(userService.getUser(uuid), HttpStatus.OK);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AdminModel> createUser(@RequestBody AdminModel user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateUser(@PathVariable String uuid, @RequestBody AdminModel user) {
        Optional<AdminModel> updatedUser = userService.updateUser(uuid, user);
        return updatedUser
                .<ResponseEntity<?>>map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND));
    }

    // DELETE
    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteUser(@PathVariable String uuid) {
        boolean deleted = userService.deleteUser(uuid);
        if (deleted) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
}
