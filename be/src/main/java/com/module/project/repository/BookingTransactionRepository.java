package com.module.project.repository;

import com.module.project.model.Booking;
import com.module.project.model.BookingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingTransactionRepository extends JpaRepository<BookingTransaction, Long> {
    Optional<BookingTransaction> findByBooking(Booking booking);
}
