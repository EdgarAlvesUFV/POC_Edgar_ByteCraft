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
        {"cwe_id":"CWE-250", "description":"Execution with Unnecessary Privileges"},
        {"cwe_id":"CWE-330", "description":"Use of Insufficiently Random Values"},
        {"cwe_id":"CWE-798", "description":"Use of Hard-coded Credentials"},
        {"cwe_id":"CWE-312", "description":"Cleartext Storage of Sensitive Information"}
    ]
}

=== CÓDIGO-FONTE ===
package app.configuration;

import app.security.JwtAuthFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        .requestMatchers(
                                "/misterios/**",
                                "/login",
                                "/professores/cadastrar"
                        ).permitAll()

                        .requestMatchers(
                                "/turma/professor/turmas"
                        ).authenticated()

                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
package app.controller;

import app.model.Professor;
import app.repository.ProfessorRepository;
import app.service.JwtService;
import app.service.AutenticacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final AutenticacaoService autenticacaoService;
    private final ProfessorRepository professorRepository;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            Optional<String> tokenOptional = autenticacaoService.autenticarProfessor(
                    loginRequest.getEmail(),
                    loginRequest.getSenha()
            );

            if (tokenOptional.isPresent()) {
                String token = tokenOptional.get();

                Optional<Professor> professorOpt = professorRepository.findByEmail(loginRequest.getEmail());
                if (professorOpt.isEmpty()) {
                    return ResponseEntity.status(404)
                            .body(Map.of("erro", "Professor não encontrado."));
                }

                Professor professor = professorOpt.get();

                return ResponseEntity.ok(Map.of(
                        "mensagem", "Login realizado com sucesso.",
                        "id", professor.getId(),
                        "email", professor.getEmail(),
                        "token", token
                ));
            } else {
                return ResponseEntity.status(401)
                        .body(Map.of("erro", "Email ou senha inválidos."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Falha inesperada no servidor: " + e.getMessage()));
        }
    }

    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }
}
package app.controller;

import app.model.dto.MetricasProgressoDto;
import app.service.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metricas")
@RequiredArgsConstructor
public class MetricaController {

    private final MetricasService metricaService;

    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<MetricasProgressoDto> getMetricasPorSessao(@PathVariable Long sessaoId) {
        MetricasProgressoDto dto = metricaService.calcularMetricaSessao(sessaoId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<Map<String, Object>> getMetricasPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(metricaService.calcularMetricaTurma(turmaId));
    }

    @GetMapping("/professora/{professorId}/turmas")
    public ResponseEntity<List<Map<String, Object>>> getMetricasTurmasProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(metricaService.calcularMetricasPorProfessor(professorId));
    }

    @GetMapping("/turma/{turmaId}/graficos")
    public ResponseEntity<Map<String, Object>> getGraficosPorTurma(@PathVariable Long turmaId) {
        Map<String, Object> graficos = metricaService.gerarGraficosPorTurma(turmaId);
        return ResponseEntity.ok(graficos);
    }

}
package app.controller;

import app.model.Misterio;
import app.model.Pista;
import app.model.Resposta;
import app.model.dto.AvaliacaoResponseDto;
import app.model.dto.PistaDto;
import app.model.dto.MisterioResponseDto;
import app.model.dto.AvaliacaoRequestDto;
import app.service.MisterioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/misterio")

public class MisterioController {

    private final MisterioService misterioService;


