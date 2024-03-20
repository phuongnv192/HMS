package com.module.project.repository;

import com.module.project.model.ServiceAddOnHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAddOnHistoryRepository extends JpaRepository<ServiceAddOnHistory, Long> {


}
