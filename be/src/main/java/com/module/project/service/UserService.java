package com.module.project.service;

import com.module.project.dto.ResponseCode;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
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

    public HmsResponse<User> getUserInfo(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        List<Booking> bookings = bookingRepository.findAllByUser(user);
        user.setBookings(new HashSet<>(bookings));
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, user);
    }
}
