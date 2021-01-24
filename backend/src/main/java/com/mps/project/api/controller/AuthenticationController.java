package com.mps.project.api.controller;

import com.mps.project.api.exceptions.ResponseMessage;
import com.mps.project.api.model.Role;
import com.mps.project.api.model.User;
import com.mps.project.api.repository.OrganizationRepository;
import com.mps.project.api.service.SecurityService;
import com.mps.project.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static com.mps.project.api.utils.UserUtils.getUserJson;

@RestController
public class AuthenticationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;


    @GetMapping("/me/authenticated")
    public ResponseEntity<ResponseMessage> isUserAuthenticated() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User is not authenticated");
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("User is authenticated"));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registration(@RequestBody @Valid User user) {
        String decodedPassword = user.getPassword();
        User userToClient = userService.registerUser(user, Role.VIEW.name());
        // we send the decoded password to autologin method
        securityService.autoLogin(user.getUsername(), decodedPassword);
        return ResponseEntity.status(HttpStatus.OK).body(userToClient);
    }

    @GetMapping("/me/logout")
    public ResponseEntity<ResponseMessage> logout(HttpServletRequest request, HttpServletResponse response) {
        String username = securityService.findLoggedInUsername();
        securityService.logout(request, response);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Logout successful for " + username));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        securityService.autoLogin(user.getUsername(), user.getPassword());
        Optional<User> userDb = userService.findByUsername(user.getUsername());
        if (userDb.isEmpty()) {
            throw new AccessDeniedException("Username " + user.getUsername() + " was not found");
        }
        User userToClient = getUserJson(userDb.get());

        return ResponseEntity.status(HttpStatus.OK).body(userToClient);
    }
}
