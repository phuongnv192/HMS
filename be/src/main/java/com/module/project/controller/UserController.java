package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.request.SubmitReviewRequest;
import com.module.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.USER_SUBMIT_REVIEW;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("user/infor")
    public ResponseEntity<Object> register(HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(service.getUserInfo(userId));
    }

    
    @PostMapping(USER_SUBMIT_REVIEW)
    public ResponseEntity<Object> submitReview(@RequestBody SubmitReviewRequest submitReviewRequest, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(userService.submitReview(submitReviewRequest, userId));
    }
}
