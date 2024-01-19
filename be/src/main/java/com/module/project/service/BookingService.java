package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.PaymentStatus;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingStatusRequest;
import com.module.project.dto.response.BookingDetailResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.Cleaner;
import com.module.project.model.ServicePackage;
import com.module.project.model.ServiceType;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;
    private final BookingScheduleRepository bookingScheduleRepository;
    private final BookingTransactionRepository bookingTransactionRepository;
    private final MailService mailService;

    public HmsResponse<Booking> booking(BookingRequest request, String userId) {
        User customer = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(
                        () -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant user is not existed on system"));
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorArea());
        if (floorInfoEnum == null) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST,
                    "error when look up floor info: ".concat(request.getFloorArea()));
        }
        Booking booking = Booking.builder()
                .hostName(request.getHostName())
                .hostPhone(request.getHostPhone())
                .hostAddress(request.getHostAddress())
                .hostDistance(request.getHostDistance())
                .houseType(request.getHouseType())
                .floorNumber(request.getFloorNumber())
                .floorArea(floorInfoEnum.getFloorArea())
                .user(customer)
                .userUpdate(customer)
                .note(request.getNote())
                .status(ConfirmStatus.RECEIVED.name())
                .rawRequest(JsonService.writeStringSkipError(request))
                .build();
        bookingRepository.save(booking);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, booking);
    }

    public HmsResponse<Object> confirmBooking(BookingStatusRequest request, Long userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "relevant booking is not existed on system"));
        if (!ConfirmStatus.RECEIVED.name().equals(booking.getStatus())) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST,
                    "can't execute this request because status of schedule is not match");
        }
        booking.setStatus(ConfirmStatus.CONFIRMED.name());
        processBookingSchedule(booking, userId);

        String mailTo = getListMailCleanerFromBooking(booking);
        mailService.sendMailForCleaners(mailTo, booking.getHostName(), booking.getHostAddress(), booking.getHostPhone(),
                HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.DDMMYYYYHHMMSS_FORMAT));

        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    public HmsResponse<Object> cancelBooking(BookingStatusRequest request, Long userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "relevant booking is not existed on system"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "can't find any user by ".concat(userId.toString())));
        List<String> acceptRole = List.of(RoleEnum.LEADER.name());
        if (booking.getUser().getId().equals(userId) || acceptRole.contains(user.getRole().getName())) {
            scheduleService.cancelBooking(booking, user);

            String mailTo = getListMailCleanerFromBooking(booking);
            mailService.sendMailCancelOfBookingToCleaners(mailTo, booking.getHostName(), booking.getHostAddress(),
                    booking.getHostPhone(),
                    HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.DDMMYYYYHHMMSS_FORMAT),
                    HMSUtil.formatDate(new Date(), HMSUtil.DDMMYYYYHHMMSS_FORMAT));

            return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
        } else {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
    }

    public HmsResponse<Object> updateWithdraw(Long bookScheduleId, String userId, String roleName) {
        List<String> approveRole = List.of(RoleEnum.LEADER.name());
        if (approveRole.contains(roleName)) {
            BookingSchedule bookingSchedule = bookingScheduleRepository.findById(bookScheduleId)
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                            "relevant schedule is not existed on system"));
            bookingSchedule.setPaymentStatus(PaymentStatus.WITHDRAW.name());
            bookingSchedule.setUpdateBy(Long.parseLong(userId));
            bookingScheduleRepository.save(bookingSchedule);
            return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
        } else {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
    }

    private void processBookingSchedule(Booking booking, Long userId) {
        BookingRequest request = JsonService.strToObject(booking.getRawRequest(), new TypeReference<>() {
        });
        if (request == null) {
            throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR,
                    "error when parse raw request of booking ".concat(booking.getId().toString()));
        }
        BookingTransaction bookingTransaction = BookingTransaction.builder()
                .booking(booking)
                .status(ConfirmStatus.CONFIRMED.name())
                .build();
        if (request.getServiceTypeId() == null) {
            scheduleService.processBookingRegular(booking, request, bookingTransaction, userId);
        } else {
            scheduleService.processBookingPeriod(booking, request, bookingTransaction, userId);
        }
    }

    public HmsResponse<Booking> updateBooking(BookingRequest request, String userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "relevant booking is not existed on system"));
        if (!userId.equals(booking.getUser().getId().toString())) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
        BookingTransaction bookingTransaction = bookingTransactionRepository.findByBooking(booking)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR,
                        "can't find any booking transaction by ".concat(booking.getId().toString())));
        List<String> status = List.of(ConfirmStatus.RECEIVED.name());
        if (!checkBookingToBeUpdated(bookingTransaction, status)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "booking is no longer allowed to be updated");
        }
        booking.setHostName(request.getHostName());
        booking.setHostAddress(request.getHostAddress());
        booking.setHostPhone(request.getHostPhone());
        booking.setHostDistance(request.getHostDistance());
        bookingRepository.save(booking);

        String mailTo = getListMailCleanerFromBooking(booking);
        mailService.sendMailUpdateOfBooking(mailTo, booking.getHostName(), booking.getHostAddress(),
                booking.getHostPhone(),
                HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.DDMMYYYYHHMMSS_FORMAT),
                HMSUtil.formatDate(new Date(), HMSUtil.DDMMYYYYHHMMSS_FORMAT));

        return HMSUtil.buildResponse(ResponseCode.SUCCESS, booking);
    }

    public BookingDetailResponse getBookingDetail(Long bookingId, String userId, String roleName,
            boolean isShowSchedule) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "relevant booking is not existed on system"));
        List<String> acceptRole = Arrays.asList(RoleEnum.MANAGER.name(), RoleEnum.LEADER.name());
        List<Long> cleanersId = booking.getCleaners().stream().map(Cleaner::getId).toList();
        if (!acceptRole.contains(roleName)) {
            if (booking.getUser().getId().toString().equals(userId) || cleanersId.contains(Long.parseLong(userId))) {
                // do nothing
            } else {
                throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
            }
        }
        BookingTransaction bookingTransaction = bookingTransactionRepository.findByBooking(booking)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR,
                        "relevant transaction is not existed on system"));
        ServicePackage servicePackage = bookingTransaction.getServicePackage();
        String serviceTypeName = null, servicePackageName = null;
        if (servicePackage != null) {
            ServiceType serviceType = servicePackage.getServiceType();
            serviceTypeName = serviceType != null ? serviceType.getServiceTypeName() : null;
            servicePackageName = servicePackage.getServicePackageName();
        }
        List<BookingSchedule> scheduleList = null;
        if (isShowSchedule) {
            scheduleList = bookingScheduleRepository.findAllByBookingTransaction(bookingTransaction);
        }
        return BookingDetailResponse.builder()
                .bookingId(bookingId)
                .hostName(booking.getHostName())
                .hostPhone(booking.getHostPhone())
                .hostAddress(booking.getHostAddress())
                .hostDistance(booking.getHostDistance())
                .houseType(booking.getHouseType())
                .floorNumber(booking.getFloorNumber())
                .floorArea(booking.getFloorArea())
                .bookingTransactionId(bookingTransaction.getTransactionId())
                .serviceTypeName(serviceTypeName)
                .servicePackageName(servicePackageName)
                .totalBookingPrice(bookingTransaction.getTotalBookingPrice())
                .totalBookingCleaner(bookingTransaction.getTotalBookingCleaner())
                .totalBookingDate(bookingTransaction.getTotalBookingDate())
                .cleaners(booking.getCleaners().stream().toList())
                .createDate(booking.getCreateDate())
                .updateDate(booking.getUpdateDate())
                .status(bookingTransaction.getStatus())
                .review(booking.getReview())
                .rejectedReason(booking.getRejectedReason())
                .scheduleList(scheduleList)
                .build();
    }

    public HmsResponse<Object> getBookingList(Integer page, Integer size, String roleName) {
        List<String> acceptRole = Arrays.asList(RoleEnum.MANAGER.name(), RoleEnum.LEADER.name(),
                RoleEnum.CUSTOMER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        Pageable pageable = PageRequest.of(page, size);
        List<Booking> bookingList = bookingRepository.findAll(pageable).getContent();
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, bookingList);
    }

    private String getListMailCleanerFromBooking(Booking booking) {
        return booking.getCleaners().stream().map(e -> {
            User u = e.getUser();
            return u.getEmail();
        }).collect(Collectors.joining(","));
    }

    public boolean checkBookingToBeUpdated(BookingTransaction bookingTransaction, List<String> status) {
        return bookingScheduleRepository.getScheduleStatusByTransactionIdAndStatusContain(bookingTransaction,
                status) == bookingTransaction.getTotalBookingDate();
    }
}
