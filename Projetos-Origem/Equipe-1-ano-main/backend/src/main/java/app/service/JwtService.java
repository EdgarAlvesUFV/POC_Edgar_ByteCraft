package app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // 🔐 Chave segura com pelo menos 32 caracteres (256 bits)
    private static final String SECRET = "super_chave_segura_com_mais_de_32_caracteres_123456";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 horas

    private final SecretKey secretKey;

    public JwtService() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("A chave JWT deve ter pelo menos 32 bytes (256 bits).");
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        System.out.printf("[INFO] JWT inicializado com %d bits.%n", keyBytes.length * 8);
    }

    // ✅ Gera token com subject = email
    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Valida token e retorna o e-mail (subject)
    public String validarToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // <--- Aqui estava o erro
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // ✅ Método auxiliar (opcional)
    public String extrairUsername(String token) {
        return validarToken(token);
    }
}
