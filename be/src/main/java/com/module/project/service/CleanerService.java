package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.Constant;
import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.response.CleanerHistoryResponse;
import com.module.project.model.Cleaner;
import com.module.project.repository.CleanerRepository;
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
    private final Random random = new Random();

    public List<Cleaner> getCleaners(CleanerFilterRequest request) {
        List<Cleaner> cleaners = cleanerRepository.findAll();
        return filterCleaners(cleaners, request);
    }

    public Cleaner chooseCleaner(String type) {
        if (Constant.CHOOSE_TYPE.AUTO.equals(type)) {
            return autoChooseCleaner();
        } else if (Constant.CHOOSE_TYPE.MANUAL.equals(type)) {
            return manualChooseCleaner();
        } else {
            throw new InternalError("type of choosing not found");
        }
    }

    private Cleaner autoChooseCleaner() {
        List<Cleaner> cleaners = getCleaners(new CleanerFilterRequest());
        // TODO: sort the cleaner by criteria (lack of rate and review of cleaner)
        cleaners.sort(Comparator.comparing(e -> e.getServices().size())
        );
        int index = random.nextInt(cleaners.size());
        return cleaners.get(index);
    }

    public List<CleanerHistoryResponse> getCleanerHistory(Integer cleanerId) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId)
                .orElseThrow(() -> new InternalError("getCleanerHistory: not found any cleaner by id ".concat(cleanerId.toString())));
        List<CleanerHistoryResponse> response = new ArrayList<>();
        Map<String, String> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
        });
        if (reviewList != null) {
            for (String bookingId : reviewList.keySet()) {
                // TODO: write a query that join all table needed to get all info
//                Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
//                if (bookingOptional.isPresent()) {
//                    Booking booking = bookingOptional.get();
//                    CleanerHistoryResponse cleanerHistory = CleanerHistoryResponse.builder()
//                            .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
//                            .rating("")
//
//                            .build();
//                }
            }
        }
        return response;
    }

    public Cleaner insertCleaner(CleanerInfoRequest cleanerInfoRequest) {
        return null;
    }

    private Cleaner manualChooseCleaner() {
        return null;
    }

    private List<Cleaner> filterCleaners(List<Cleaner> cleaners, CleanerFilterRequest request) {
        // TODO: 31/10/2023 fields for filter are not finalize yet (age - rate), need to check later
        return cleaners.stream()
                .filter(cleaner -> StringUtils.isBlank(request.getGender()) || cleaner.getUser().getGender().equalsIgnoreCase(request.getGender()))
                .filter(cleaner -> StringUtils.isBlank(request.getAge()) || cleaner.getUser().getId() == Integer.parseInt(request.getAge()))
                .filter(cleaner -> StringUtils.isBlank(request.getRate()) || cleaner.getId() == Integer.parseInt(request.getRate()))
                .toList();
    }
}
