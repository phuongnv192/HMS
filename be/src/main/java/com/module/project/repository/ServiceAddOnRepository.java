package com.module.project.repository;

import com.module.project.model.ServiceAddOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAddOnRepository extends JpaRepository<ServiceAddOn, Long> {
    List<ServiceAddOn> findAllByParentIdAndStatusEquals(Long parentId, String status);

    List<ServiceAddOn> findAllByStatusEquals(String status);

    List<ServiceAddOn> findAllByIdInAndStatus(List<Long> ids, String status);
}
