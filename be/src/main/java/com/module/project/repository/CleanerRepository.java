package com.module.project.repository;

import com.module.project.model.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
    Optional<Cleaner> findById(Integer id);

    List<Cleaner> findAll();

    List<Cleaner> findAllByIdInAndStatusEquals(List<Long> ids, String status);
}
