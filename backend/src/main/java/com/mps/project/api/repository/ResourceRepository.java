package com.mps.project.api.repository;

import com.mps.project.api.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByOrganization_Id(Long organizationId);
    List<Resource> findByOrganization_IdAndName(Long organizationId, String name);
}
