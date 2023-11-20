package com.module.project.repository;

import com.module.project.model.BookingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingTransactionRepository extends JpaRepository<BookingTransaction, Long> {
}
