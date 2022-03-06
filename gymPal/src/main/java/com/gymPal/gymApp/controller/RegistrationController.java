package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.model.PasswordModel;
import com.gymPal.gymApp.model.RegistrationUserModel;
import com.gymPal.gymApp.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final UserServiceImpl userService;

    public RegistrationController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationUserModel registrationUserModel, final HttpServletRequest request) {
        return userService.registerUser(registrationUserModel, request);

    }

    @PostMapping("/verifyRegistration")
    public ResponseEntity<?> verifyRegistration(@RequestParam("token") String token) {
        return userService.verifyRegistration(token);
    }

    @GetMapping("/resendVerificationToken")
    public ResponseEntity<?> resendVerificationToken(@RequestBody RegistrationUserModel userModel,final HttpServletRequest request){
        return userService.resendVerificationToken(userModel,request);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordModel passwordModel, final HttpServletRequest request) {
        return userService.changePasssword(passwordModel, request);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordModel passwordModel, final HttpServletRequest request) {
        return userService.resetPassword(passwordModel, request);
    }

    @PostMapping("/savePassword")
    public ResponseEntity<?> savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
        return userService.savePassword(token, passwordModel);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
       //TODO userService.refreshToken(request,response);
    }



}


