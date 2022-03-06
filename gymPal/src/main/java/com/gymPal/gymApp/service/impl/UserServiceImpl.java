package com.gymPal.gymApp.service.impl;

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
import org.springframework.http.ResponseEntity;
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
import java.util.*;

import static com.gymPal.gymApp.utils.ServerConsts.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    TokenRepository tokenRepository;
    PasswordEncoder passwordEncoder;
    JavaMailSenderImpl javaMailSender;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JavaMailSenderImpl javaMailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Transactional
    public ResponseEntity registerUser(RegistrationUserModel registrationUserModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(registrationUserModel.getEmail());
        User userFromUsername = userRepository.findByUsername(registrationUserModel.getUsername());
        String url;
        if (user != null || userFromUsername != null) {
            if (user != null) {
                return ResponseEntity.unprocessableEntity().body(EMAIL_TAKEN);
            } else {
                return ResponseEntity.unprocessableEntity().body(USERNAME_TAKEN);
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
        return ResponseEntity.accepted().body(USER_REGISTERED);
    }

    @Override
    @Transactional
    public ResponseEntity verifyRegistration(String token) {
        Token registrationToken = tokenRepository.findByToken(token);
        if (registrationToken == null) {
            return ResponseEntity.status(404).body(TOKEN_INVALID);
        } else {
            if (registrationToken.getExpirationDate().getTime() < new Date().getTime()) {
                return ResponseEntity.status(400).body(TOKEN_EXPIRED);
            } else if (registrationToken.getTokenValid().equals(TokenValid.TOKEN_USED)) {
                return ResponseEntity.status(400).body(TOKEN_USED);
            } else {
                if (!registrationToken.getTokenType().equals(TokenType.NEW_ACCOUNT_VERIFICATION)) {
                    return ResponseEntity.status(400).body(TOKEN_INVALID);
                }

                User user = registrationToken.getUser();
                if (user.isEnabled() && !user.getRoles().contains("NOT_VERIFIED")) {
                    return ResponseEntity.status(400).body(USER_VERIFIED_ALREADY);
                } else {
                    registrationToken.setTokenValid(TokenValid.TOKEN_USED);
                    user.setEnabled(true);
                    Role role = roleRepository.findByName("ROLE_USER");
                    Collection<Role> roleCollection = new ArrayList<>();
                    roleCollection.add(role);
                    user.setRoles(roleCollection);
                    userRepository.save(user);
                    tokenRepository.save(registrationToken);
                }
            }
        }
        return ResponseEntity.ok().body(USER_VERIFIED);
    }

    @Override
    public ResponseEntity resendVerificationToken(RegistrationUserModel userModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(userModel.getEmail());
        String url;
        if (user == null) {
            return ResponseEntity.status(404).body(EMAIL_NOT_FOUND);
        } else {
            String token = UUID.randomUUID().toString();
            Token registrationToken = new Token(user, token, TokenType.NEW_ACCOUNT_VERIFICATION, TokenValid.TOKEN_VALID);
            url = generateVerificationTokenUrl(generateUrl(request), registrationToken);
            tokenRepository.save(registrationToken);
            // TODO sendEmailWithVerificationToken(user, url);
            log.info("Url: {}", url);
            log.info("Token: {}", token);
        }
        return ResponseEntity.ok().body(TOKEN_SENT);
    }

    @Override
    public ResponseEntity changePasssword(PasswordModel passwordModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        String url;
        if (user == null) {
            return ResponseEntity.unprocessableEntity().body(EMAIL_NOT_FOUND);
        } else {
            if (!checkIfOldPasswordIsValid(passwordModel.getPassword(), user)) {
                return ResponseEntity.unprocessableEntity().body(PASSWORD_OLD_INCORRECT);
            } else {
                user.setPassword(passwordModel.getNewPassword());
                userRepository.save(user);
            }
        }
        return ResponseEntity.ok().body(PASSWORD_CHANGED);
    }

    @Override
    public ResponseEntity resetPassword(PasswordModel passwordModel, HttpServletRequest request) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        String url;
        if (user == null) {
            return ResponseEntity.unprocessableEntity().body(USER_NOT_FOUND);
        } else {
            String token = UUID.randomUUID().toString();
            Token resetPasswordToken = new Token(user, token, TokenType.FORGOT_PASSWORD, TokenValid.TOKEN_VALID);
            url = generateChangePasswordTokenUrl(generateUrl(request), resetPasswordToken);
            tokenRepository.save(resetPasswordToken);
            //TODO sendEmailWithResetPasswordToken(user,url);
            log.info("Url: {}", url);
            log.info("Token: {}", token);
        }
        return ResponseEntity.ok().body(TOKEN_SENT);
    }

    @Override
    public ResponseEntity savePassword(String token, PasswordModel passwordModel) {
        Token changePasswordToken = tokenRepository.findByToken(token);
        if (changePasswordToken == null) {
            return ResponseEntity.unprocessableEntity().body(TOKEN_NOT_FOUND);
        } else {
            if (changePasswordToken.getExpirationDate().getTime() < new Date().getTime()) {
                return ResponseEntity.unprocessableEntity().body(TOKEN_EXPIRED);
            } else if (!changePasswordToken.getTokenType().equals(TokenType.FORGOT_PASSWORD)) {
                return ResponseEntity.unprocessableEntity().body(TOKEN_INVALID);
            } else if (changePasswordToken.getTokenValid().equals(TokenValid.TOKEN_USED)) {
                return ResponseEntity.unprocessableEntity().body(TOKEN_USED);
            } else {
                User user = userRepository.findByEmail(changePasswordToken.getUser().getEmail());
                changePasswordToken.setTokenValid(TokenValid.TOKEN_USED);
                user.setPassword(passwordEncoder.encode(passwordModel.getNewPassword()));
                userRepository.save(user);
                tokenRepository.save(changePasswordToken);
            }
        }
        return ResponseEntity.ok().body(PASSWORD_CHANGED);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public ResponseEntity addRoleToUser(String username, String rolename) {
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(rolename.toUpperCase());
        if (role != null) {
            user.getRoles().add(role);
            userRepository.save(user);
            return ResponseEntity.ok().body(USER_ROLE_ADDED);
        } else {
            return ResponseEntity.unprocessableEntity().body(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt;
        if (username.contains("@")) {
            log.info("User is being fetched by a mail");
            userOpt = Optional.ofNullable(userRepository.findByEmail(username));
        } else {
            log.info("User is being fetched by a username");
            userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        }
        if (userOpt.isEmpty()) {
            log.info("User is empty");
            return (UserDetails) userOpt.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));//todo
        } else {
            log.info("User is loaded");

            User user = userOpt.get();
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    //TODO is refresh token necessary?
    /*@Override
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
    }*/

    @Override
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.accepted().body(userRepository.findAll());
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