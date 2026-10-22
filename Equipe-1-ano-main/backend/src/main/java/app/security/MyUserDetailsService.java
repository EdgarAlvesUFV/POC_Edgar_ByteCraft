package app.security;

import app.service.AutenticacaoService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final AutenticacaoService autenticacaoService;

    public MyUserDetailsService(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        boolean existe = autenticacaoService.emailExiste(email); // você pode criar esse método
        if (!existe) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
        return new User(email, "", Collections.emptyList());
    }
}
