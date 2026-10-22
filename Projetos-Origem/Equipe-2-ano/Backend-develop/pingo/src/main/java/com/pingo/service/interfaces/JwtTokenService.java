package com.pingo.service.interfaces;

import org.springframework.stereotype.Service;
import com.pingo.config.UserDetailsImpl; 

@Service
public interface JwtTokenService {

    public String generateToken(UserDetailsImpl user);

    public String getSubjectFromToken(String token);

}