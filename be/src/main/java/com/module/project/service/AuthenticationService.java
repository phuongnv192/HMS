package com.module.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.module.project.dto.request.AuthenticationRequest;
import com.module.project.dto.response.AuthenticationResponse;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.repository.RoleRepository;
import com.module.project.model.Token;
import com.module.project.repository.TokenRepository;
import com.module.project.model.User;
import com.module.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository repository;
        private final RoleRepository roleRepository;
        private final TokenRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthenticationResponse register(RegisterRequest request) {

                if (repository.existsByUsername(request.getUsername())) {
                        return AuthenticationResponse.builder()
                                        .token("")
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Username already exists!")
                                        .build();
                }

                if (repository.existsByEmail(request.getEmail())) {
                        return AuthenticationResponse.builder()
                                        .token("")
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Email already exists!")
                                        .build();
                }

                var user = User.builder()
                                .username(request.getUsername())
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .phoneNumber(request.getPhoneNumber())
                                .email(request.getEmail())
                                .gender(request.getGender())
                                .role(roleRepository.findByName(request.getRole()))
                                .accountStatus(true)
                                .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .code(HttpStatus.CREATED.value())
                                .message(HttpStatus.CREATED.getReasonPhrase())
                                .build();
        }

        public AuthenticationResponse authentication(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(),
                                                request.getPassword()));

                var user = repository.findByUsername(request.getUsernameOrEmail())
                                .orElseGet(() -> repository.findByEmail(request.getUsernameOrEmail())
                                                .orElseThrow());
                var jwtToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, jwtToken);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .code(HttpStatus.OK.value())
                                .message(HttpStatus.OK.getReasonPhrase())
                                .build();
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
