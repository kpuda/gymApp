package com.gymPal.gymApp.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymPal.gymApp.entity.Role;
import com.gymPal.gymApp.entity.Token;
import com.gymPal.gymApp.entity.User;
import com.gymPal.gymApp.enums.TokenType;
import com.gymPal.gymApp.enums.TokenValid;
import com.gymPal.gymApp.model.PasswordModel;
import com.gymPal.gymApp.model.RegistrationUserModel;
import com.gymPal.gymApp.repository.RoleRepository;
import com.gymPal.gymApp.repository.TokenRepository;
import com.gymPal.gymApp.repository.UserRepository;
import com.gymPal.gymApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    TokenRepository tokenRepository;
    PasswordEncoder passwordEncoder;
    JavaMailSenderImpl javaMailSender;

    private static final String EMAIL_TAKEN = "EMAIL_TAKEN";
    private static final String EMAIL_NOT_FOUND = "EMAIL_NOT_FOUND";
    private static final String USERNAME_TAKEN = "USERNAME_TAKEN";
    private static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    private static final String USER_REGISTERED = "USER_REGISTERED";
    private static final String USER_VERIFIED = "USER_VERIFIED";
    private static final String USER_VERIFIED_ALREADY = "USER_VERIFIED_ALREADY";
    private static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
    private static final String PASSWORD_OLD_INCORRECT = "PASSWORD_OLD_INCORRECT";

    private static final String TOKEN_USED = "TOKEN_USED";
    private static final String TOKEN_SENT = "TOKEN_SENT";
    private static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    private static final String TOKEN_INVALID = "TOKEN_INVALID";
    private static final String TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND";


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JavaMailSenderImpl javaMailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Transactional
    public String registerUser(RegistrationUserModel registrationUserModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(registrationUserModel.getEmail());
        User userFromUsername = userRepository.findByUsername(registrationUserModel.getUsername());
        String url;
        if (user != null || userFromUsername != null) {
            if (user != null) {
                return EMAIL_TAKEN;
            } else {
                return USERNAME_TAKEN;
            }
        } else {
            user = generateUser(registrationUserModel);
            String token = UUID.randomUUID().toString();
            Token registrationToken = new Token(user, token, TokenType.NEW_ACCOUNT_VERIFICATION, TokenValid.TOKEN_VALID);
            url = generateVerificationTokenUrl(generateUrl(request), registrationToken);
            userRepository.save(user);
            tokenRepository.save(registrationToken);
            // TODO sendEmailWithVerificationToken(user, url);
            log.info("Url: {}", url);
            log.info("Token: {}", token);
        }
        return USER_REGISTERED;
    }

    @Override
    @Transactional
    public String verifyRegistration(String token) {
        Token registrationToken = tokenRepository.findByToken(token);
        if (registrationToken == null) {
            return TOKEN_INVALID;
        } else {
            if (registrationToken.getExpirationDate().getTime() < new Date().getTime()) {
                return TOKEN_EXPIRED;
            } else if (registrationToken.getTokenValid().equals(TokenValid.TOKEN_USED)) {
                return TOKEN_USED;
            } else {
                if (!registrationToken.getTokenType().equals(TokenType.NEW_ACCOUNT_VERIFICATION)) {
                    return TOKEN_INVALID;
                }

                User user = registrationToken.getUser();
                if (user.isEnabled()) {
                    return USER_VERIFIED_ALREADY;
                } else {
                    registrationToken.setTokenValid(TokenValid.TOKEN_USED);
                    user.setEnabled(true);
                    userRepository.save(user);
                    tokenRepository.save(registrationToken);
                }
            }
        }
        return USER_VERIFIED;
    }

    @Override
    public Object resendVerificationToken(RegistrationUserModel userModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(userModel.getEmail());
        String url;
        if (user == null) {
            return EMAIL_NOT_FOUND;
        } else {
            String token = UUID.randomUUID().toString();
            Token registrationToken = new Token(user, token, TokenType.NEW_ACCOUNT_VERIFICATION, TokenValid.TOKEN_VALID);
            url = generateVerificationTokenUrl(generateUrl(request), registrationToken);
            tokenRepository.save(registrationToken);
            // TODO sendEmailWithVerificationToken(user, url);
            log.info("Url: {}", url);
            log.info("Token: {}", token);
        }
        return TOKEN_SENT;
    }

    @Override
    public String changePasssword(PasswordModel passwordModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        String url;
        if (user == null) {
            return EMAIL_NOT_FOUND;
        } else {
            if (!checkIfOldPasswordIsValid(passwordModel.getPassword(), user)) {
                return PASSWORD_OLD_INCORRECT;
            } else {
                user.setPassword(passwordModel.getNewPassword());
                userRepository.save(user);
            }
        }
        return PASSWORD_CHANGED;
    }

    @Override
    public String resetPassword(PasswordModel passwordModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        String url;
        if (user == null) {
            return USER_NOT_FOUND;
        } else {
            String token = UUID.randomUUID().toString();
            Token resetPasswordToken = new Token(user, token, TokenType.FORGOT_PASSWORD, TokenValid.TOKEN_VALID);
            url = generateChangePasswordTokenUrl(generateUrl(request), resetPasswordToken);
            tokenRepository.save(resetPasswordToken);
            //TODO sendEmailWithResetPasswordToken(user,url);
            log.info("Url: {}", url);
            log.info("Token: {}", token);
        }
        return TOKEN_SENT;
    }

    @Override
    public String savePassword(String token, PasswordModel passwordModel) {
        Token changePasswordToken = tokenRepository.findByToken(token);
        if (changePasswordToken == null) {
            return TOKEN_NOT_FOUND;
        } else {
            if (changePasswordToken.getExpirationDate().getTime() < new Date().getTime()) {
                return TOKEN_EXPIRED;
            } else if (!changePasswordToken.getTokenType().equals(TokenType.FORGOT_PASSWORD)) {
                return TOKEN_INVALID;
            } else if (changePasswordToken.getTokenValid().equals(TokenValid.TOKEN_USED)) {
                return TOKEN_USED;
            } else {
                User user = userRepository.findByEmail(changePasswordToken.getUser().getEmail());
                changePasswordToken.setTokenValid(TokenValid.TOKEN_USED);
                user.setPassword(passwordEncoder.encode(passwordModel.getNewPassword()));
                userRepository.save(user);
                tokenRepository.save(changePasswordToken);
            }
        }
        return PASSWORD_CHANGED;
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public String addRoleToUser(String username, String rolename) {
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(rolename.toUpperCase());
        user.getRoles().add(role);
        userRepository.save(user);
        return "ADDED";//TODO
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;//TODO
        } else if (!user.isEnabled()) {
            return null;
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    //TODO
    private User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Role addRole(String rolename) {
        return roleRepository.findByName(rolename);
    }

    private void sendEmailWithVerificationToken(User user, String url) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo(user.getEmail());
        message.setSubject("Registration");
        message.setText(url);
        message.setReplyTo(user.getEmail());
        javaMailSender.send(message);
    }

    private void sendEmailWithResetPasswordToken(User user, String url) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("football@score.com");
        message.setTo(user.getEmail());
        message.setSubject("Reset password");
        message.setText(url);
        message.setReplyTo(user.getEmail());
        javaMailSender.send(message);
    }


    private User generateUser(RegistrationUserModel registrationUserModel) {
        User user = new User();
        user.setUsername(registrationUserModel.getUsername());
        user.setEmail(registrationUserModel.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserModel.getPassword()));
        user.setRoles(new ArrayList<>());
        user.getRoles().add(addRole("NOT_VERIFIED"));
        return user;
    }

    private String generateUrl(HttpServletRequest request) {
        return "http://"
                + request.getServerName() + ":" +
                request.getServerPort() +
                request.getContextPath();
    }

    private String generateVerificationTokenUrl(String applicationUrl, Token token) {
        return applicationUrl +
                "/verifyRegistration?token=" +
                token.getToken();
    }

    private String generateChangePasswordTokenUrl(String generateUrl, Token token) {
        return generateUrl
                + "/savePassword?token="
                + token.getToken();
    }

    private boolean checkIfOldPasswordIsValid(String oldPassword, User user) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
}