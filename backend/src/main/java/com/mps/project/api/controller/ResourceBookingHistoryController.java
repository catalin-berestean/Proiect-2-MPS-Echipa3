package com.mps.project.api.controller;

import com.mps.project.api.model.ResourceBookingHistory;
import com.mps.project.api.repository.ResourceBookingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResourceBookingHistoryController {

    @Autowired
    private ResourceBookingHistoryRepository resourceBookingHistoryRepository;

    @GetMapping("/resources/{resource_id}/history")
    public ResponseEntity<List<ResourceBookingHistory>> getResourceHistory(@PathVariable("resource_id") Long resourceId) {
        List<ResourceBookingHistory> historyList = resourceBookingHistoryRepository.findByResource_IdOrderByFromBookingTimeDesc(resourceId);
        historyList.forEach(h -> h.getUser().setPassword(null));
        return ResponseEntity.status(HttpStatus.OK).body(historyList);
    }
}