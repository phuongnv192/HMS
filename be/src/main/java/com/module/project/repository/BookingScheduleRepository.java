package com.module.project.repository;

import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingScheduleRepository extends JpaRepository<BookingSchedule, Long> {

    @Modifying
    @Query(value = "update BookingSchedule " +
            "set status = :status " +
            "where bookingTransaction = :bookingTransaction and status not in :notStatus")
    void updateCancelBooking(@Param(value = "bookingTransaction") BookingTransaction bookingTransaction,
                            @Param(value = "status") String status,
                            @Param(value = "notStatus") List<String> notStatus);

    List<BookingSchedule> findAllByBookingTransaction(BookingTransaction bookingTransaction);

    @Query(value = "select count(1) from BookingSchedule " +
            "where bookingTransaction = :bookingTransaction and status not in :status")
    int getScheduleStatusByTransactionId(@Param(value = "bookingTransaction") BookingTransaction bookingTransaction,
                                         @Param(value = "status") List<String> status);
}
