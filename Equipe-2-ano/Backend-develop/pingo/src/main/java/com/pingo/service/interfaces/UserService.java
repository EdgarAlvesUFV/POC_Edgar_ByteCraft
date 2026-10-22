package com.pingo.service.interfaces;

import org.springframework.stereotype.Service;

import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;


@Service
public interface UserService {
    public UserResponseDTO authenticate(UserRequestDTO userRequestDTO);
}
