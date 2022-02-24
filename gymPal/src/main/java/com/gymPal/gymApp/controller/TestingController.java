package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TestingController {

    UserService userService;


    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/admin/hello")
    public String yea(Principal principal){
        return "Hello "+principal.getName();
    }

    @PostMapping("/admin/addRole")
    public ResponseEntity<?> addRole(@RequestParam("username")String username, @RequestParam("role")String role){
        return ResponseEntity.ok().body(userService.addRoleToUser(username,role));
    }
}
