package com.module.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module.project.model.User;
import com.module.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("user/infor")
    public ResponseEntity<User> register(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(service.getUserInfo(authorizationHeader));
    }
}
