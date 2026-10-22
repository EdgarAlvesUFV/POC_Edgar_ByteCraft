Você é um analista de segurança de software especializado em análise estática de código.

Sua tarefa é analisar o código-fonte fornecido e determinar se ele contém vulnerabilidades de segurança.

Siga estas instruções rigorosamente:

Identifique vulnerabilidades de segurança com base em padrões conhecidos, como CWE (Common Weakness Enumeration).
Relate apenas vulnerabilidades reais. Não especule nem invente problemas.
Se nenhuma vulnerabilidade for encontrada, retorne uma lista vazia.
Para cada vulnerabilidade encontrada, forneça:
"cwe_id": Identificador CWE (ex: CWE-89)
"vulnerability_type": Descrição curta da vulnerabilidade
"severity": Um dos valores ["LOW", "MEDIUM", "HIGH", "CRITICAL"]
"line": Número da linha onde o problema ocorre (ou a melhor aproximação)
Não inclua explicações fora do JSON.
Garanta que a saída seja um JSON válido.

Formato de saída:

{
"vulnerabilities": [
{
"cwe_id": "CWE-XXX",
"vulnerability_type": "Descrição",
"severity": "LOW|MEDIUM|HIGH|CRITICAL",
"line": <número>,
"justification": "Explicação técnica curta"
}
]
}

Agora analise o seguinte código:
=== Código Fonte ===
package com.bytecraft.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonManagedReference; // em Sala

//Classe do Pablo
@Entity
@Table(name = "salas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeTurma;

    @Column(name = "codigo_unico", nullable = false, unique = true)
    private Byte codigoUnico;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<Aluno> alunos = new HashSet<>();
}
package com.bytecraft.repository;

import com.bytecraft.enums.NivelDificuldadeEnum;
import com.bytecraft.model.Aluno;
import com.bytecraft.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Atualizar nível considerando apelido + sala
    @Modifying
    @Transactional
    @Query("UPDATE Aluno a SET a.nivel = :nivel WHERE a.apelido = :apelido AND a.sala = :sala")
    int atualizaNivel(NivelDificuldadeEnum nivel, String apelido, Sala sala);

    // Buscar aluno por apelido e sala (único por sala)
    @Query("SELECT a FROM Aluno a WHERE a.apelido = :apelido AND a.sala = :sala")
    Optional<Aluno> buscarPorApelidoESala(String apelido, Sala sala);

    // Buscar aluno só por apelido
    @Query("SELECT a FROM Aluno a WHERE a.apelido = :apelido")
    Optional<Aluno> buscarPorApelido(String apelido);
    
    // Atualizar pontuação considerando id do aluno
    @Modifying
    @Transactional
    @Query("UPDATE Aluno a SET a.pontuacao = :pontuacao WHERE a.id = :id")
    int atualizaPontuacao(@Param("id") Long id, @Param("pontuacao") int pontuacao);

    // Buscar alunos por sala
    @Query("SELECT a FROM Aluno a WHERE a.sala.id = :id")
    Optional<List<Aluno>> buscarPorSala(@Param("id") Long id);
}
package com.bytecraft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bytecraft.enums.NivelDificuldadeEnum;
import com.bytecraft.model.Pergunta;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {

    //Querry da Lara
    @Query("SELECT p FROM Pergunta p WHERE p.nivelDificuldade = :nivel")
    List<Pergunta> buscarPorNivel(@Param("nivel") NivelDificuldadeEnum nivel);

       
}
package com.bytecraft.repository;

import com.bytecraft.model.Professor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ProfessorRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public int salvaProfessor(Professor professor) {
        try {
            em.persist(professor);
            return 1; // sucesso
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // falha
        }
    }

    public Professor buscarPorNome(String nomeDeUsuario) {
        try {
            return em.createQuery(
                            "SELECT p FROM Professor p WHERE p.nomeDeUsuario = :nome", Professor.class)
                    .setParameter("nome", nomeDeUsuario)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // caso não encontre
        }
    }

    public Long buscarProfessorID(String nomeDeUsuario) {
        try {
            return em.createQuery(
                            "SELECT p.id FROM Professor p WHERE p.nomeDeUsuario = :nome", Long.class)
                    .setParameter("nome", nomeDeUsuario)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // caso não encontre
        }
    }
}
package com.bytecraft.repository;

