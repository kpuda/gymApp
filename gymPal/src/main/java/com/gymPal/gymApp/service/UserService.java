package com.gymPal.gymApp.service;

import com.gymPal.gymApp.entity.Role;
import com.gymPal.gymApp.model.PasswordModel;
import com.gymPal.gymApp.model.RegistrationUserModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserService {
    ResponseEntity registerUser(RegistrationUserModel registrationUserModel, HttpServletRequest request);

    ResponseEntity verifyRegistration(String token);

    ResponseEntity changePasssword(PasswordModel passwordModel, HttpServletRequest request);

    ResponseEntity savePassword(String token, PasswordModel passwordModel);

    ResponseEntity resetPassword(PasswordModel passwordModel, HttpServletRequest request);

    Role saveRole(Role role);

    ResponseEntity addRoleToUser(String username, String rolename);

    Object resendVerificationToken(RegistrationUserModel email, HttpServletRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    ResponseEntity getUsers();
}
