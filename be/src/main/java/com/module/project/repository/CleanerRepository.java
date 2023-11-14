package com.module.project.repository;

import com.module.project.dto.response.CleanerHistoryResponse;
import com.module.project.model.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Integer> {
    Optional<Cleaner> findById(Integer id);

    List<Cleaner> findAll();

    @Query(value = "select bs.ratingScore, bs.workDate, bs. b from Booking b" +
            "left join BookingTransaction bt on b.id = bt.booking_id" +
            "left join BookingSchedule bs on bt.schedule_id = bs.scheduleId" +
            "where b.id = :bookingId", nativeQuery = true)
    CleanerHistoryResponse getCleanerHistoryInfo(@Param("bookingId") Integer bookingId);

}
