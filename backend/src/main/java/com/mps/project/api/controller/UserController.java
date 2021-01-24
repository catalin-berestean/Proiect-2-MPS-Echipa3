package com.mps.project.api.controller;

import com.mps.project.api.exceptions.ResourceNotFoundException;
import com.mps.project.api.exceptions.ResponseMessage;
import com.mps.project.api.model.Role;
import com.mps.project.api.model.User;
import com.mps.project.api.service.SecurityService;
import com.mps.project.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersByOrganization() {
        String userName = securityService.findLoggedInUsername();
        Optional<User> loggedInUser = userService.findByUsername(userName);
        if(loggedInUser.isEmpty()) {
            throw new AccessDeniedException("Unexpected error");
        }
        List<User> users = userService.findByOrganizationId(loggedInUser.get().getOrganization().getId())
                .stream()
                .filter(u -> !u.getRole().equalsIgnoreCase(Role.ADMIN.name()))
                .collect(Collectors.toList());
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ResponseMessage> changeUserRole(@PathVariable("id") Long userId, @RequestBody Map<String, String> roleMap) {
        Optional<User> userFromDb = userService.findById(userId);
        if (userFromDb.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + userId + " was not found");
        }
        User updatedUser = userFromDb.get();
        String oldRole = updatedUser.getRole();
        String newRole = roleMap.get("role");
        updatedUser.setRole(newRole);
        User savedUser = userService.updateUser(updatedUser);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("User with name " + savedUser.getUsername() + " changed role from " + oldRole + " to " + newRole));
    }
}
