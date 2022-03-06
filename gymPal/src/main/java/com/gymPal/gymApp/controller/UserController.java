package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
