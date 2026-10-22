package com.pingo.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pingo.config.SecurityConfig;
import com.pingo.config.UserDetailsImpl;
import com.pingo.dao.UserDAOImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;
import com.pingo.model.entity.User;
import com.pingo.service.interfaces.JwtTokenService;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDAOImpl userDAO;

    @Mock
    private SecurityConfig securityConfiguration;

    @Mock 
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void authenticate_WhenNewUser_ShouldCreateNewUser() {
        PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class); 
        when(securityConfiguration.passwordEncoder()).thenReturn(passwordEncoder);

        // given
        when(userDAO.findByNickname("test")).thenReturn(null); // simula que não existe
        when(securityConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(jwtTokenService.generateToken(any())).thenReturn("fakeToken");

        // when
        UserResponseDTO response = userServiceImpl.authenticate(new UserRequestDTO("test", "123"));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("test");
        verify(userDAO, times(1)).save(any(User.class)); // salva apenas uma vez
    }

    @Test
    void authenticate_WhenExistingUser_ShouldAuthenticate() {
        // given
        User existingUser = new User();
        existingUser.setNickname("test");
        existingUser.setPassword("encoded123");
        when(userDAO.findByNickname("test")).thenReturn(existingUser); // simula que já existe
        when(jwtTokenService.generateToken(any())).thenReturn("fakeToken");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(new UserDetailsImpl(existingUser));
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        // when
        UserResponseDTO response = userServiceImpl.authenticate(new UserRequestDTO("test", "123"));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("test");
        verify(userDAO, never()).save(any(User.class)); // não deve salvar
    }

}
