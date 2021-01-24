package com.mps.project.api.controller;

import com.mps.project.api.exceptions.ConflictException;
import com.mps.project.api.exceptions.ResourceNotFoundException;
import com.mps.project.api.model.Organization;
import com.mps.project.api.model.Resource;
import com.mps.project.api.model.ResourceState;
import com.mps.project.api.model.User;
import com.mps.project.api.repository.OrganizationRepository;
import com.mps.project.api.repository.ResourceRepository;
import com.mps.project.api.service.SecurityService;
import com.mps.project.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @GetMapping("/resources/organizations/{org_id}")
    public ResponseEntity<List<Resource>> getResources(@PathVariable("org_id") Long organizationId) {
        List<Resource> resourceList = resourceRepository.findByOrganization_Id(organizationId);
        return ResponseEntity.status(HttpStatus.OK).body(resourceList);
    }

    @PostMapping("/resources/organizations/{org_id}")
    public ResponseEntity<Resource> createResource(@PathVariable("org_id") Long organizationId,
                                                   @RequestBody Resource resource) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if(organization.isEmpty()) {
            throw new ResourceNotFoundException("Organization with id " + organizationId + " does not exist");
        }
        List<Resource> resourceList = resourceRepository.findByOrganization_IdAndName(organizationId, resource.getName());
        if(!resourceList.isEmpty()) {
            throw new ConflictException("Resource with name " + resource.getName() + " already exists");
        }
        resource.setState(ResourceState.AVAILABLE.name());
        resource.setOrganization(organization.get());
        Resource resourceDb = resourceRepository.save(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceDb);
    }

    @PutMapping("/resources/book/{resource_id}")
    public ResponseEntity<Resource> bookResource(@PathVariable("resource_id") Long resourceId,
                                                 @RequestBody Resource resource) {
        Optional<Resource> resourceDb = resourceRepository.findById(resourceId);
        if(resourceDb.isEmpty()) {
            throw new ResourceNotFoundException("Resource with id " + resourceId + " could not be found");
        }
        resourceDb.get().setBookingReason(resource.getBookingReason());
        resourceDb.get().setState(ResourceState.BOOKED.name());
        resourceDb.get().setEstimatedTimeBooking(resource.getEstimatedTimeBooking());
        Optional<User> user = userService.findByUsername(securityService.findLoggedInUsername());
        if(user.isEmpty()) {
            throw new AccessDeniedException("User is not allowed to book this resource");
        }
        resourceDb.get().setUser(user.get());
        Resource updatedResource = resourceRepository.save(resourceDb.get());

        updatedResource.getUser().setPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(updatedResource);
    }
}
