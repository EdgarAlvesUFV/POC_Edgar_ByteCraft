package com.pingo.service.interfaces;

import org.springframework.stereotype.Service;

import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;

@Service
public interface UserProgressService {
    public String saveProgress(UserProgressRequestDTO request);

    public UserProgressResponseDTO getUserProgress(String nickname);
}
