Você é um analista de segurança de software especializado em análise estática de código.

Sua tarefa é analisar o código-fonte fornecido juntamente com uma lista pré-definida de vulnerabilidades potenciais.

Siga estas instruções rigorosamente:

Você receberá:
O código-fonte de uma classe
Uma lista de vulnerabilidades previamente identificadas por ferramentas de análise estática
Para cada vulnerabilidade na lista:
Verifique se ela está realmente presente no código
Confirme, refine ou rejeite a vulnerabilidade
Procure ativamente por vulnerabilidades adicionais além da lista fornecida.
Para cada vulnerabilidade (tanto confirmada quanto recém-identificada), forneça:
"cwe_id": Identificador CWE (ex: CWE-89)
"vulnerability_type": Descrição curta
"severity": Um de ["LOW", "MEDIUM", "HIGH", "CRITICAL"]
"line": Número da linha (ou a melhor aproximação)
"status": Um de ["CONFIRMED", "REJECTED", "NEW"]
"justification": Explicação breve do motivo pelo qual a vulnerabilidade foi confirmada, rejeitada ou identificada
Regras:
Não assuma vulnerabilidades sem evidência no código
Seja preciso e conservador ao confirmar vulnerabilidades
Se uma vulnerabilidade listada não for encontrada, marque como REJECTED
Classifique como NEW apenas se não estiver presente na lista fornecida
Mantenha as justificativas concisas e técnicas
Se nenhuma vulnerabilidade for encontrada, retorne uma lista vazia
Retorne APENAS JSON válido. Não inclua explicações fora do JSON.

Formato de saída:

{
"vulnerabilities": [
{
"cwe_id": "CWE-XXX",
"vulnerability_type": "Descrição",
"severity": "LOW|MEDIUM|HIGH|CRITICAL",
"line": <número>,
"status": "CONFIRMED|REJECTED|NEW",
"justification": "Explicação técnica curta"
}
]
}

Agora analise as seguintes entradas:

=== LISTA DE VULNERABILIDADES ===
{
    [
        {"cwe_id":"CWE-352", "description":"Cross-Site Request Forgery"},
        {"cwe_id":"CWE-489", "description":"Active Debug Code"},
        {"cwe_id":"CWE-330", "description":"Use of Insufficiently Random Values"},
        {"cwe_id":"CWE-209", "description":"Generation of Error Message Containing Sensitive Information"}
    ]
}

=== CÓDIGO-FONTE ===
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