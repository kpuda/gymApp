package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.model.PasswordModel;
import com.gymPal.gymApp.model.RegistrationUserModel;
import com.gymPal.gymApp.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RegistrationController {

    private final UserServiceImpl userService;



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationUserModel registrationUserModel, final HttpServletRequest request) {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/register").toUriString());
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
        userService.refreshToken(request,response);
    }



}