import com.bytecraft.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    // Buscar sala pelo código único
    @Query("SELECT s FROM Sala s WHERE s.codigoUnico = :codigoUnico")
    Optional<Sala> buscarPorCodigo(Byte codigoUnico);

    // Buscar todas as salas
    @Query("SELECT s FROM Sala s")
    List<Sala> buscarSalas();

    // Buscar sala pelo nome da turma
    @Query("SELECT s FROM Sala s WHERE s.nomeTurma = :nomeTurma")
    Optional<Sala> buscarSala(String nomeTurma);

    // Buscar ID da sala pelo código único
    @Query("SELECT s.id FROM Sala s WHERE s.codigoUnico = :codigoUnico")
    Long buscarSalaID(@Param("codigoUnico") Byte codigoUnico);

    @Query("SELECT p.sala FROM Professor p WHERE p.id = :id")
    Sala buscarPorProfessor(@Param("id") Long id);

}
package com.bytecraft.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth

                // 🔓 LIBERAR O FRONT React (HTML + JS + CSS)
                .requestMatchers("/", "/index.html", "/static/**", "/assets/**", "/favicon.ico").permitAll()

                // 🔓 LIBERAR SUAS APIs
                .requestMatchers("/api/alunos/**").permitAll()
                .requestMatchers("/api/professores/**").permitAll()
                .requestMatchers("/api/salas/**").permitAll()
                .requestMatchers("/api/perguntas/**").permitAll()

                // 🔒 QUALQUER OUTRO ENDPOINT (ex: /admin)
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔓 ACEITAR FRONT DEV + FRONT NO WAR
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3030", // React dev server
                "http://localhost:8080"  // React empacotado no WAR
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
package com.bytecraft.service;

