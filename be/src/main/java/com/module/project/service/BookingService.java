package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.NumberUserByMonth;
import com.module.project.dto.PaymentStatus;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingStatusRequest;
import com.module.project.dto.response.BookingDetailResponse;
import com.module.project.dto.response.DashboardInfoResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorNumber());
//        if (floorInfoEnum == null) {
//            throw new HmsException(HmsErrorCode.INVALID_REQUEST,
//                    "error when look up floor info: ".concat(request.getFloorArea()));
//        }
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
            booking.setRejectedReason(request.getRejectedReason());
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
            scheduleList.sort(Comparator.comparing(BookingSchedule::getWorkDate));
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

    public HmsResponse<Object> getBookingList(Integer page, Integer size, String roleName, String userId, String bookingName, String bookingPhone, String status, Date workingDate, String floorArea) {
        List<String> acceptRole = Arrays.asList(RoleEnum.MANAGER.name(), RoleEnum.LEADER.name(),
                RoleEnum.CUSTOMER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        Pageable pageable = PageRequest.of(page, size);
        List<Booking> bookingList = bookingRepository.findAll(pageable).getContent();
        bookingList = filter(bookingList, bookingName, bookingPhone, status, floorArea);
        List<BookingDetailResponse> responses = new ArrayList<>();
        for (Booking booking : bookingList) {
            responses.add(getBookingDetail(booking.getId(), userId, roleName, true));
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, responses);
    }

    public HmsResponse<DashboardInfoResponse> getDashboardInfo(String roleName, LocalDate startDate) {
        List<String> acceptRole = Arrays.asList(RoleEnum.MANAGER.name(), RoleEnum.LEADER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        Calendar calendar = HMSUtil.setLocalDateToCalendar(Calendar.getInstance(), startDate);
        calendar.add(Calendar.YEAR, 1);
        Date scanDate = calendar.getTime();
        List<Booking> bookings = bookingRepository.findAllByCreateDateAfterAndCreateDateBefore(Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), scanDate);
//        Map<String, NumberUserByMonth> userByMonths = userRepository.getNumberUserByMonth(Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), scanDate)
//                .stream()
//                .collect(Collectors.toMap(NumberUserByMonth::getMonth, Function.identity()));
        List<Object> us = userRepository.getNumberUserByMonth(Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), scanDate);
        long totalUser = 0;
        Map<String, NumberUserByMonth> userByMonths = new HashMap<>();
        if (us != null) {
            for (Object object : us) {
                List<Object> userByMonth = JsonService.toObject(object, new TypeReference<>() {
                });
                if (userByMonth != null) {
                    userByMonths.put((String) userByMonth.get(0),
                            NumberUserByMonth.builder()
                                    .month((String) userByMonth.get(0))
                                    .times((Long) userByMonth.get(1))
                                    .build());
                    totalUser += (Long) userByMonth.get(1);
                }
            }
        }
        Map<String, Integer> numberByBookingStatus = initNumberByBookingStatus();
        Map<String, DashboardInfoResponse.ItemDetail> months = new HashMap<>();
        BigDecimal totalProfit = new BigDecimal(0);
        BigDecimal totalProfitWithdraw = new BigDecimal(0);
        int bookingNumberHaveReview = 0;
        List<Long> averageRating = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingTransaction bookingTransaction = bookingTransactionRepository.findByBooking(booking)
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "relevant transaction is not existed on system"));
            totalProfit = totalProfit.add(BigDecimal.valueOf(bookingTransaction.getTotalBookingPrice()));
            List<BookingSchedule> bookingSchedules = bookingScheduleRepository.findAllByBookingTransaction(bookingTransaction);
            BigDecimal totalPrice = new BigDecimal(0);
            for (BookingSchedule schedule : bookingSchedules) {
                if (PaymentStatus.WITHDRAW.name().equals(schedule.getPaymentStatus())) {
                    totalPrice = totalPrice.add(BigDecimal.valueOf(schedule.getTotalSchedulePrice()));
                }
                if (schedule.getRatingScore() != null) {
                    averageRating.add(Long.valueOf(schedule.getRatingScore()));
                }
            }
            totalProfitWithdraw = totalProfitWithdraw.add(totalPrice);
            bookingNumberHaveReview += booking.getReview() != null ? 1 : 0;
            Integer times = numberByBookingStatus.get(booking.getStatus());
            if (times == null) {
                numberByBookingStatus.put(booking.getStatus(), 1);
            } else {
                numberByBookingStatus.put(booking.getStatus(), ++times);
            }
            Map<String, Integer> monthNumber = initNumberByBookingStatus();
            monthNumber.put(booking.getStatus(), 1);
            NumberUserByMonth userByMonth = userByMonths.get(HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.MMYYYY_FORMAT));
            long numberUserByMonth = 0;
            if (userByMonth != null) {
                numberUserByMonth = userByMonth.getTimes().intValue();
            }
            DashboardInfoResponse.ItemDetail month = DashboardInfoResponse.ItemDetail.builder()
                    .bookingNumber(1)
                    .bookingNumberHaveReview(booking.getReview() != null ? 1 : 0)
                    .totalProfit(BigDecimal.valueOf(bookingTransaction.getTotalBookingPrice()))
                    .totalProfitWithdraw(totalPrice)
                    .numberByBookingStatus(monthNumber)
                    .newUserNumber(numberUserByMonth)
                    .build();
            DashboardInfoResponse.ItemDetail item = months.get(HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.MMYYYY_FORMAT));
            if (item == null) {
                months.put(HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.MMYYYY_FORMAT), month);
            } else {
                item = DashboardInfoResponse.ItemDetail.builder()
                        .bookingNumber(item.getBookingNumber() + month.getBookingNumber())
                        .bookingNumberHaveReview(item.getBookingNumberHaveReview() + month.getBookingNumberHaveReview())
                        .totalProfit(item.getTotalProfit().add(month.getTotalProfit()))
                        .totalProfitWithdraw(item.getTotalProfitWithdraw().add(month.getTotalProfitWithdraw()))
                        .numberByBookingStatus(combineTwoMap(item.getNumberByBookingStatus(), month.getNumberByBookingStatus()))
                        .newUserNumber(numberUserByMonth)
                        .build();
                months.put(HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.MMYYYY_FORMAT), item);
            }
        }
        DashboardInfoResponse.ItemDetail summary = DashboardInfoResponse.ItemDetail.builder()
                .bookingNumber(bookings.size())
                .bookingNumberHaveReview(bookingNumberHaveReview)
                .totalProfit(totalProfit)
                .totalProfitWithdraw(totalProfitWithdraw)
                .numberByBookingStatus(numberByBookingStatus)
                .averageRating(averageRating.stream().mapToDouble(Long::doubleValue).sum() / averageRating.size())
                .newUserNumber(totalUser)
                .build();
        DashboardInfoResponse response = DashboardInfoResponse.builder()
                .summary(summary)
                .months(fulfillMonth(userByMonths, months))
                .build();
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
    }

    private Map<String, DashboardInfoResponse.ItemDetail> fulfillMonth(Map<String, NumberUserByMonth> userByMonths,
                                                                       Map<String, DashboardInfoResponse.ItemDetail> months) {
        for (String key : userByMonths.keySet()) {
            if (months.get(key) == null) {
                DashboardInfoResponse.ItemDetail item = DashboardInfoResponse.ItemDetail.builder()
                        .bookingNumber(0)
                        .bookingNumberHaveReview(0)
                        .totalProfit(new BigDecimal(0))
                        .totalProfitWithdraw(new BigDecimal(0))
                        .numberByBookingStatus(initNumberByBookingStatus())
                        .newUserNumber(userByMonths.get(key).getTimes())
                        .build();
                months.put(key, item);
            }
        }
        return months;
    }

    private Map<String, Integer> combineTwoMap(Map<String, Integer> root, Map<String, Integer> map) {
        for (String key : map.keySet()) {
            Integer times = root.get(key);
            if (times == null) {
                root.put(key, map.get(key));
            } else {
                root.put(key, times + map.get(key));
            }
        }
        return root;
    }

    private Map<String, Integer> initNumberByBookingStatus() {
        Map<String, Integer> numberByBookingStatus = new HashMap<>();
        for (ConfirmStatus value : ConfirmStatus.values()) {
            numberByBookingStatus.put(value.name(), 0);
        }
        return numberByBookingStatus;
    }

    private String getListMailCleanerFromBooking(Booking booking) {
        return booking.getCleaners().stream().map(e -> {
            User u = e.getUser();
            return u.getEmail();
        }).collect(Collectors.joining(","));
    }

    private List<Booking> filter(List<Booking> bookings, String bookingName, String bookingPhone, String status, String floorArea) {
        if (bookingName == null && bookingPhone == null && status == null && floorArea == null) {
            return bookings;
        }
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUpByName(floorArea);
        return bookings.stream()
                .filter(booking -> (bookingName != null && booking.getHostName().contains(bookingName))
                        || (bookingPhone != null && booking.getHostPhone().contains(bookingPhone))
                        || (booking.getStatus().equalsIgnoreCase(status))
                        || (floorInfoEnum != null && booking.getFloorArea().equalsIgnoreCase(floorInfoEnum.getFloorArea())))
                .toList();
    }

    public boolean checkBookingToBeUpdated(BookingTransaction bookingTransaction, List<String> status) {
        return bookingScheduleRepository.getScheduleStatusByTransactionIdAndStatusContain(bookingTransaction,
                status) == bookingTransaction.getTotalBookingDate();
    }
}
