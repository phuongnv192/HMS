package com.module.project.service;

import com.module.project.dto.ResponseCode;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.SubmitReviewRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.Cleaner;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CleanerRepository cleanerRepository;
    private final BookingScheduleRepository bookingScheduleRepository;
    private final ScheduleService scheduleService;

    public HmsResponse<User> getUserInfo(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        List<Booking> bookings = bookingRepository.findAllByUser(user);
        user.setBookings(new HashSet<>(bookings));
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, user);
    }

    public HmsResponse<Object> submitReview(SubmitReviewRequest request, String userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any booking by ".concat(request.getBookingId().toString())));
        if (!userId.equals(booking.getUser().getId().toString())) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
        if (request.getReviewBooking()) {
            if (!TransactionStatus.DONE.name().equals(booking.getStatus())) {
                throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "can't submit review because booking is not done yet");
            }
            booking.setReview(request.getReview());
            bookingRepository.save(booking);
        } else {
            BookingSchedule bookingSchedule = bookingScheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any schedule by ".concat(request.getBookingId().toString())));
            if (!TransactionStatus.DONE.name().equals(bookingSchedule.getStatus())) {
                throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "can't submit review because schedule is not done yet");
            }
            bookingSchedule.setRatingScore(request.getRatingScore().toString());
            bookingScheduleRepository.save(bookingSchedule);

            Cleaner cleaner = cleanerRepository.findById(request.getCleanerId())
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any cleaner by ".concat(request.getBookingId().toString())));
            scheduleService.updateReviewOfCleaner(cleaner, booking, bookingSchedule, Long.valueOf(request.getRatingScore()), request.getReview());
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }
}
