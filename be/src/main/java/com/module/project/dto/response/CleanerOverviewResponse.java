package com.module.project.dto.response;

import com.module.project.model.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerOverviewResponse {
    private Long cleanerId;
    private Long userId;
    private String name;
    private String gender;
    private String idCard;
    private String address;
    private String dob;
    private String email;
    private String phoneNumber;
    private String status;
    private Branch branch;
    private int activityYear;
    private Long averageRating;
    private int ratingNumber;
}
