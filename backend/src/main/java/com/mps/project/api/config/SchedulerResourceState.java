package com.mps.project.api.config;

import com.mps.project.api.model.Resource;
import com.mps.project.api.model.ResourceState;
import com.mps.project.api.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerResourceState {

    @Autowired
    private ResourceRepository resourceRepository;

    Logger logger = LoggerFactory.getLogger(SchedulerResourceState.class);

    @Scheduled(fixedDelay = 10000)
    public void updateResourceState() {
        logger.info("Check if any resource should be updated");
        List<Resource> resourceList = resourceRepository.findByState(ResourceState.BOOKED.name());
        for (Resource resource : resourceList) {
            if (resource.getEstimatedTimeBooking() != null && resource.getEstimatedTimeBooking().isBefore(LocalDateTime.now())) {
                logger.info("Resource with name {} changing from Booked to Available", resource.getName());
                resource.setState(ResourceState.AVAILABLE.name());
                resource.setUser(null);
                resource.setBookingReason(null);
                resource.setEstimatedTimeBooking(null);
                resourceRepository.save(resource);
            }
        }
    }
}
