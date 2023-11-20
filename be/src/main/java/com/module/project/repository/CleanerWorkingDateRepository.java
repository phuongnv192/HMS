package com.module.project.repository;

import com.module.project.model.CleanerWorkingDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CleanerWorkingDateRepository extends JpaRepository<CleanerWorkingDate, Long> {
    List<CleanerWorkingDate> findAllByStatusEquals(String status);
}
