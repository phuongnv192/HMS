package com.module.project.repository;

import com.module.project.model.Cleaner;
import com.module.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
    Optional<Cleaner> findById(Integer id);

    int countCleanerByStatusEquals(String status);

    List<Cleaner> findAllByStatusEquals(String status);

    List<Cleaner> findAllByIdInAndStatusEquals(List<Long> ids, String status);

    Optional<Cleaner> findByUser(User user);
}
