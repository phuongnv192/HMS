package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.Constant;
import com.module.project.dto.request.ChooseCleanerRequest;
import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.dto.response.CleanerHistoryResponse;
import com.module.project.model.BookingSchedule;
import com.module.project.model.Branch;
import com.module.project.model.Cleaner;
import com.module.project.model.User;
import com.module.project.repository.BookingHistoryRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BranchRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanerService {

    private final CleanerRepository cleanerRepository;
    private final BookingHistoryRepository bookingHistoryRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BookingScheduleRepository bookingScheduleRepository;

    private final Random random = new Random();

    public List<Cleaner> getCleaners(CleanerFilterRequest request) {
        List<Cleaner> cleaners = cleanerRepository.findAll();
        return filterCleaners(cleaners, request);
    }

    public List<Cleaner> chooseCleaner(ChooseCleanerRequest chooseCleanerRequest) {
        if (Constant.CHOOSE_TYPE.AUTO.equals(chooseCleanerRequest.getType())) {
            return autoChooseCleaner(chooseCleanerRequest);
        } else if (Constant.CHOOSE_TYPE.MANUAL.equals(chooseCleanerRequest.getType())) {
            return manualChooseCleaner();
        } else {
            throw new InternalError("type of choosing not found");
        }
    }

    private List<Cleaner> autoChooseCleaner(ChooseCleanerRequest chooseCleanerRequest) {
        int number = chooseCleanerRequest.getNumber();
        if (number <= 0 || number > 4) {
            throw new InternalError("number of cleaner with auto type is invalid");
        }
        // TODO: pick randomly in pool of available cleaners
        List<Cleaner> cleaners = filterOnlyAvailable(getCleaners(new CleanerFilterRequest()));
        List<Cleaner> res = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Cleaner cleaner = cleaners.get(random.nextInt(cleaners.size()));
            res.add(cleaner);
            cleaners.remove(cleaner);
        }
        return res;
    }

    public List<CleanerHistoryResponse> getCleanerHistory(Integer cleanerId) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId)
                .orElseThrow(() -> new InternalError(
                        "getCleanerHistory: not found any cleaner by id ".concat(cleanerId.toString())));
        List<CleanerHistoryResponse> response = new ArrayList<>();
        Map<String, String> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
        });
        // TODO: get history from booking-history table
        return response;
    }

    public Cleaner insertCleaner(CleanerInfoRequest cleanerInfoRequest) {
        Branch branch = branchRepository.findById(Integer.parseInt(cleanerInfoRequest.getBranchId()))
                .orElseThrow(() -> new InternalError(
                        "insertCleaner: can't find any branch with id: ".concat(cleanerInfoRequest.getBranchId())));
        User user = userRepository.findById(Integer.parseInt(cleanerInfoRequest.getUserId()))
                .orElseThrow(() -> new InternalError(
                        "insertCleaner: can't find any user with id".concat(cleanerInfoRequest.getUserId())));
        Set<com.module.project.model.Service> serviceIds = new HashSet<>(
                serviceRepository.findAllById((Iterable<Integer>) cleanerInfoRequest.getServiceIds().iterator()));
        Cleaner cleaner = Cleaner.builder()
                .address(cleanerInfoRequest.getAddress())
                .idCard(cleanerInfoRequest.getIdCard())
                .branch(branch)
                .status(Constant.COMMON_STATUS.ACTIVE)
                .user(user)
                .services(serviceIds)
                .build();
        return cleanerRepository.save(cleaner);
    }

    public Cleaner updateCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId()).get();

        Branch branch = branchRepository.findById(Integer.parseInt(cleanerUpdateRequest.getBranchId()))
                .orElseThrow(() -> new InternalError(
                        "insertCleaner: can't find any branch with id: ".concat(cleanerUpdateRequest.getBranchId())));
        Set<com.module.project.model.Service> serviceIds = new HashSet<>(
                serviceRepository.findAllById((Iterable<Integer>) cleanerUpdateRequest.getServiceIds().iterator()));

        cleaner = Cleaner.builder()
                .address(cleanerUpdateRequest.getAddress())
                .idCard(cleanerUpdateRequest.getIdCard())
                .branch(branch)
                .status(Constant.COMMON_STATUS.ACTIVE.equals(cleanerUpdateRequest.getStatus().toUpperCase())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE)
                .services(serviceIds)
                .build();

        return cleanerRepository.save(cleaner);
    }

    public Cleaner changeStatusCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId()).get();
        cleaner = Cleaner.builder()
                .status(Constant.COMMON_STATUS.ACTIVE.equals(cleanerUpdateRequest.getStatus().toUpperCase())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE)
                .build();

        return cleanerRepository.save(cleaner);
    }

    private List<Cleaner> manualChooseCleaner() {
        return null;
    }

    private List<Cleaner> filterCleaners(List<Cleaner> cleaners, CleanerFilterRequest request) {
        // TODO: update later - filter by gender and sort by rating
        // List<BookingSchedule> bookingSchedules =
        // bookingScheduleRepository.findAllById();
        // cleaners.sort(Comparator.comparing(Clea));
        return cleaners.stream()
                .filter(cleaner -> StringUtils.isBlank(request.getGender())
                        || cleaner.getUser().getGender().equalsIgnoreCase(request.getGender()))
                // .filter(cleaner -> StringUtils.isBlank(request.getAge()) ||
                // cleaner.getUser().getId() == Integer.parseInt(request.getAge()))
                // .filter(cleaner -> StringUtils.isBlank(request.getRate()) || cleaner.getId()
                // == Integer.parseInt(request.getRate()))
                .toList();
    }

    private List<Cleaner> filterOnlyAvailable(List<Cleaner> cleaners) {
        // TODO: check in booking schedule table that doesn't exist in with specific
        // date
        return cleaners;
    }
}
