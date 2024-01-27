package com.module.project.dto.request;

import com.module.project.dto.WorkingTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanerAvailableRequest {
    private List<WorkingTime> workingTimes;
}
