package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.request.AuthenticationRequest;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.dto.response.AuthenticationResponse;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Token;
import com.module.project.model.User;
import com.module.project.repository.RoleRepository;
import com.module.project.repository.TokenRepository;
import com.module.project.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class})
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @Test
    void register_whenSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .username("username")
                .firstName("firstName")
                .build();

        HmsResponse<AuthenticationResponse> response = authenticationService.register(request);
        Assertions.assertEquals(ResponseCode.SUCCESS.getCode(), response.getCode());
    }

    @Test
    void register_whenFail_whenException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("username")
                .firstName("firstName")
                .build();
        Mockito.doThrow(HttpClientErrorException.BadRequest.class).when(mailService).sendMailVerifyEmail(any(), any());
        HmsResponse<AuthenticationResponse> response = authenticationService.register(request);
        Assertions.assertEquals("00", response.getCode());

        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        Assertions.assertThrows(HmsException.class, () -> authenticationService.register(request));

        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
        Assertions.assertThrows(HmsException.class, () -> authenticationService.register(request));
    }

    @Test
    void verify_whenSuccess() {
        String username = "username";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        HmsResponse<AuthenticationResponse> response = authenticationService.verify(username);
        Assertions.assertEquals(ResponseCode.SUCCESS.getCode(), response.getCode());
    }

    @Test
    void authentication_whenSuccess() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .usernameOrEmail("username")
                .password("password")
                .build();
        User user = User.builder()
                .status(Constant.COMMON_STATUS.ACTIVE)
                .build();

        Mockito.when(userRepository.findByUsername(request.getUsernameOrEmail())).thenReturn(Optional.of(user));
        Mockito.when(jwtService.generateToken(any())).thenReturn(StringUtils.EMPTY);
        Mockito.when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(List.of(new Token()));

        HmsResponse<AuthenticationResponse> response = authenticationService.authentication(request);
        Assertions.assertEquals(ResponseCode.SUCCESS.getCode(), response.getCode());

        Mockito.when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(Collections.emptyList());
        response = authenticationService.authentication(request);
        Assertions.assertEquals(ResponseCode.SUCCESS.getCode(), response.getCode());
    }

    @Test
    void authentication_whenFail_whenException() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .usernameOrEmail("username")
                .password("password")
                .build();
        User user = User.builder()
                .status(Constant.COMMON_STATUS.INACTIVE)
                .build();

        Mockito.when(userRepository.findByUsername(request.getUsernameOrEmail())).thenReturn(Optional.of(user));
        Assertions.assertThrows(HmsException.class, () -> authenticationService.authentication(request));

        Mockito.when(userRepository.findByUsername(request.getUsernameOrEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(request.getUsernameOrEmail())).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> authenticationService.authentication(request));
    }
}
