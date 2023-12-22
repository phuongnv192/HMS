package com.module.project.service;

import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class})
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private BookingScheduleRepository bookingScheduleRepository;
    @Mock
    private BookingTransactionRepository bookingTransactionRepository;
    @Mock
    private MailService mailService;

    @Test
    void booking_whenSuccess() {

    }
}
