package com.module.project.repository;

import com.module.project.model.BookingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingScheduleRepository extends JpaRepository<BookingSchedule, Long> {
}
