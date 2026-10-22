package com.bytecraft.controller;

import com.bytecraft.DTO.AlunoDTO;
import com.bytecraft.enums.NivelDificuldadeEnum;
import com.bytecraft.model.Aluno;
import com.bytecraft.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    // Login ou registro
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            String apelido = payload.get("apelido");
            String codigoSalaStr = payload.get("codigoSala");

            if (apelido != null) {
                apelido = apelido.trim().replaceAll("\\s+", " ");
            }
            if (codigoSalaStr != null) {
                codigoSalaStr = codigoSalaStr.trim();
            }

            if (apelido == null || apelido.isEmpty()) {
                resposta.put("erro", "Apelido é obrigatório");
                return ResponseEntity.badRequest().body(resposta);
            }
            if (codigoSalaStr == null || codigoSalaStr.isEmpty()) {
                resposta.put("erro", "Código da sala é obrigatório");
                return ResponseEntity.badRequest().body(resposta);
            }

            Byte codigoSala = Byte.parseByte(codigoSalaStr);
            Aluno aluno = alunoService.vincularAlunoASala(apelido, codigoSala);

            resposta.put("apelido", aluno.getApelido());
            resposta.put("nivel", aluno.getNivel() != null ? aluno.getNivel().name() : null);
            resposta.put("modoHistoriaCompleto", aluno.getModoHistoriaCompleto());
            resposta.put("sala", Map.of(
                    "id", aluno.getSala().getId(),
                    "nomeTurma", aluno.getSala().getNomeTurma(),
                    "codigo", aluno.getSala().getCodigoUnico()
            ));

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            resposta.put("erro", "Erro no login: " + e.getMessage());
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    // Retorna níveis disponíveis
    @GetMapping("/niveis")
    public ResponseEntity<List<String>> getNiveis() {
        List<String> niveis = List.of(
                NivelDificuldadeEnum.FACIL.name(),
                NivelDificuldadeEnum.MEDIO.name(),
                NivelDificuldadeEnum.DIFICIL.name()
        );
        return ResponseEntity.ok(niveis);
    }

    // Atualiza nível do aluno
    @PostMapping("/{apelido}/registrarNivel")
    public ResponseEntity<?> registrarNivel(@PathVariable String apelido,
                                            @RequestBody Map<String, String> payload) {
        try {
            apelido = apelido != null ? apelido.trim().replaceAll("\\s+", " ") : null;
            String codigoSalaStr = payload.get("codigoSala");
            String nivelStr = payload.get("nivel");

            if (codigoSalaStr != null) codigoSalaStr = codigoSalaStr.trim();
            if (nivelStr != null) nivelStr = nivelStr.trim();

            if (apelido == null || apelido.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Apelido é obrigatório"));
            if (codigoSalaStr == null || codigoSalaStr.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Código da sala é obrigatório"));
            if (nivelStr == null || nivelStr.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Nível é obrigatório"));

            Byte codigoSala = Byte.parseByte(codigoSalaStr);
            NivelDificuldadeEnum nivel = NivelDificuldadeEnum.valueOf(nivelStr.toUpperCase());

            boolean atualizado = alunoService.registraNivel(nivel, apelido, codigoSala);
            if (!atualizado)
                return ResponseEntity.badRequest().body(Map.of("erro", "Aluno não encontrado"));

            Aluno aluno = alunoService.findAluno(apelido, codigoSala);
            AlunoDTO alunoDTO = AlunoDTO.fromEntity(aluno);

            return ResponseEntity.ok(alunoDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/setPontuacao")
    public ResponseEntity<?> salvaPontuacao(@RequestBody Map<String, String> payload) {
        try {
            String apelido = payload.get("apelido");
            String codigoSalaStr = payload.get("codigoSala");
            String pontosStr = payload.get("pontos");
            String segundosStr = payload.get("segundos");

            if (apelido != null) apelido = apelido.trim().replaceAll("\\s+", " ");
            if (codigoSalaStr != null) codigoSalaStr = codigoSalaStr.trim();
            if (pontosStr != null) pontosStr = pontosStr.trim();
            if (segundosStr != null) segundosStr = segundosStr.trim();

            if (apelido == null || apelido.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Apelido é obrigatório"));
            if (codigoSalaStr == null || codigoSalaStr.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Código da sala é obrigatório"));
            if (pontosStr == null || pontosStr.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Pontos são obrigatórios"));
            if (segundosStr == null || segundosStr.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Tempo (segundos) é obrigatório"));

            Byte codigoSala = Byte.parseByte(codigoSalaStr);
            int pontos = Integer.parseInt(pontosStr);
            int segundos = Integer.parseInt(segundosStr);

            boolean atualizado = alunoService.registraPontuacao(apelido, segundos, pontos, codigoSala);

            return ResponseEntity.ok(Map.of("atualizado", atualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Erro ao registrar pontuação: " + e.getMessage()));
        }
    }

    // ✅ NOVO: Salvar progresso do Modo História
    @PostMapping("/progresso")
    public ResponseEntity<?> salvarProgresso(@RequestBody Map<String, Object> payload) {
        try {
            String apelido = (String) payload.get("apelido");
            Integer codigoSala = (Integer) payload.get("codigoSala");
            Boolean modoHistoriaCompleto = (Boolean) payload.get("modoHistoriaCompleto");

            System.out.println("📥 Salvando progresso: apelido=" + apelido + 
                             ", codigoSala=" + codigoSala + 
                             ", modoHistoriaCompleto=" + modoHistoriaCompleto);

            if (apelido != null) apelido = apelido.trim().replaceAll("\\s+", " ");

            if (apelido == null || apelido.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("erro", "Apelido é obrigatório"));
            if (codigoSala == null)
                return ResponseEntity.badRequest().body(Map.of("erro", "Código da sala é obrigatório"));
            if (modoHistoriaCompleto == null)
                return ResponseEntity.badRequest().body(Map.of("erro", "Status do modo história é obrigatório"));

            Byte codigoSalaByte = codigoSala.byteValue();
            boolean atualizado = alunoService.salvarProgresso(apelido, codigoSalaByte, modoHistoriaCompleto);

            if (!atualizado) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Aluno não encontrado"));
            }

            System.out.println("✅ Progresso salvo com sucesso!");
            return ResponseEntity.ok(Map.of("sucesso", true, "modoHistoriaCompleto", modoHistoriaCompleto));

        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar progresso: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao salvar progresso: " + e.getMessage()));
        }
    }

    // ✅ NOVO: Obter progresso do aluno
    @GetMapping("/progresso/{apelido}/{codigoSala}")
    public ResponseEntity<?> obterProgresso(
            @PathVariable String apelido,
            @PathVariable Byte codigoSala) {
        try {
            System.out.println("📥 Buscando progresso: apelido=" + apelido + ", codigoSala=" + codigoSala);

            if (apelido != null) apelido = apelido.trim().replaceAll("\\s+", " ");

            Aluno aluno = alunoService.findAluno(apelido, codigoSala);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("modoHistoriaCompleto", aluno.getModoHistoriaCompleto());
            resposta.put("apelido", aluno.getApelido());
            resposta.put("pontuacao", aluno.getPontuacao());

            System.out.println("✅ Progresso encontrado: modoHistoriaCompleto=" + aluno.getModoHistoriaCompleto());
            return ResponseEntity.ok(resposta);

        } catch (RuntimeException e) {
            System.err.println("❌ Aluno não encontrado: " + e.getMessage());
            return ResponseEntity.status(404).body(Map.of("erro", "Aluno não encontrado"));
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar progresso: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao buscar progresso: " + e.getMessage()));
        }
    }
}package com.bytecraft.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;

import com.bytecraft.enums.NivelDificuldadeEnum;
import com.bytecraft.model.Pergunta;
import com.bytecraft.service.PerguntaService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/perguntas")
@RequiredArgsConstructor
public class PerguntaController {

    private final PerguntaService perguntaService;

    // DTO interno para o payload recebido no corpo da requisição
    @Data
    public static class PerguntaRequest {
        private int quantidade;
        private NivelDificuldadeEnum nivelDificuldade;
    }

    @PostMapping("/list")
    public ResponseEntity<List<Pergunta>> getListaPerguntas(@RequestBody PerguntaRequest request) {
        try {
            List<Pergunta> perguntas = perguntaService.getPerguntasPorNivel(
                request.getQuantidade(),
                request.getNivelDificuldade()
            );
            return ResponseEntity.ok(perguntas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Enum inválido
        }
    }

    // Novo endpoint para importar CSV
    /* 
    @PostMapping("/import")
    public ResponseEntity<String> importarCsv(@RequestParam("arquivos") List<MultipartFile> arquivos) {
        try {
            for (MultipartFile arquivo : arquivos) {
                perguntaService.importarCSV(arquivo);
            }
            return ResponseEntity.ok("Todos os CSVs foram importados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao importar CSV: " + e.getMessage());
        }
    }*/
}
package com.bytecraft.controller;

import com.bytecraft.DTO.ProfessorDTO;
import com.bytecraft.model.Professor;
import com.bytecraft.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    // 🔹 Método utilitário para normalizar texto
    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    // Cadastro do professor + criação de sala automática
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, String> payload) {
        String nome = normalizar(payload.get("nome"));
        String senha = normalizar(payload.get("senha"));
        String nomeTurma = normalizar(payload.get("nomeTurma"));

        if (nome.isEmpty() || senha.isEmpty() || nomeTurma.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome, senha e nome da turma são obrigatórios."));
        }

        try {
            ProfessorDTO professorDTO = professorService.cadastrarProfessor(nome, senha, nomeTurma);
            return ResponseEntity.ok(professorDTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // Autenticação do professor
    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody Map<String, String> payload) {
        String nome = normalizar(payload.get("nome"));
        String senha = normalizar(payload.get("senha"));

        if (nome.isEmpty() || senha.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome e senha são obrigatórios."));
        }

        Professor professor = professorService.autenticarProfessor(nome, senha);
        if (professor != null) {
            ProfessorDTO professorDTO = ProfessorDTO.fromEntity(professor);
            return ResponseEntity.ok(professorDTO);
        } else {
            return ResponseEntity.status(401).body(Map.of("erro", "Nome de usuário ou senha inválidos."));
        }
    }

    @GetMapping("/{nomeProfessor}/sala")
    public ResponseEntity<?> getSala(@PathVariable String nomeProfessor) {
        try {
            // Chama o service para buscar a sala do professor
            var salaDTO = professorService.getSalaByNomeProfessor(nomeProfessor);

            if (salaDTO != null) {
                return ResponseEntity.ok(salaDTO);
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Professor ou sala não encontrados."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
package com.bytecraft.controller;

import com.bytecraft.DTO.SalaDTO;
import com.bytecraft.model.Sala;
import com.bytecraft.model.Aluno;
import com.bytecraft.DTO.AlunoDTO;
import com.bytecraft.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    // Criar sala
    @PostMapping("/criar")
    public ResponseEntity<?> criarSala(@RequestBody Map<String, String> payload) {
        String nomeTurma = payload.get("nomeTurma");
        if (nomeTurma == null || nomeTurma.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome da turma é obrigatório"));
        }
        Sala sala = salaService.criaSala(nomeTurma);
        SalaDTO salaDTO = SalaDTO.fromEntity(sala);
        return ResponseEntity.ok(salaDTO);
    }
    
    // Verifica se há salas cadastradas
    @GetMapping("/existe")
    public ResponseEntity<Map<String, Object>> existeSala() {
        List<SalaDTO> salas = salaService.getTodasSalas().stream()
                .map(SalaDTO::fromEntity)
                .toList();
        boolean existe = !salas.isEmpty();
        return ResponseEntity.ok(Map.of(
                "existe", existe,
                "salas", salas
        ));
    }

    // Obter ranking da turma
    @GetMapping("/getRanking/{codigoUnico}")
    public ResponseEntity<?> getRankingTurma(@PathVariable Byte codigoUnico) {
        Optional<List<Aluno>> alunosOpt = salaService.getRankingTurma(codigoUnico);

        if (alunosOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Sala não encontrada ou sem alunos."));
        }

        // Converte cada Aluno para AlunoDTO
        List<AlunoDTO> ranking = alunosOpt.get().stream()
                .map(AlunoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/{codigoUnico}/detalhes")
    public ResponseEntity<?> getDetalhes(@PathVariable Byte codigoUnico) {
        try {
            // Busca a sala pelo código único
            Optional<Sala> salaOpt = salaService.getSalaByCodigo(codigoUnico);

            if (salaOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Sala não encontrada."));
            }

            // Converte a sala para SalaDTO, incluindo nomes dos alunos
            SalaDTO salaDTO = SalaDTO.fromEntity(salaOpt.get());

            return ResponseEntity.ok(salaDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

   
}    package com.bytecraft.DTO;

import com.bytecraft.model.Aluno;

public record AlunoDTO(String apelido, String nivel, int pontuacao, SalaDTO sala) {

    public static AlunoDTO fromEntity(Aluno aluno) {
        if (aluno == null) return null;

        String nivel = (aluno.getNivel() != null) ? aluno.getNivel().name() : null;

        return new AlunoDTO(
            aluno.getApelido(),
            nivel,
            aluno.getPontuacao(),
            SalaDTO.fromEntity(aluno.getSala())
        );
    }
}
package com.bytecraft.DTO;

import com.bytecraft.model.Professor;

public record ProfessorDTO(String nomeDeUsuario, SalaDTO sala) {

    public static ProfessorDTO fromEntity(Professor professor) {
        return new ProfessorDTO(
            professor.getNomeDeUsuario(),
            SalaDTO.fromEntity(professor.getSala())
        );
    }
}
package com.bytecraft.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bytecraft.model.Sala;
import com.bytecraft.model.Aluno;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaDTO {
    private Long id;
    private String nomeTurma;
    private Byte codigoUnico;
    private List<String> nomesAlunos; // lista de nomes dos alunos

    // Converte entidade para DTO e já popula a lista de nomes dos alunos
    public static SalaDTO fromEntity(Sala sala) {
        if (sala == null) return null;

        List<String> nomes = null;
        if (sala.getAlunos() != null) {
            nomes = sala.getAlunos().stream()
                        .map(Aluno::getApelido)
                        .collect(Collectors.toList());
        }

        return new SalaDTO(
            sala.getId(),
            sala.getNomeTurma(),
            sala.getCodigoUnico(),
            nomes
        );
    }
}
package com.bytecraft.enums;


public enum NivelDificuldadeEnum {
    FACIL,
    MEDIO,
    DIFICIL
}
package com.bytecraft.model;

import com.bytecraft.enums.NivelDificuldadeEnum;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(
    name = "alunos",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"apelido", "sala_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String apelido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private NivelDificuldadeEnum nivel;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    @JsonBackReference
    private Sala sala;

    @Column(nullable = false)
    private int pontuacao;

    // ✅ NOVO CAMPO: Indica se o aluno completou o Modo História
    @Column(name = "modo_historia_completo", nullable = false)
    private Boolean modoHistoriaCompleto = false;
}package com.bytecraft.model;

import com.bytecraft.enums.NivelDificuldadeEnum;
import jakarta.persistence.*;
import lombok.*;

//Classe da Lara adaptada
@Entity
@Table(name = "pergunta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enunciado", nullable = false, length = 1000)
    private String enunciado;

    @Lob
    @Column(name = "imagem", columnDefinition = "LONGTEXT")
    private String imagem;

    @Column(name = "alternativa_a", nullable = false, length = 500)
    private String alternativaA;

    @Column(name = "alternativa_b", nullable = false, length = 500)
    private String alternativaB;

    @Column(name = "alternativa_c", nullable = false, length = 500)
    private String alternativaC;

    @Column(name = "alternativa_d", nullable = false, length = 500)
    private String alternativaD;

    @Column(name = "alternativa_correta", nullable = false, length = 500)
    private String alternativaCorreta; // ✅ nova propriedade

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_dificuldade", nullable = false)
    private NivelDificuldadeEnum nivelDificuldade;
}
package com.bytecraft.model;

import jakarta.persistence.*;
import lombok.*;

//Classe do Pedro Paulo adaptada
@Entity
@Table(name = "professores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "nome_de_usuario", nullable = false)
    private String nomeDeUsuario;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Column(name = "senha", nullable = false)
    private String senha;

    @OneToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;
}
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
