package app.security;

import app.model.Professor;
import app.repository.ProfessorRepository;
import app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final ProfessorRepository professorRepository; // ✅ injeta o repositório

    public JwtAuthFilter(
            JwtService jwtService,
            MyUserDetailsService userDetailsService,
            ProfessorRepository professorRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.professorRepository = professorRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = null;

        try {
            email = jwtService.validarToken(token);
        } catch (Exception e) {
            System.err.println("[ERROR] Erro ao validar token: " + e.getMessage());
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Professor> optProfessor = professorRepository.findByEmail(email);

            if (optProfessor.isPresent()) {
                Professor professor = optProfessor.get();
                System.out.println("[DEBUG] Professor encontrado: " + professor.getId() + " - " + professor.getEmail());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                professor, null, Collections.emptyList()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.err.println("[WARN] Nenhum professor encontrado com o email: " + email);
            }
        }

        chain.doFilter(request, response);
    }
}

