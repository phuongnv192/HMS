package com.module.project.controller;

import com.module.project.dto.request.AuthenticationRequest;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.AUTH;
import static com.module.project.dto.Constant.AUTHENTICATE;
import static com.module.project.dto.Constant.REGISTER;
import static com.module.project.dto.Constant.VERIFY;

import java.net.URI;

@RestController
@RequestMapping(API_V1 + AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping(REGISTER)
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @GetMapping(VERIFY)
    public ResponseEntity<Object> verify(@RequestParam(value = "username") String username) {

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost/hms/login"));
        service.verify(username);
        // Create a ResponseEntity with status code 302 and headers
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping(AUTHENTICATE)
    public ResponseEntity<Object> authentication(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authentication(request));
    }
}
