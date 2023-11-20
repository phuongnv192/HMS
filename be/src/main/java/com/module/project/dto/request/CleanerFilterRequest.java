package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanerFilterRequest {
//    private String name;
//    private String age;
//    private String rate;
    private int number;
    private List<LocalDate> workDate;
}
