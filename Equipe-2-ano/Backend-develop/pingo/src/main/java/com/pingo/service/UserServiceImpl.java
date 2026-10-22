package com.pingo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.pingo.config.SecurityConfig;
import com.pingo.config.UserDetailsImpl;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;
import com.pingo.model.entity.User;
import com.pingo.service.interfaces.JwtTokenService;
import com.pingo.service.interfaces.UserService;


@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtTokenService jwtTokenService;

    @Autowired
    private final SecurityConfig securityConfiguration;

    private final UserDAO userDAO;
    
    public UserServiceImpl(AuthenticationManager authenticationManager, UserDAO userDAO, JwtTokenService jwtTokenService, SecurityConfig securityConfiguration) {
        this.authenticationManager = authenticationManager;
        this. jwtTokenService = jwtTokenService;
        this.securityConfiguration = securityConfiguration;
        this.userDAO = userDAO;
    }
    @Override
    public UserResponseDTO authenticate(UserRequestDTO userRequestDTO) {
        // Busca usuário pelo nickname
        User user = userDAO.findByNickname(userRequestDTO.getNickname());
        UserDetailsImpl userDetails;

        if (user == null) {
            // Registro de um novo usuário
            user = new User();
            user.setNickname(userRequestDTO.getNickname());
            user.setPassword(securityConfiguration.passwordEncoder().encode(userRequestDTO.getPassword()));
            userDAO.save(user);
            userDetails = new UserDetailsImpl(user);
        } else {
            // Login do Usuário
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userRequestDTO.getNickname(), userRequestDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            userDetails = (UserDetailsImpl) authentication.getPrincipal();
        }
        // Retorna DTO com nickname e token
        return new UserResponseDTO(user.getNickname(), jwtTokenService.generateToken(userDetails));
    }
}
