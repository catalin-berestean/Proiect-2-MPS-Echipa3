package com.mps.project.api.controller;

import com.mps.project.api.model.Organization;
import com.mps.project.api.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping("/organizations")
    public ResponseEntity<List<Organization>> getOrganizations(@RequestParam(value="name", required = false) String name) {
        if(name != null) {
            Optional<Organization> orgFromDb = organizationRepository.findByName(name);
            if(orgFromDb.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
            }
            return ResponseEntity.status(HttpStatus.OK).body(List.of(orgFromDb.get()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(organizationRepository.findAll());
    }
}
