package com.mps.project.api.controller;

import com.mps.project.api.exceptions.ConflictException;
import com.mps.project.api.exceptions.ResourceNotFoundException;
import com.mps.project.api.model.AdminContext;
import com.mps.project.api.model.Organization;
import com.mps.project.api.repository.OrganizationRepository;
import com.mps.project.api.service.SecurityService;
import com.mps.project.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/organizations")
    public ResponseEntity<List<Organization>> getOrganizations(@RequestParam(value = "name", required = false) String name) {
        if (name != null) {
            Optional<Organization> orgFromDb = organizationRepository.findByName(name);
            if (orgFromDb.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
            }
            return ResponseEntity.status(HttpStatus.OK).body(List.of(orgFromDb.get()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(organizationRepository.findAll());
    }

    @PostMapping("/organizations")
    public ResponseEntity<AdminContext> createOrganization(@RequestBody AdminContext adminContext) {
        Optional<Organization> orgOptional = organizationRepository.findByName(adminContext.getOrganization().getName());
        if (orgOptional.isPresent()) {
            throw new ConflictException("Organization with name " + adminContext.getOrganization().getName() + " already exists");
        }
        adminContext.getUser().setOrganization(adminContext.getOrganization());
        String decodedPassword = adminContext.getUser().getPassword();
        AdminContext updatedAdminContext = userService.createAdminAndOrganization(adminContext);
        securityService.autoLogin(adminContext.getUser().getUsername(), decodedPassword);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAdminContext);
    }

    @PutMapping("/organizations/{id}")
    public ResponseEntity<Organization> updateOrganization(@PathVariable("id") Long organizationId, @RequestBody Organization organization) {
        Optional<Organization> orgOptional = organizationRepository.findById(organizationId);
        if (orgOptional.isEmpty()) {
            throw new ResourceNotFoundException("Organization with name " + organization.getName() + " could not be found");
        }
        orgOptional.get().setName(organization.getName());
        orgOptional.get().setDescription(organization.getDescription());
        organizationRepository.save(organization);

        return ResponseEntity.status(HttpStatus.OK).body(organization);
    }
}
