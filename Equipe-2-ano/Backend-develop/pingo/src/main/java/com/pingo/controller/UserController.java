package com.pingo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;
import com.pingo.service.interfaces.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public UserResponseDTO authenticate(@RequestBody UserRequestDTO UserRequestDTO) {
        return userService.authenticate(UserRequestDTO);
    }
}
