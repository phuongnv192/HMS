package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.request.ChangePasswordRequest;
import com.module.project.dto.request.SubmitReviewRequest;
import com.module.project.dto.request.UserInfoRequest;
import com.module.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.USERS;
import static com.module.project.dto.Constant.USER_CHANGE_PASSWORD;
import static com.module.project.dto.Constant.USER_INFO;
import static com.module.project.dto.Constant.USER_INFO_BY_ID;
import static com.module.project.dto.Constant.USER_SUBMIT_REVIEW;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(USERS)
    public ResponseEntity<Object> getUsers(HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(userService.getUsers(roleName));
    }

    @GetMapping(USER_INFO)
    public ResponseEntity<Object> getUserInfo(HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @GetMapping(USER_INFO_BY_ID)
    public ResponseEntity<Object> getUserInfoById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getEmployeeInfo(id));
    }

    @PutMapping(USER_INFO)
    public ResponseEntity<Object> updateUserInfo(@RequestBody UserInfoRequest request,
                                                 HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(userService.updateUserInfo(request, userId, roleName));
    }

    @PostMapping(USER_SUBMIT_REVIEW)
    public ResponseEntity<Object> submitReview(@RequestBody SubmitReviewRequest submitReviewRequest,
                                               HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(userService.submitReview(submitReviewRequest, userId));
    }

    @PostMapping(USER_CHANGE_PASSWORD)
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequest request,
                                                 HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(userService.changePassword(request, userId, roleName));
    }
}
