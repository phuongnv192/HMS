package com.module.project.repository;

import com.module.project.model.Booking;
import com.module.project.model.Cleaner;
import com.module.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUser(User user);

    Page<Booking> findAllByCleanersIn(Set<Cleaner> cleaners, Pageable pageable);
}
