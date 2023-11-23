package com.module.project.repository;

import com.module.project.model.CleanerWorkingDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CleanerWorkingDateRepository extends JpaRepository<CleanerWorkingDate, Long> {
    List<CleanerWorkingDate> findAllByStatusEquals(String status);

    Optional<CleanerWorkingDate> findByCleanerIdAndScheduleDateEqualsAndStatusEquals(Long cleanerId, LocalDate scheduleDate, String status);

    @Modifying
    @Query(value = "update CleanerWorkingDate " +
            "set status = :status " +
            "where cleanerId = :cleanerId and scheduleDate in :workDate")
    void updateCancelBooking(@Param(value = "cleanerId") Long cleanerId,
                             @Param(value = "workDate") List<LocalDate> workDate,
                             @Param(value = "status") String status);
}
