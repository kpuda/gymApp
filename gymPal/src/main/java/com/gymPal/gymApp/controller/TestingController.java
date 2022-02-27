package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;

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
    public String yea(Principal principal, final HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        Arrays.stream(cookies).forEach(System.out::println);
        return "Hello "+principal.getName();
    }

    @PostMapping("/admin/addRole")
    public ResponseEntity<?> addRole(@RequestParam("username")String username, @RequestParam("role")String role){
        return userService.addRoleToUser(username,role);
    }
}
