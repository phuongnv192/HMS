package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.ChangePasswordRequest;
import com.module.project.dto.request.SubmitReviewRequest;
import com.module.project.dto.request.UserInfoRequest;
import com.module.project.dto.response.BookingDetailResponse;
import com.module.project.dto.response.ListUserResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.Cleaner;
import com.module.project.model.Role;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.RoleRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final BookingService bookingService;

    public HmsResponse<User> getUserInfo(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(
                () -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        List<Booking> bookings = bookingRepository.findAllByUser(user);
        user.setBookings(new HashSet<>(bookings));
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, user);
    }

    public HmsResponse<User> getEmployeeInfo(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .filter(a -> !a.getRole().getName().equals(RoleEnum.CUSTOMER.toString())).orElseThrow(
                        () -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        List<Booking> bookings = bookingRepository.findAllByUser(user);
        user.setBookings(new HashSet<>(bookings));
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, user);
    }

    public HmsResponse<List<ListUserResponse>> getUsers(String roleName) {
        List<String> acceptRoles = List.of(RoleEnum.ADMIN.name(), RoleEnum.LEADER.name(), RoleEnum.MANAGER.name());
        if (!acceptRoles.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
        List<User> userList = userRepository.findAll();
        List<ListUserResponse> response = new ArrayList<>();
        for (User user : userList) {
            ListUserResponse userResponse = ListUserResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .gender(user.getGender())
                    .fullName(HMSUtil.convertToFullName(user.getFirstName(), user.getLastName()))
                    .build();
            response.add(userResponse);
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);

    }

    public HmsResponse<User> updateUserInfo(UserInfoRequest request, String userId, String roleName) {
        User user = validateUpdateUser(userId, roleName, request.getUserId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(Constant.COMMON_STATUS.ACTIVE.equals(request.getStatus())
                ? Constant.COMMON_STATUS.ACTIVE
                : Constant.COMMON_STATUS.INACTIVE);
        user.setUpdateBy(Long.parseLong(userId));
        if (!user.getRole().getId().equals(request.getRoleId())) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any role"));
            user.setRole(role);
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, userRepository.save(user));
    }

    public HmsResponse<Object> submitReview(SubmitReviewRequest request, String userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "can't find any booking by ".concat(request.getBookingId().toString())));
        if (!userId.equals(booking.getUser().getId().toString())) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
        if (request.getReviewBooking()) {
            if (!TransactionStatus.DONE.name().equals(booking.getStatus())) {
                throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR,
                        "can't submit review because booking is not done yet");
            }
            booking.setReview(request.getReview());
            bookingRepository.save(booking);
        } else {
            BookingSchedule bookingSchedule = bookingScheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                            "can't find any schedule by ".concat(request.getBookingId().toString())));
            if (!TransactionStatus.DONE.name().equals(bookingSchedule.getStatus())) {
                throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR,
                        "can't submit review because schedule is not done yet");
            }
            bookingSchedule.setRatingScore(request.getRatingScore().toString());
            bookingScheduleRepository.save(bookingSchedule);

            Cleaner cleaner = cleanerRepository.findById(request.getCleanerId())
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                            "can't find any cleaner by ".concat(request.getBookingId().toString())));
            scheduleService.updateReviewOfCleaner(cleaner, booking, bookingSchedule,
                    Long.valueOf(request.getRatingScore()), request.getReview());
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    public HmsResponse<User> changePassword(ChangePasswordRequest request, String userId, String roleName) {
        User user = validateUpdateUser(userId, roleName, request.getUserId());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR,
                    "the current password you input not match with itself on system");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, userRepository.save(user));
    }

    public HmsResponse<List<BookingDetailResponse>> getBookingForUser(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(
                () -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        List<Booking> bookings = bookingRepository.findAllByUser(user);
        List<BookingDetailResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            if (!TransactionStatus.DONE.name().equals(booking.getStatus())) {
                responses.add(bookingService.getBookingDetail(booking.getId(), userId, RoleEnum.CUSTOMER.name(), true));
            }
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, responses);
    }

    private User validateUpdateUser(String userId, String roleName, Long userUpdateId) {
        if (!userId.equals(userUpdateId.toString())) {
            List<String> acceptRoles = List.of(RoleEnum.ADMIN.name(), RoleEnum.LEADER.name());
            if (!acceptRoles.contains(roleName)) {
                throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
            }
        }
        return userRepository.findById(userUpdateId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "can't find any schedule by ".concat(userUpdateId.toString())));
    }
}
