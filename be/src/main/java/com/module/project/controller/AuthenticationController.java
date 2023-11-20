package com.module.project.controller;

import com.module.project.dto.request.AuthenticationRequest;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.dto.response.AuthenticationResponse;
import com.module.project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.*;

@RestController
@RequestMapping(API_V1 + AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping(REGISTER)
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(AuthenticationResponse.builder()
                            .token("")
                            .message("").build());
        }
    }

    @GetMapping(VERIFY)
    public ResponseEntity<AuthenticationResponse> verify(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.verify(request));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(AuthenticationResponse.builder()
                    .token("")
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build());
        } catch (Exception ex) {
            log.error("verify error", ex);
            return ResponseEntity.internalServerError()
                    .body(AuthenticationResponse.builder()
                            .token("")
                            .message("").build());
        }
    }

    @PostMapping(AUTHENTICATE)
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(service.authentication(request));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(AuthenticationResponse.builder()
                    .token("")
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build());
        } catch (Exception ex) {
            log.error("authentication error", ex);
            return ResponseEntity.internalServerError()
                    .body(AuthenticationResponse.builder()
                            .token("")
                            .message("").build());
        }
    }

}