    @GetMapping("/{id}/misterio")
    public ResponseEntity<MisterioResponseDto> listarMisterioSessao(@PathVariable("id") Long sessaoId) {
        try {
            MisterioResponseDto misterio = misterioService.listarMisterioSessao(sessaoId);
            return ResponseEntity.ok(misterio);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{id}/resposta")
    public ResponseEntity<?> avaliarResposta(
            @PathVariable("id") Long sessaoId,
            @RequestBody @Valid AvaliacaoRequestDto avaliacaoDto) {
        try {
            var resultado = misterioService.validarResposta(sessaoId, avaliacaoDto);
            return ResponseEntity.ok(resultado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
package app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pergunta")
public class PerguntaController {
}
package app.controller;

import app.model.dto.PistaUsoCreateDto;
import app.service.PistaUsoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pistauso")
@RequiredArgsConstructor
public class PistaUsoController {

    private final PistaUsoService pistaUsoService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody PistaUsoCreateDto dto) {
        Long id = pistaUsoService.registrarPistaUso(dto);
        return ResponseEntity.ok(
                java.util.Map.of(
                        "status", "ok",
                        "id", id
                )
        );
    }
}
package app.controller;

import app.model.dto.ProfessorRequestDTO;
import app.model.dto.ProfessorResponseDTO;
import app.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ProfessorResponseDTO cadastrarProfessor(@RequestBody @Valid ProfessorRequestDTO dto) {
        return service.cadastrar(dto);
    }
}
package app.controller;


import app.exception.ResourceNotFoundException;
import app.model.dto.MetricasProgressoDto;
import app.model.dto.ProgressoCreateDto;
import app.service.MetricasService;
import app.service.ProgressoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("/progresso")
@RequiredArgsConstructor
public class ProgressoController {

    private final MetricasService metricasService;
    private  final ProgressoService progressoService;

    @PostMapping
    public ResponseEntity<?> registrarOuAtualizar(@RequestBody ProgressoCreateDto dto) {
        Long id = progressoService.registrarOuAtualizar(dto);
        return ResponseEntity.ok(
                java.util.Map.of(
                        "status", "ok",
                        "id", id
                )
        );
    }
}
package app.controller;

import app.model.Misterio;
import app.model.Sessao;
import app.model.dto.SessaoMisterioResponseDTO;
import app.model.dto.SessaoRequestDTO;
import app.model.dto.SessaoResponseDTO;
import app.service.MisterioService;
import app.service.SessaoService;
import app.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessao")
public class SessaoController {

    private final SessaoService sessaoService;
    private final MisterioService misterioService;

    @PostMapping("/{idTurma}")
    public ResponseEntity<SessaoMisterioResponseDTO> iniciarSessao(
            @PathVariable Long idTurma,
            @RequestBody SessaoRequestDTO sessaoRequestDTO
    ) {
        List<Misterio> misterioList = misterioService.sortearMisterios(sessaoRequestDTO.n_misterios());
        SessaoMisterioResponseDTO sessaoResponse = sessaoService.iniciarNovoJogo(idTurma, misterioList);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sessaoResponse.idSessao())
                .toUri();

        return ResponseEntity.created(location).body(sessaoResponse);
    }

@PutMapping("/{idSessao}/finalizar")
    public ResponseEntity<SessaoResponseDTO> finalizarSessao(@PathVariable Long idSessao){
        Sessao sessaoFinalizada = sessaoService.finalizarSessao(idSessao);
        SessaoResponseDTO responseDTO = new SessaoResponseDTO(sessaoFinalizada);
        return ResponseEntity.ok(responseDTO);
}

}
package app.controller;

import app.model.dto.TentativaCreateDto;
import app.service.TentativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tentativas")
@RequiredArgsConstructor
public class TentativaController {

    private final TentativaService tentativaService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody TentativaCreateDto dto) {
        Long id = tentativaService.registrarTentativa(dto);
        return ResponseEntity.ok().body(
                java.util.Map.of(
                        "status", "ok",
                        "id", id
                )
        );
    }
}

package app.controller;

import app.model.Professor;
import app.model.Turma;
import app.model.dto.TurmaRequestDto;
import app.model.dto.TurmaResponseDto;
import app.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/turma")
public class TurmaController {

    private final TurmaService turmaService;

    @PostMapping
    public ResponseEntity<?> cadastrarTurma(@AuthenticationPrincipal Professor professorAutenticado,@RequestBody @Valid TurmaRequestDto turmaRequestDto) {
        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Turma turmaCriada = turmaService.cadastrarTurma(turmaRequestDto, professorAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(turmaCriada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/professor/turmas")
    public ResponseEntity<List<TurmaResponseDto>> listarTurmasDoProfessor(@AuthenticationPrincipal Professor professorAutenticado) {
        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<TurmaResponseDto> listaTurma = turmaService.listarTurmas(professorAutenticado);
        return ResponseEntity.ok(listaTurma);
    }



    @GetMapping
    public ResponseEntity<List<TurmaResponseDto>> listarTurmas() {
        List<TurmaResponseDto> turmas = turmaService.listarTodasTurmas();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDto> buscarTurma(@PathVariable Long id) {
        TurmaResponseDto turma = turmaService.buscarTurma(id);
        return ResponseEntity.ok(turma);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarTurma(@AuthenticationPrincipal Professor professorAutenticado, @PathVariable Long id) {
        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            turmaService.deletarTurma(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível excluir a turma: existem registros vinculados.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarTurma(
            @AuthenticationPrincipal Professor professorAutenticado,
            @PathVariable Long id,
            @RequestBody @Valid TurmaRequestDto turmaRequestDto) {

        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            TurmaResponseDto turma = turmaService.modificarTurma(id, turmaRequestDto, professorAutenticado);
            return ResponseEntity.ok(turma);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
package app.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Já existe um professor cadastrado com o email: " + email);
    }
}
package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
package app.model.dto;

public record AvaliacaoRequestDto(
       int sessaoId,
       String resposta)
{}
package app.model.dto;

public record AvaliacaoResponseDto(
    String status,
    String mensagem,
    Boolean isResolvido,
    )
{}
package app.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDto(
        @NotBlank
        @Email
        String email,

        @NotEmpty
        String senha)
{

}
package app.model.dto;

public record LoginResponseDto (String token)
{

}
package app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricasProgressoDto {
    private long totalResolvidos;
    private double mediaEstrelas;
    private double mediaTentativas;
}
package app.model.dto;

import java.util.List;

public record MisterioResponseDto(
        Long id,
        String enunciado,
        List<String> pistas,
        List<PistaDto> alternativas
) {}
package app.model.dto;

public record MisterioResumoDto(
        Long misterioID,
        double estrelas,
        int pontuacao,
        long tempoResolucaoMs,
        int pistasUsadas,
        String medalhaConquista
) {}
package app.model.dto;

import app.model.Misterio;
import app.model.Pista;
import app.model.Resposta;
import java.util.List;

public record MisterioSessaoDTO(
        Long idMisterio,
        String titulo,
        Long sessaoMisterioId,
        List<Pista> pistas,
        List<Resposta> respostas
) {
    public static MisterioSessaoDTO from(Misterio misterio, Long sessaoMisterioId) {
        return new MisterioSessaoDTO(
                misterio.getId(),
                misterio.getTitulo(),
                sessaoMisterioId,
                misterio.getPistas(),
                misterio.getRespostas()
        );
    }
}
package app.model.dto;

public record PistaDto(
    Long Id,
    String tipo,
    )
{}
package app.model.dto;

public record PistaUsoCreateDto(
        Long sessaoMisterioId,
        Long pistaId,
        Integer ordem,
        String tipoPista
) {}
package app.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfessorRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}
package app.model.dto;

public record ProfessorResponseDTO(
        Long id,
        String nome,
        String email
) {}
package app.model.dto;

public record ProgressoCreateDto(
        Integer pontuacaoObtida,
        Long tempoResolucaoMs,
        Boolean resolvido
) {}
package app.model.dto;

import java.util.List;

public record SessaoMisterioResponseDTO(
        Long idSessao,
        Long turmaId,
        List<MisterioSessaoDTO> misterios
) {}
package app.model.dto;

public record SessaoRequestDTO(int n_misterios) {
}
package app.model.dto;

import app.model.Sessao;
import java.time.LocalDateTime;

public record SessaoResponseDTO(
        Long sessaoId,
        Long turmaId,
        String status,
        LocalDateTime dataInicio
) {
    public SessaoResponseDTO(Sessao sessao) {
        this(
                sessao.getIdSessao(),
                sessao.getTurma().getId(),
                sessao.getStatusSessao().name(),
                LocalDateTime.now()
        );
    }
}
package app.model.dto;

import java.time.LocalDateTime;

public record TentativaCreateDto(
        Long sessaoId,
        Long misterioId,
        Boolean correta,
) {}
package app.model.dto;

import jakarta.validation.constraints.NotBlank;

public record TurmaRequestDto(
          @NotBlank(message = "nome é obrigatório")
          String nome,

          int ano,

          @NotBlank(message = "turno é obrigatório")
          String turno) {

}
package app.model.dto;

import jakarta.validation.constraints.NotBlank;

public record TurmaResponseDto(
          @NotBlank(message = "id é obrigatório")
          long id,

          @NotBlank(message = "nome é obrigatório")
          String nome,

          int ano,

          @NotBlank(message = "turno é obrigatório")
          String turno) {

}
package app.model;

import java.util.List;

public class Misterio {
    private Long id;
    private String titulo;
    private List<Pista> pistas;
    private List<Resposta> respostas;

    public Long getId(){
        return id;
    }
    public String getTitulo(){
        return titulo;
    }

    public List<Pista> getPistas(){
        return pistas;
    }

    public List<Resposta> getRespostas(){
        return respostas;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public void setPistas(List<Pista> pistas){
        this.pistas = pistas;
    }
    

    public void setRespostas(List<Resposta> respostas){
        this.respostas = respostas;
    }

}
package app.model;

import java.util.List; 

public class Pergunta {
    private int id;
    private String texto;
    private String imagemUrl;
    private String audioUrl;
    private List<String> alternativas;

    public int getId() {
        return id;
    }

    public String getTexto(){
        return texto;
    }

    public String getImagemUrl(){
        return imagemUrl;
    }

    public String getAudioUrl(){
        return audioUrl;
    }

    public List<String> getAlternativas(){
        return alternativas;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }

    public void setImagemUrl(String image){
        this.imagemUrl = image;
    }

    public void setAudioUrl(String audio){
        this.audioUrl = audio;
    }

    public void setAlternativas(List<String> alternativas){
        this.alternativas = alternativas;
    }

}
package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Pista {

    private Long id;

    private String conteudo;

    private String tipo;

    @JsonIgnore
    private Misterio misterioID;


    public Long getId(){ return id; }
    public String getConteudo(){ return conteudo; }
    public String getTipo(){ return tipo; }
    public Misterio getMisterioID(){ return misterioID; }

    public void setId(Long id){ this.id = id; }
    public void setConteudo(String conteudo){ this.conteudo = conteudo; }
    public void setTipo(String tipo){ this.tipo = tipo; }
    public void setMisterioID(Misterio misterio){ this.misterioID = misterio; }

}
package app.model;

import java.time.LocalDateTime;

public class PistaUso {
    private Long id;
    private Long progressoId;
    private Long sessaoMisterioId;
    private Long pistaId;
    private Integer ordem;
    private LocalDateTime dataHora;
    private String tipoPista;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProgressoId() { return progressoId; }
    public void setProgressoId(Long progressoId) { this.progressoId = progressoId; }

    public Long getSessaoMisterioId() { return sessaoMisterioId; }
    public void setSessaoMisterioId(Long sessaoMisterioId) { this.sessaoMisterioId = sessaoMisterioId; }

    public Long getPistaId() { return pistaId; }
    public void setPistaId(Long pistaId) { this.pistaId = pistaId; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getTipoPista() { return tipoPista; }
    public void setTipoPista(String tipoPista) { this.tipoPista = tipoPista; }
}