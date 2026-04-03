package com.ratelimit.user.controller;

import com.ratelimit.user.dto.UserDTO;
import com.ratelimit.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public UserDTO getUser(@RequestParam("keycloakId") String keycloakId) {
        return userService.findOrCreateByKeycloakId(keycloakId);
    }
}

