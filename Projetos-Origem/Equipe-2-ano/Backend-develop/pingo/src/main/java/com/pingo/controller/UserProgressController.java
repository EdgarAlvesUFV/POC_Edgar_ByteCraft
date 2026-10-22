package com.pingo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;
import com.pingo.service.interfaces.UserProgressService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/progress")
public class UserProgressController {
    UserProgressService userProgressService;
    
    public UserProgressController(UserProgressService userProgressService) {
        this.userProgressService = userProgressService;
    }
    
    @PostMapping("/save")
    public String saveProgress(@RequestBody UserProgressRequestDTO request) {
        return userProgressService.saveProgress(request);
    }

    @GetMapping("/get")
    public UserProgressResponseDTO getUserProgress(@RequestParam String nickname) {
        return userProgressService.getUserProgress(nickname);
    }

}
