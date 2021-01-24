package com.mps.project.api.controller;

import com.mps.project.api.exceptions.ConflictException;
import com.mps.project.api.exceptions.ResourceNotFoundException;
import com.mps.project.api.model.*;
import com.mps.project.api.repository.OrganizationRepository;
import com.mps.project.api.repository.ResourceBookingHistoryRepository;
import com.mps.project.api.repository.ResourceRepository;
import com.mps.project.api.service.SecurityService;
import com.mps.project.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private ResourceBookingHistoryRepository resourceBookingHistoryRepository;

    @GetMapping("/resources")
    public ResponseEntity<List<Resource>> getResources() {
        String userName = securityService.findLoggedInUsername();
        Optional<User> loggedInUser = userService.findByUsername(userName);
        if(loggedInUser.isEmpty()) {
            throw new AccessDeniedException("Unexpected error");
        }
        List<Resource> resourceList = resourceRepository.findByOrganization_Id(loggedInUser.get().getOrganization().getId());
        resourceList.forEach(r -> {
            if(r.getUser() != null) {
                r.getUser().setPassword(null);
            }
        });
        return ResponseEntity.status(HttpStatus.OK).body(resourceList);
    }

    @PostMapping("/resources")
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        String userName = securityService.findLoggedInUsername();
        Optional<User> loggedInUser = userService.findByUsername(userName);
        if(loggedInUser.isEmpty()) {
            throw new AccessDeniedException("Unexpected error");
        }
        Long organizationId = loggedInUser.get().getOrganization().getId();
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
        if(resource.getEstimatedTimeBooking() == null || resource.getBookingReason() == null) {
            throw new IllegalArgumentException("Estimated time for booking and reason should not be null");
        }
        if(resource.getEstimatedTimeBooking().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Estimated time for booking is before the current date");
        }
        Optional<Resource> resourceDb = resourceRepository.findById(resourceId);
        if(resourceDb.isEmpty()) {
            throw new ResourceNotFoundException("Resource with id " + resourceId + " could not be found");
        }
        if(resourceDb.get().getState().equalsIgnoreCase(ResourceState.BOOKED.name())) {
            throw new ConflictException("Resource is already booked");
        }
        resourceDb.get().setBookingReason(resource.getBookingReason());
        resourceDb.get().setState(ResourceState.BOOKED.name());
        resourceDb.get().setEstimatedTimeBooking(resource.getEstimatedTimeBooking());
        Optional<User> user = userService.findByUsername(securityService.findLoggedInUsername());
        if(user.isEmpty() || !user.get().getRole().equalsIgnoreCase(Role.EDIT.name())) {
            throw new AccessDeniedException("User is not allowed to book this resource");
        }
        resourceDb.get().setUser(user.get());
        Resource updatedResource = resourceRepository.save(resourceDb.get());

        ResourceBookingHistory resourceBookingHistory = new ResourceBookingHistory();
        resourceBookingHistory.setResource(resourceDb.get());
        resourceBookingHistory.setToBookingTime(resource.getEstimatedTimeBooking());
        resourceBookingHistory.setUser(resourceDb.get().getUser());
        resourceBookingHistoryRepository.save(resourceBookingHistory);

        updatedResource.getUser().setPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(updatedResource);
    }
}
