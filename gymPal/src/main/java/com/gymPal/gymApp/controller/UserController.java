package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@RequestMapping("/api/user")
@RequestMapping("/testing")
@AllArgsConstructor
public class UserController {

    UserRepository userRepository;
        @GetMapping("/getAllUsers")
    public List<?> getUsers(){
            return userRepository.findAll();
        }
}
