package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.request.AuthenticationRequest;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.dto.response.AuthenticationResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Token;
import com.module.project.model.User;
import com.module.project.repository.RoleRepository;
import com.module.project.repository.TokenRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final TokenRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final MailService mailService;
        private final AuthenticationManager authenticationManager;

        public HmsResponse<AuthenticationResponse> register(RegisterRequest request) {
                if (userRepository.existsByUsername(request.getUsername())) {
                    throw new HmsException(HmsErrorCode.INVALID_REQUEST, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
                throw new HmsException(HmsErrorCode.INVALID_REQUEST, "Email already exists. Please use another email to register");
        }
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .gender(request.getGender())
                .role(roleRepository.findByName(request.getRole()))
                .status(Constant.COMMON_STATUS.INACTIVE)
                .build();
        userRepository.save(user);
        try {
            mailService.sendMailVerifyEmail(request.getEmail(), user.getUsername());
        } catch (Exception e) {
            log.error("error when send email verify account {}", request.getUsername());
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, AuthenticationResponse.builder()
                .message(HttpStatus.CREATED.getReasonPhrase())
                .build());
        }

        public HmsResponse<AuthenticationResponse> verify(String username) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "Invalid username"));
                user.setStatus(Constant.COMMON_STATUS.ACTIVE);
                userRepository.save(user);
                return HMSUtil.buildResponse(ResponseCode.SUCCESS, AuthenticationResponse.builder()
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .build());
        }

        public HmsResponse<AuthenticationResponse> authentication(AuthenticationRequest request) {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(),
                                request.getPassword()));

        var user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(request.getUsernameOrEmail())
                .orElseThrow());
        var jwtToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, jwtToken);
                return HMSUtil.buildResponse(ResponseCode.SUCCESS, AuthenticationResponse.builder()
                        .token(jwtToken)
                        .build());
        }

        private void saveUserToken(User user, String jwtToken) {
                var token = Token.builder()
                        .user(user)
                        .token(jwtToken)
                        .expired(false)
                        .revoked(false)
                        .build();
                tokenRepository.save(token);
            }
        
            private void revokeAllUserTokens(User user) {
                var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
                if (validUserTokens.isEmpty())
                    return;
                validUserTokens.forEach(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                });
                tokenRepository.saveAll(validUserTokens);
        }
}
