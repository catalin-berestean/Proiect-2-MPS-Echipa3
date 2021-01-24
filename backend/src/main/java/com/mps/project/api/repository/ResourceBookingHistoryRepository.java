package com.mps.project.api.repository;


import com.mps.project.api.model.ResourceBookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceBookingHistoryRepository extends JpaRepository<ResourceBookingHistory, Long> {
    List<ResourceBookingHistory> findByResource_IdOrderByFromBookingTimeDesc(Long resourceId);
}