import com.bytecraft.enums.NivelDificuldadeEnum;
import com.bytecraft.model.Aluno;
import com.bytecraft.model.Sala;
import com.bytecraft.repository.AlunoRepository;
import com.bytecraft.repository.SalaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final SalaRepository salaRepository;

    // Vincula aluno à sala (cria se não existir)
    public Aluno vincularAlunoASala(String apelido, Byte codigoSala) {
        String apelidoTrim = apelido != null ? apelido.trim() : null;

        Sala sala = salaRepository.buscarPorCodigo(codigoSala)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada"));

        Optional<Aluno> existente = alunoRepository.buscarPorApelidoESala(apelidoTrim, sala);
        if (existente.isPresent()) return existente.get();

        Aluno novo = new Aluno();
        novo.setApelido(apelidoTrim);
        novo.setSala(sala);
        novo.setPontuacao(0);
        novo.setModoHistoriaCompleto(false); // ✅ Inicializar como false
        return alunoRepository.save(novo);
    }

    @Transactional
    public boolean registraNivel(NivelDificuldadeEnum nivel, String apelido, Byte codigoSala) {
        String apelidoTrim = apelido != null ? apelido.trim() : null;

        Sala sala = salaRepository.buscarPorCodigo(codigoSala)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada"));

        return alunoRepository.buscarPorApelidoESala(apelidoTrim, sala)
                .map(aluno -> {
                    aluno.setNivel(nivel);
                    alunoRepository.save(aluno);
                    return true;
                })
                .orElse(false);
    }

    // Busca aluno em uma sala
    public Aluno findAluno(String apelido, Byte codigoSala) {
        String apelidoTrim = apelido != null ? apelido.trim() : null;

        Sala sala = salaRepository.buscarPorCodigo(codigoSala)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada"));

        return alunoRepository.buscarPorApelidoESala(apelidoTrim, sala)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    // Calcula pontuação com base no tempo (em segundos) e pontos recebidos do frontend
    public int calculaPontuacao(int pontos, int segundos) {
        if (pontos < 0) pontos = 0;
        if (segundos < 0) segundos = 0;
    
        // Fórmula RN22: aplica bônus se T < 600s (10 minutos)
        double fatorTempo = Math.max(1.0, 2.0 - ((double) segundos / 600.0));
        double pontuacaoFinal = pontos * fatorTempo;
    
        return (int) Math.round(pontuacaoFinal);
    }
    
    @Transactional
    public boolean registraPontuacao(String apelido, int segundos, int pontuacao, Byte codigoSala) {
        try {
            Aluno aluno = findAluno(apelido, codigoSala);
            int pontuacaoFinal = calculaPontuacao(pontuacao, segundos);
            int rowsUpdated = alunoRepository.atualizaPontuacao(aluno.getId(), pontuacaoFinal);
            return rowsUpdated > 0;
        } catch (Exception e) {
            System.err.println("Erro ao registrar pontuação: " + e.getMessage());
            return false;
        }
    }

    // ✅ NOVO: Salvar progresso do Modo História
    @Transactional
    public boolean salvarProgresso(String apelido, Byte codigoSala, Boolean modoHistoriaCompleto) {
        try {
            String apelidoTrim = apelido != null ? apelido.trim() : null;

            System.out.println("💾 [Service] Salvando progresso: apelido=" + apelidoTrim + 
                             ", codigoSala=" + codigoSala + 
                             ", modoHistoriaCompleto=" + modoHistoriaCompleto);

            Sala sala = salaRepository.buscarPorCodigo(codigoSala)
                    .orElseThrow(() -> new RuntimeException("Sala não encontrada"));

            Optional<Aluno> alunoOpt = alunoRepository.buscarPorApelidoESala(apelidoTrim, sala);
            
            if (alunoOpt.isEmpty()) {
                System.err.println("❌ [Service] Aluno não encontrado");
                return false;
            }

            Aluno aluno = alunoOpt.get();
            aluno.setModoHistoriaCompleto(modoHistoriaCompleto);
            alunoRepository.save(aluno);

            System.out.println("✅ [Service] Progresso salvo com sucesso!");
            return true;

        } catch (Exception e) {
            System.err.println("❌ [Service] Erro ao salvar progresso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Buscar alunos por sala
    public Optional<List<Aluno>> getAlunosPorSala(Long id){
        return alunoRepository.buscarPorSala(id);
    }
}package com.bytecraft.service;

import com.bytecraft.model.Professor;
import com.bytecraft.model.Sala;
import com.bytecraft.DTO.ProfessorDTO;
import com.bytecraft.DTO.SalaDTO;
import com.bytecraft.repository.ProfessorRepository;
import com.bytecraft.repository.SalaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final SalaService salaService;
    private final SalaRepository salaRepository;
    private final PasswordEncoder passwordEncoder;

    // Cadastra professor e cria sala se não existir
    public ProfessorDTO cadastrarProfessor(String nome, String senha, String nomeTurma) {
        // Normaliza entradas
        String nomeTrim = nome != null ? nome.trim() : null;
        String senhaTrim = senha != null ? senha.trim() : null; // opcional, mas evita espaços no início/fim
        String nomeTurmaTrim = nomeTurma != null ? nomeTurma.trim() : null;

        // Validações básicas
        if (nomeTrim == null || nomeTrim.isEmpty() || senhaTrim == null || senhaTrim.isEmpty() || nomeTurmaTrim == null || nomeTurmaTrim.isEmpty()) {
            throw new IllegalArgumentException("Nome, senha e nome da turma são obrigatórios");
        }
        if (senhaTrim.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        // Verifica duplicidade
        if (professorRepository.buscarPorNome(nomeTrim) != null) {
            throw new IllegalStateException("Nome de usuário já existe");
        }

        // Cria sala associada
        Sala sala = salaService.criaSala(nomeTurmaTrim);

        // Cria e persiste professor
        Professor professor = new Professor();
        professor.setNomeDeUsuario(nomeTrim);
        professor.setSenha(passwordEncoder.encode(senhaTrim));
        professor.setSala(sala);

        professorRepository.salvaProfessor(professor);

        // Retorna DTO garantindo que o código da sala esteja presente
        return new ProfessorDTO(professor.getNomeDeUsuario(), SalaDTO.fromEntity(sala));
    }

    public Professor autenticarProfessor(String nome, String senha) {
        String nomeTrim = nome != null ? nome.trim() : null;
        String senhaTrim = senha != null ? senha.trim() : null;

        if (nomeTrim == null || nomeTrim.isEmpty() || senhaTrim == null || senhaTrim.isEmpty()) {
            throw new IllegalArgumentException("Nome e senha são obrigatórios");
        }

        Professor professor = professorRepository.buscarPorNome(nomeTrim);
        if (professor != null && passwordEncoder.matches(senhaTrim, professor.getSenha())) {
            return professor;
        }
        return null;
    }

    public Sala getSalaByNomeProfessor(String nomeDeUsuario) {
        Long id = professorRepository.buscarProfessorID(nomeDeUsuario);
        Sala sala = salaRepository.buscarPorProfessor(id);
        return sala;
    }
}
package com.bytecraft.service;

import com.bytecraft.model.Aluno;
import com.bytecraft.model.Sala;
import com.bytecraft.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final AlunoService alunoService;

    // Gera código único entre 10 e 99, sem repetição
    private Byte geraCodigoUnico() {
        Byte codigo;
        do {
            codigo = (byte) (10 + (int) (Math.random() * 90));
        } while (salaRepository.buscarPorCodigo(codigo).isPresent());
        return codigo;
    }

    // Cria sala se não existir
    public Sala criaSala(String nomeTurma) {
        return salaRepository.buscarSala(nomeTurma)
                .orElseGet(() -> {
                    Sala nova = Sala.builder()
                            .nomeTurma(nomeTurma)
                            .codigoUnico(geraCodigoUnico())
                            .build();
                    return salaRepository.save(nova);
                });
    }

    // Busca sala por código
    public Optional<Sala> getSalaByCodigo(Byte codigo) {
        return salaRepository.buscarPorCodigo(codigo);
    }

    // Retorna todas as salas
    public List<Sala> getTodasSalas() {
        return salaRepository.findAll();
    }

    // Busca ID da sala pelo código único
    private Long getSalaID(Byte codigoUnico) {
        return salaRepository.buscarSalaID(codigoUnico);
    }

    // Retorna ranking da turma (alunos ordenados por pontuação)
    public Optional<List<Aluno>> getRankingTurma(Byte codigoUnico) {
        Long idSala = getSalaID(codigoUnico);

        if (idSala == null) {
            return Optional.empty();
        }

        Optional<List<Aluno>> alunosOpt = alunoService.getAlunosPorSala(idSala);

        if (alunosOpt.isEmpty()) {
            return Optional.empty();
        }

        List<Aluno> alunos = alunosOpt.get();

        // Ordena pela pontuação (maior primeiro)
        alunos.sort(Comparator.comparingInt(Aluno::getPontuacao).reversed());

        return Optional.of(alunos);
    }

    public Sala getSalaByProfessor(Long id){
        Sala sala = salaRepository.buscarPorProfessor(id);
        return sala;
    }
}
package com.bytecraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ByteCraftApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ByteCraftApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ByteCraftApplication.class);
    }
}
