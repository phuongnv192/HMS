package com.module.project.repository;

import com.module.project.model.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Integer> {
}
