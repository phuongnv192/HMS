package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListUserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String gender;
    private String address;
    private String bod;
}
