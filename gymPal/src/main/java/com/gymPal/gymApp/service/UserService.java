package com.gymPal.gymApp.service;

import com.gymPal.gymApp.entity.Role;
import com.gymPal.gymApp.model.PasswordModel;
import com.gymPal.gymApp.model.RegistrationUserModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserService {
    String registerUser(RegistrationUserModel registrationUserModel, HttpServletRequest request);

    String verifyRegistration(String token);

    String changePasssword(PasswordModel passwordModel, HttpServletRequest request);

    String savePassword(String token, PasswordModel passwordModel);

    String resetPassword(PasswordModel passwordModel, HttpServletRequest request);

    Role saveRole(Role role);

    String addRoleToUser(String username, String rolename);

    Object resendVerificationToken(RegistrationUserModel email, HttpServletRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
