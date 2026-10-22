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

    /**
     * 🔹 Métricas de uma sessão específica (usada no jogo)
     */
    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<MetricasProgressoDto> getMetricasPorSessao(@PathVariable Long sessaoId) {
        MetricasProgressoDto dto = metricaService.calcularMetricaSessao(sessaoId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 🔹 Métricas de uma turma (média entre todos os alunos da turma)
     */
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<Map<String, Object>> getMetricasPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(metricaService.calcularMetricaTurma(turmaId));
    }

    /**
     * 🔹 Métricas agregadas de todas as turmas de um professor
     */
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

    /*@GetMapping(value = "/{sessaold}/metricas", produces = "application/json")
    public ResponseEntity<?> getMetricasProgresso(@PathVariable Long sessaold) {

        try {
            MetricasProgressoDto dto = metricasService.calcularMetricaSessao(sessaold);

            return ResponseEntity.ok(dto);

        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "erro");
            errorResponse.put("codigo", 404);
            errorResponse.put("mensagem", "Recurso não encontrado.");
            errorResponse.put("detalhes", e.getMessage());


            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (app.exception.InternalServerErrorException e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "erro");
            errorResponse.put("codigo", 500);
            errorResponse.put("mensagem", "Ocorreu um erro interno no servidor.");
            errorResponse.put("detalhes",e.getMessage());


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }*/
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

    /**
     * Endpoint para iniciar uma nova sessao de jogo para uma turma
     * parametro - idTurma, recebido como uma variavel na URL
     * return - ResponseEntity com status 201 Created, um header location e o corpo da resposta formatado pelo SessaoRepository
     */

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

    /**
     * endpoint para finalizar (deletar) uma sessao de jogo existente
     * parametros - idSessao - o id da sessao que queremos finalizar
     * return - ResponseEntity com o status 200 OK e o corpo da sessao que foi finalizada(deletada)
     */

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

/**
 * Exceção para ser lançada quando uma regra de negócio é violada.
 * Mapeia para o status HTTP 422 Unprocessable Entity.
 */

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

/**
 * Exceção para ser lançada quando ocorre um erro inesperado no servidor.
 * Mapeia para o status HTTP 500 Internal Server Error.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }

    /**
     * Construtor crucial para erros 500.
     * Permite que a exceção original (ex: falha no banco) seja encapsulada.
     * @param message A mensagem de alto nível para o erro.
     * @param cause A exceção original (ex: DataAccessException).
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para ser lançada quando um recurso específico não é encontrado.
 * Mapeia para o status HTTP 404 Not Found.
 */
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

/**
 * DTO para formatar a resposta da criação de uma sessão
 */
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
package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashSenha;

    public Professor() {}

    public Professor(String nome, String email, String hashSenha) {
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashSenha() { return hashSenha; }
    public void setHashSenha(String hashSenha) { this.hashSenha = hashSenha; }
}
package app.model;

public class Progresso {
    private Long id;
    /** FK para sessao_misterios(id) */
    private Long sessaoMisterioId;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessaoMisterioId() { return sessaoMisterioId; }
    public void setSessaoMisterioId(Long sessaoMisterioId) { this.sessaoMisterioId = sessaoMisterioId; }

    public int getPontuacaoObtida() { return pontuacaoObtida; }
    public void setPontuacaoObtida(int pontuacaoObtida) { this.pontuacaoObtida = pontuacaoObtida; }

    public Long getTempoResolucaoMs() { return tempoResolucaoMs; }
    public void setTempoResolucaoMs(Long tempoResolucaoMs) { this.tempoResolucaoMs = tempoResolucaoMs; }

    public boolean isResolvido() { return resolvido; }
    public void setResolvido(boolean resolvido) { this.resolvido = resolvido; }
}
package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Resposta {
    private Long id;
    private String descricao;
    private int ordem;
    private String tipo;
    @JsonIgnore
    private Misterio misterioID;
    private Boolean status;
    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getOrdem() {
        return ordem;
    }

    public String getTipo() {
        return tipo;
    }

    public Misterio getMisterioID() {
        return misterioID;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setMisterioID(Misterio misterioID) {
        this.misterioID = misterioID;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
package app.model;

import java.util.List;

public class Sessao {
        private long IdSessao;
        private Turma turma;
        private List<Misterio> misterios;
        private StatusSessao statusSessao;

        public Sessao() {
        }

        public Sessao(Turma turma){
            this.turma = turma;
            this.statusSessao = StatusSessao.ATIVA;
        }

        public long getIdSessao() {
            return IdSessao;
        }

        public Turma getTurma() {
            return turma;
        }

        public List<Misterio> getMisterios() {
            return misterios;
        }

        public StatusSessao getStatusSessao() { return statusSessao; }
        public void setIdSessao(long idSessao) {
            IdSessao = idSessao;
        }

        public void setTurma(Turma turma) {
            this.turma = turma;
        }

        public void setMisterios(List<Misterio> misterios) {
            this.misterios = misterios;
        }

        public void setStatusSessao(StatusSessao statusSessao) { this.statusSessao = statusSessao; }
    }

package app.model;

public class SessaoMisterios {
    private Long id;
    private Sessao sessao;
    private Misterio misterio;
}
package app.model;

public enum StatusSessao {
    ATIVA,
    FINALIZADA
}
package app.model;

import java.time.LocalDateTime;

public class Tentativa {
    private Long id;
    private Long progressoId;
    private Long misterioId;
    private String alternativaEscolhida;
    private boolean correta;
    private Long tempoRespostaMs;
    private LocalDateTime dataHora;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getProgressoId() {return progressoId;}
    public void setProgressoId(Long progressoId) {this.progressoId = progressoId;}

    public Long getMisterioId() {return misterioId;}
    public void setMisterioId(Long misterioId) {this.misterioId = misterioId;}

    public String getAlternativaEscolhida() {return alternativaEscolhida;}
    public void setAlternativaEscolhida(String alternativaEscolhida) {this.alternativaEscolhida = alternativaEscolhida;}

    public boolean isCorreta() {return correta;}
    public void setCorreta(boolean correta) {this.correta = correta;}

    public Long getTempoRespostaMs() {return tempoRespostaMs;}
    public void setTempoRespostaMs(Long tempoRespostaMs) {this.tempoRespostaMs = tempoRespostaMs;}

    public LocalDateTime getDataHora() {return dataHora;}
    public void setDataHora(LocalDateTime dataHora) {this.dataHora = dataHora;}
}
package app.model;

import jakarta.persistence.*;

import java.util.Locale;

@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private String turno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_turmas_professor"))
    private Professor professor;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {this.nome = nome.toUpperCase();}
    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno){
        this.turno = turno.toUpperCase();
    }

    public Professor getProfessor() {
        return professor;
    }
    public void setProfessor(Professor professor){
        this.professor = professor;
    }
}
package app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MetricaRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 🔹 Calcula médias simples da turma
     */
    public Map<String, Object> calcularMetricasTurma(Long turmaId) {
        String sql = """
        SELECT 
            AVG(p.pontuacaoObtida) AS media_pontuacao,
            AVG(p.tempoResolucaoMs) AS media_tempo,
            SUM(CASE WHEN p.resolvido = TRUE THEN 1 ELSE 0 END) AS total_resolvidos,
            COUNT(DISTINCT p.id) AS total_misterios
        FROM progresso p
        INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
        INNER JOIN sessoes s ON sm.sessao_id = s.id_sessao
        WHERE s.turma_id = ?
    """;

        return jdbcTemplate.queryForMap(sql, turmaId);
    }


    /**
     * 🔹 Calcula métricas agregadas por turma de um professor
     */
    public List<Map<String, Object>> calcularMetricasProfessor(Long professorId) {
        String sql = """
            SELECT 
                t.id AS turmaId,
                t.nome AS nomeTurma,
                COALESCE(AVG(p.pontuacaoObtida), 0) AS mediaPontuacao,
                COALESCE(AVG(p.tempoResolucaoMs), 0) AS mediaTempo,
                SUM(CASE WHEN p.resolvido = TRUE THEN 1 ELSE 0 END) AS totalResolvidos,
                COUNT(p.id) AS totalMisterios
            FROM progresso p
            INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
            INNER JOIN sessoes s ON sm.sessao_id = s.id_sessao
            INNER JOIN turmas t ON s.turma_id = t.id
            INNER JOIN professores pr ON t.professor_id = pr.id
            WHERE pr.id = ?
            GROUP BY t.id, t.nome
            ORDER BY mediaPontuacao DESC
        """;

        return jdbcTemplate.queryForList(sql, professorId);
    }

    /**
     * 🔹 Calcula média de estrelas (baseada em quantidade de pistas usadas)
     */
    public double calcularMediaEstrelas(List<?> pistasUsadas) {
        if (pistasUsadas == null || pistasUsadas.isEmpty()) return 0;
        double total = 0;
        int count = 0;

        Map<Long, Long> pistasPorProgresso = pistasUsadas.stream()
                .collect(Collectors.groupingBy(p -> ((app.model.PistaUso)p).getProgressoId(), Collectors.counting()));

        for (long qtd : pistasPorProgresso.values()) {
            double estrelas = Math.max(0.0, 5.0 - (1.25 * (qtd - 1)));
            total += estrelas;
            count++;
        }

        return count > 0 ? total / count : 0;
    }

    /**
     * 🔹 Calcula média de tentativas por mistério resolvido
     */
    public double calcularMediaTentativas(List<?> tentativas, List<Long> progressoIds) {
        if (tentativas == null || tentativas.isEmpty()) return 0;
        long totalTentativas = tentativas.size();
        return progressoIds.isEmpty() ? 0 : (double) totalTentativas / progressoIds.size();
    }

    public List<Map<String, Object>> buscarDistribuicaoEstrelasPorTipoPista(Long turmaId) {
        String sql = """
            SELECT
              pu.tipo_pista,
              CASE
                WHEN p.pontuacaoObtida = 5 THEN 5
                WHEN p.pontuacaoObtida = 4 THEN 4
                WHEN p.pontuacaoObtida = 3 THEN 3
                WHEN p.pontuacaoObtida = 2 THEN 2
                ELSE 1
              END AS estrelas,
              COUNT(*) AS total
            FROM pistauso pu
            INNER JOIN progresso p
              ON pu.progresso_id = p.id
            INNER JOIN sessao_misterios sm
              ON p.sessao_misterio_id = sm.id
            INNER JOIN sessoes s
              ON sm.sessao_id = s.id_sessao
            WHERE s.turma_id = ?
            GROUP BY pu.tipo_pista, estrelas
            ORDER BY pu.tipo_pista, estrelas DESC;
            
        """;

        return jdbcTemplate.queryForList(sql, turmaId);
    }

    /**
     * 🔹 Tempo médio de resolução por tipo de pista (para gráfico de barras)
     */
    public List<Map<String, Object>> buscarTempoMedioPorTipoPista(Long turmaId) {
        String sql = """
            SELECT
              pu.tipo_pista,
              ROUND(AVG(p.tempoResolucaoMs)/1000, 2) AS tempoMedioSegundos
            FROM pistauso pu
            JOIN progresso p            ON pu.progresso_id = p.id
            JOIN sessao_misterios sm    ON p.sessao_misterio_id = sm.id
            JOIN sessoes s              ON sm.sessao_id = s.id_sessao
            WHERE s.turma_id = ?
            GROUP BY pu.tipo_pista
            ORDER BY tempoMedioSegundos DESC;
        """;

        return jdbcTemplate.queryForList(sql, turmaId);
    }
}
package app.repository;

import app.model.Misterio;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MisterioRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Misterio> MISTERIO_RM = (rs, i) -> {
        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("id"));
        misterio.setTitulo(rs.getString("titulo"));
        return misterio;
    };

    public List<Misterio> todosMisterios() {
        String sql  = """
            SELECT id, titulo
            FROM misterios
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM);
    }

    public List<Misterio> sorteiaMisterio(int limite) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            ORDER BY RAND() LIMIT ?
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM, limite);
    }

    public Optional<Misterio> buscaMisterio(Long id) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            WHERE id = ?
        """;
        try {
            Misterio misterio = jdbcTemplate.queryForObject(sql, MISTERIO_RM, id);
            return Optional.ofNullable(misterio);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Misterio> proximoMisterio(Long id) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            WHERE id > ?
            ORDER BY id ASC LIMIT 1
        """;
        try {
            Misterio misterio = jdbcTemplate.queryForObject(sql, MISTERIO_RM, id);
            return Optional.ofNullable(misterio);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM misterios";
        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return (total != null) ? total : 0;
    }


    /**
     * Retorna todos os mistérios vinculados a uma sessão
     */
    public List<Misterio> buscarPorSessao(Long sessaoId) {
        String sql = """
            SELECT m.id, m.titulo
            FROM misterios m
            INNER JOIN sessao_misterios sm ON m.id = sm.misterio_id
            WHERE sm.sessao_id = ?
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM, sessaoId);
    }

    /**
     * Vincula um mistério a uma sessão
     */
    public Long adicionarMisterioNaSessao(Long sessaoId, Long misterioId) {
        String sql = """
        INSERT INTO sessao_misterios (sessao_id, misterio_id)
        VALUES (?, ?)
    """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sessaoId);
            ps.setLong(2, misterioId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }


    /**
     * Remove o vínculo entre mistério e sessão
     */
    public void removerMisterioDaSessao(Long sessaoId, Long misterioId) {
        String sql = """
            DELETE FROM sessao_misterios
            WHERE sessao_id = ? AND misterio_id = ?
        """;
        jdbcTemplate.update(sql, sessaoId, misterioId);
    }

    /**
     * Remove todos os vínculos de uma sessão (resetar a sessão)
     */
    public void limparMisteriosDaSessao(Long sessaoId) {
        String sql = """
            DELETE FROM sessao_misterios
            WHERE sessao_id = ?
        """;
        jdbcTemplate.update(sql, sessaoId);
    }
}
package app.repository;

import app.model.Misterio;
import app.model.Pista;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PistaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Pista> PISTA_RM = (rs, rowNum) -> {
        Pista pista = new Pista();
        pista.setId(rs.getLong("id"));
        pista.setConteudo(rs.getString("conteudo"));
        pista.setTipo(rs.getString("tipo"));

        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("misterio_id"));
        pista.setMisterioID(misterio);

        return pista;
    };

    /**
     * Retorna todas as pistas vinculadas a um mistério
     */
    public List<Pista> retornaPistasVinculadasMisterio(Misterio misterio) {
        final String sql = """
            SELECT id, conteudo, tipo, misterio_id
            FROM pistas
            WHERE misterio_id = ?
        """;
        return jdbcTemplate.query(sql, PISTA_RM, misterio.getId());
    }

    /**
     * Busca uma pista específica pelo ID
     */
    public Optional<Pista> buscarPorId(Long id) {
        final String sql = """
            SELECT id, conteudo, tipo, misterio_id
            FROM pistas
            WHERE id = ?
        """;
        try {
            Pista pista = jdbcTemplate.queryForObject(sql, PISTA_RM, id);
            return Optional.ofNullable(pista);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void salvar(Pista pista) {
        final String sql = """
            INSERT INTO pistas (conteudo, tipo, misterio_id)
            VALUES (?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                pista.getConteudo(),
                pista.getTipo(),
                pista.getMisterioID().getId());
    }

    public void deletar(Long id) {
        final String sql = "DELETE FROM pistas WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
package app.repository;

import app.model.PistaUso;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PistaUsoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcTemplate plainJdbc;

    private static final RowMapper<PistaUso> PISTA_USO_ROW_MAPPER = (rs, rowNum) -> {
        PistaUso pu = new PistaUso();
        pu.setId(rs.getLong("id"));
        pu.setProgressoId(rs.getLong("progresso_id"));
        pu.setSessaoMisterioId(rs.getLong("sessao_misterio_id"));
        pu.setPistaId(rs.getLong("pista_id"));
        pu.setOrdem(rs.getInt("ordem"));
        pu.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        pu.setTipoPista(rs.getString("tipo_pista"));
        return pu;
    };

    public Long inserirPistaUso(Long progressoId,
                                Long sessaoMisterioId,
                                Long pistaId,
                                Integer ordem,
                                String tipoPista) {

        final String sql = """
            INSERT INTO pistauso 
                (progresso_id, sessao_misterio_id, pista_id, ordem, data_hora, tipo_pista)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        KeyHolder kh = new GeneratedKeyHolder();

        plainJdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, progressoId);
            ps.setObject(2, sessaoMisterioId);
            ps.setObject(3, pistaId);
            ps.setObject(4, ordem);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, tipoPista);
            return ps;
        }, kh);

        return kh.getKey() != null ? kh.getKey().longValue() : null;
    }

    public List<PistaUso> buscarListaProgressoID(List<Long> progressoIDs) {
        if (progressoIDs == null || progressoIDs.isEmpty()) {
            return Collections.emptyList();
        }

        final String sql = """
            SELECT * FROM pistauso
            WHERE progresso_id IN (:ids)
        """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", progressoIDs);

        return jdbcTemplate.query(sql, parameters, PISTA_USO_ROW_MAPPER);
    }
}
package app.repository;

import app.model.Professor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class ProfessorRepository {

    private final JdbcTemplate jdbc;

    public ProfessorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Professor> RM = new RowMapper<>() {
        @Override
        public Professor mapRow(ResultSet rs, int rowNum) throws SQLException {
            Professor p = new Professor();
            p.setId(rs.getLong("id"));
            p.setNome(rs.getString("nome"));
            p.setEmail(rs.getString("email"));
            p.setHashSenha(rs.getString("hash_senha"));
            return p;
        }
    };

    public Optional<Professor> findByEmailAndSenhaHash(String email, String senhaHash) {
        String sql = "SELECT id, nome, email, hash_senha FROM professores WHERE email = ? AND hash_senha = ?";
        try {
            Professor professor = jdbc.queryForObject(sql, (rs, rowNum) -> {
                Professor p = new Professor();
                p.setId(rs.getLong("id"));
                p.setEmail(rs.getString("email"));
                p.setHashSenha(rs.getString("hash_senha"));
                p.setNome(rs.getString("nome"));
                return p;
            }, email, senhaHash);
            return Optional.ofNullable(professor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM professores WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public Professor save(Professor professor) {
        String sql = """
            INSERT INTO professores (nome, email, hash_senha)
            VALUES (?, ?, ?)
        """;
        jdbc.update(sql, professor.getNome(), professor.getEmail(), professor.getHashSenha());
        return professor;
    }

    public Optional<Professor> findByEmail(String email) {
        String sql = "SELECT * FROM professores WHERE email = ?";
        return jdbc.query(sql, RM, email).stream().findFirst();
    }

}
package app.repository;

import app.model.Progresso;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProgressoRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Progresso> PROGRESSO_ROW_MAPPER = (rs, rowNum) -> {
        Progresso p = new Progresso();
        p.setId(rs.getLong("id"));
        p.setSessaoMisterioId(rs.getLong("sessao_misterio_id"));
        p.setPontuacaoObtida(rs.getInt("pontuacaoObtida"));
        p.setTempoResolucaoMs(rs.getLong("tempoResolucaoMs"));
        p.setResolvido(rs.getBoolean("resolvido"));
        return p;
    };

    public Optional<Progresso> buscarPorSessaoMisterio(Long sessaoMisterioId) {
        final String sql = """
            SELECT * FROM progresso
            WHERE sessao_misterio_id = ?
        """;
        var lista = jdbcTemplate.query(sql, PROGRESSO_ROW_MAPPER, sessaoMisterioId);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    public Long buscarSessaoMisterioId(Long sessaoId, Long misterioId) {
        final String sql = """
            SELECT id FROM sessao_misterios
            WHERE sessao_id = ? AND misterio_id = ?
            LIMIT 1
        """;
        var result = jdbcTemplate.queryForList(sql, Long.class, sessaoId, misterioId);
        return result.isEmpty() ? null : result.get(0);
    }

    public Long inserir(Progresso p) {
        final String sql = """
        INSERT INTO progresso (sessao_misterio_id, pontuacaoObtida, tempoResolucaoMs, resolvido)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            pontuacaoObtida = VALUES(pontuacaoObtida),
            tempoResolucaoMs = VALUES(tempoResolucaoMs),
            resolvido = VALUES(resolvido)
    """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, p.getSessaoMisterioId());
            ps.setInt(2, p.getPontuacaoObtida());
            ps.setLong(3, p.getTempoResolucaoMs());
            ps.setBoolean(4, p.isResolvido());
            return ps;
        }, kh);

        return kh.getKey() != null ? kh.getKey().longValue() : null;
    }


    public void atualizar(Progresso p) {
        final String sql = """
            UPDATE progresso
               SET pontuacaoObtida = ?,
                   tempoResolucaoMs = ?,
                   resolvido = ?
             WHERE sessao_misterio_id = ?
        """;

        jdbcTemplate.update(sql,
                p.getPontuacaoObtida(),
                p.getTempoResolucaoMs(),
                p.isResolvido(),
                p.getSessaoMisterioId());
    }

    public List<Progresso> buscarPorSessao(Long sessaoId) {
        final String sql = """
            SELECT p.*
              FROM progresso p
              INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
             WHERE sm.sessao_id = ?
        """;
        return jdbcTemplate.query(sql, PROGRESSO_ROW_MAPPER, sessaoId);
    }

    public Long buscarMisterioIdPorSessaoMisterio(Long sessaoMisterioId) {
        final String sql = "SELECT misterio_id FROM sessao_misterios WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, sessaoMisterioId);
    }
}
package app.repository;

import app.model.Misterio;
import app.model.Resposta;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RespostaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Resposta> RESPOSTA_RM = (rs, rowNum) -> {
        Resposta resposta = new Resposta();
        resposta.setId(rs.getLong("id"));
        resposta.setDescricao(rs.getString("descricao"));
        resposta.setOrdem(rs.getInt("ordem"));
        resposta.setStatus(rs.getBoolean("status"));

        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("misterio_id"));
        resposta.setMisterioID(misterio);

        return resposta;
    };

    public List<Resposta> alternativasMisterioID(Misterio misterio) {
        String sql = """
            SELECT id, descricao, ordem, tipo, status, misterio_id 
            FROM respostas 
            WHERE misterio_id = ?
        """;
        return jdbcTemplate.query(sql, RESPOSTA_RM, misterio.getId());
    }

    public List<Resposta> buscaIncorretas(int limite) {
        String sql = """
            SELECT r.id, r.descricao, r.ordem, r.tipo, r.status, r.misterio_id
            FROM respostas r
            WHERE r.status = false
            ORDER BY RANDOM()
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, RESPOSTA_RM, limite);
    }

    public Resposta buscaCorretaMisterio(Long misterioID) {
        String sql = """
            SELECT r.id, r.descricao, r.ordem, r.tipo, r.status, r.misterio_id
            FROM respostas r
            WHERE r.misterio_id = ? AND r.status = true
            LIMIT 1
        """;
        return jdbcTemplate.queryForObject(sql, RESPOSTA_RM, misterioID);
    }
}
package app.repository;

import app.model.Sessao;
import app.model.StatusSessao;
import app.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessaoRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Sessao> SESSAO_RM = (rs, i) -> {
        Sessao sessao = new Sessao();
        sessao.setIdSessao(rs.getLong("id_sessao"));
        sessao.setStatusSessao(StatusSessao.valueOf(rs.getString("status_sessao")));

        Turma turma = new Turma();
        turma.setId(rs.getLong("turma_id"));
        sessao.setTurma(turma);

        return sessao;
    };

    public Sessao salvarSessao(Sessao sessao) {
        String sql = """
                INSERT INTO sessoes (turma_id, status_sessao)
                VALUES (?, ?)
        """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sessao.getTurma().getId());
            ps.setString(2, sessao.getStatusSessao().name());
            return ps;
        }, keyHolder);

        Optional.ofNullable(keyHolder.getKey())
                .ifPresent(key -> sessao.setIdSessao(key.longValue()));

        return sessao;
    }

    public Optional<Sessao> acharPeloId(Long id) {
        String sql = """
                SELECT id_sessao, turma_id, status_sessao
                FROM sessoes
                WHERE id_sessao = ?
        """;
        try {
            Sessao sessao = jdbcTemplate.queryForObject(sql, SESSAO_RM, id);
            return Optional.ofNullable(sessao);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void atualizarStatusSessao(Sessao sessao) {
        String sql = """
                UPDATE sessoes
                SET status_sessao = ?
                WHERE id_sessao = ?
        """;
        jdbcTemplate.update(sql, sessao.getStatusSessao().name(), sessao.getIdSessao());
    }
}
package app.repository;

import app.model.Tentativa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TentativaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final JdbcTemplate plainJdbc;

    private static final RowMapper<Tentativa> TENTATIVA_ROW_MAPPER = (rs, rowNum) -> {
        Tentativa t = new Tentativa();
        t.setId(rs.getLong("id"));
        t.setProgressoId(rs.getLong("progressoId"));
        t.setMisterioId(rs.getLong("misterioId"));
        t.setAlternativaEscolhida(rs.getString("alternativaEscolhida"));
        t.setCorreta(rs.getBoolean("correta"));
        t.setTempoRespostaMs(rs.getLong("tempoRespostaMs"));
        t.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
        return t;
    };

    public Long inserirTentativa(Long progressoId,
                                 Long misterioId,
                                 Long respostaId,
                                 boolean correta,
                                 Long tempoRespostaMs) {

        final String sql = """
                INSERT INTO Tentativa
                    (progressoId, misterioId, alternativaEscolhida, correta, tempoRespostaMs, dataHora)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder kh = new GeneratedKeyHolder();

        plainJdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, progressoId);
            ps.setLong(2, misterioId);
            ps.setString(3, respostaId != null ? respostaId.toString() : null);
            ps.setBoolean(4, correta);
            ps.setLong(5, tempoRespostaMs != null ? tempoRespostaMs : 0L);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, kh);

        return kh.getKey() != null ? kh.getKey().longValue() : null;
    }

    public List<Tentativa> buscarListaProgressoID(List<Long> progressoIDs) {
        if (progressoIDs == null || progressoIDs.isEmpty()) {
            return Collections.emptyList();
        }

        final String sql = """
            SELECT * FROM Tentativa
            WHERE progressoId IN (:ids)
        """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", progressoIDs);

        return jdbcTemplate.query(sql, parameters, TENTATIVA_ROW_MAPPER);
    }
}
package app.repository;

import app.model.Professor;
import app.model.Turma;
import app.model.dto.TurmaRequestDto;
import app.model.dto.TurmaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TurmaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Turma> TURMA_RM = (rs, i) -> {
        Turma turma = new Turma();
        turma.setId(rs.getLong("id"));
        turma.setNome(rs.getString("nome"));
        turma.setAno(rs.getInt("ano"));
        turma.setTurno(rs.getString("turno"));

        Professor p = new Professor();
        p.setId(rs.getLong("professor_id"));
        turma.setProfessor(p);

        return turma;
    };

    private static final RowMapper<TurmaResponseDto> TURMA_RESPONSE_MAPPER = (rs, rowNum) ->
            new TurmaResponseDto(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getInt("ano"),
                    rs.getString("turno")
            );

    public List<Turma> listarTurmasDoProfessor(long professorId) {
        String sql = """
            SELECT id, nome, ano, turno, professor_id
            FROM turmas
            WHERE professor_id = ?
        """;
        return jdbcTemplate.query(sql, TURMA_RM, professorId);
    }

    public Turma save(Turma turma) {
        String sql = """
            INSERT INTO turmas (nome, ano, turno, professor_id)
            VALUES (?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, turma.getNome());
            ps.setInt(2, turma.getAno());
            ps.setString(3, turma.getTurno());
            return ps;
        }, keyHolder);

        Optional.ofNullable(keyHolder.getKey())
                .ifPresent(key -> turma.setId(key.longValue()));

        return turma;
    }

    public void deletarTurma(Long id) {
        String sql = "DELETE FROM turmas WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Nenhuma turma encontrada com o id " + id);
        }
    }

    public TurmaResponseDto modificarTurma(Long id, Long professorId, TurmaRequestDto dto) {
        final String sqlUpdate = """
            UPDATE turmas
               SET nome = ?, ano = ?, turno = ?
             WHERE id = ? AND professor_id = ?
        """;

        int updated = jdbcTemplate.update(sqlUpdate,
                dto.nome(), dto.ano(), dto.turno(),
                id, professorId
        );

        if (updated == 0) {
            throw new IllegalArgumentException("Turma não encontrada ou sem permissão para alterar.");
        }

        final String sqlSelect = """
            SELECT id, nome, ano, turno, professor_id
            FROM turmas
            WHERE id = ? AND professor_id = ?
        """;

        return jdbcTemplate.queryForObject(sqlSelect, TURMA_RESPONSE_MAPPER, id, professorId);
    }

    public List<TurmaResponseDto> buscarTurmas() {
        String sql = """
        SELECT id, nome, ano, turno, professor_id
        FROM turmas
    """;
        return jdbcTemplate.query(sql, TURMA_RESPONSE_MAPPER);
    }

    public TurmaResponseDto buscarTurmaEspecifica(Long id) {
        String sql = """
        SELECT id, nome, ano, turno, professor_id
        FROM turmas
        WHERE id = ?
    """;
        return jdbcTemplate.queryForObject(sql, TURMA_RESPONSE_MAPPER, id);
    }

    public Turma buscarTurmaEntity(Long id) {
        String sql = """
        SELECT id, nome, ano, turno, professor_id
        FROM turmas
        WHERE id = ?
    """;
        return jdbcTemplate.queryForObject(sql, TURMA_RM, id);
    }
}
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
        if (!existe) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
        return new User(email, "", Collections.emptyList());
    }
}
package app.service;

import app.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

@Service
public class AutenticacaoService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private JwtService jwtService;

    public Optional<String> autenticarProfessor(String email, String senha) {
        try {
            if (email == null || email.isBlank() || senha == null || senha.isBlank())
                return Optional.empty();

            String senhaHash = gerarHashSenha(senha);
            boolean credenciaisOk = professorRepository.findByEmailAndSenhaHash(email, senhaHash).isPresent();

            if (credenciaisOk) {
                String token = jwtService.gerarToken(email);
                return Optional.of(token);
            }

            return Optional.empty();

        } catch (Exception e) {
            System.err.println("Erro durante autenticação: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String gerarHashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    public boolean emailExiste(String email) {
        try {
            return professorRepository.emailExiste(email);
        } catch (Exception e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }


}
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

    private static final String SECRET = "super_chave_segura_com_mais_de_32_caracteres_123456";

    private final SecretKey secretKey;

    public JwtService() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("A chave JWT deve ter pelo menos 32 bytes (256 bits).");
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        System.out.printf("[INFO] JWT inicializado com %d bits.%n", keyBytes.length * 8);
    }

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validarToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String extrairUsername(String token) {
        return validarToken(token);
    }
}
package app.service;

import app.model.Progresso;
import app.model.Tentativa;
import app.model.PistaUso;
import app.model.dto.MetricasProgressoDto;
import app.repository.MetricaRepository;
import app.repository.ProgressoRepository;
import app.repository.PistaUsoRepository;
import app.repository.TentativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricasService {

    private final ProgressoRepository progressoRepository;
    private final TentativaRepository tentativaRepository;
    private final PistaUsoRepository pistaUsoRepository;
    private final MetricaRepository metricaRepository;

    public MetricasProgressoDto calcularMetricaSessao(Long sessaoId) {
        List<Progresso> progressos = progressoRepository.buscarPorSessao(sessaoId);
        List<Long> progressoIds = progressos.stream().map(Progresso::getId).toList();

        List<Tentativa> tentativas = tentativaRepository.buscarListaProgressoID(progressoIds);
        List<PistaUso> pistas = pistaUsoRepository.buscarListaProgressoID(progressoIds);

        long totalResolvidos = progressos.stream().filter(Progresso::isResolvido).count();
        double mediaEstrelas = metricaRepository.calcularMediaEstrelas(pistas);
        double mediaTentativas = metricaRepository.calcularMediaTentativas(tentativas, progressoIds);

        return new MetricasProgressoDto(totalResolvidos, mediaEstrelas, mediaTentativas);
    }

    public Map<String, Object> calcularMetricaTurma(Long turmaId) {
        return metricaRepository.calcularMetricasTurma(turmaId);
    }

    public List<Map<String, Object>> calcularMetricasPorProfessor(Long professorId) {
        return metricaRepository.calcularMetricasProfessor(professorId);
    }

    public Map<String, Object> gerarGraficosPorTurma(Long turmaId) {
        Map<String, Object> resposta = new HashMap<>();

        List<Map<String, Object>> estrelasPorPista = metricaRepository.buscarDistribuicaoEstrelasPorTipoPista(turmaId);
        resposta.put("distribuicaoEstrelas", estrelasPorPista);

        List<Map<String, Object>> tempoPorPista = metricaRepository.buscarTempoMedioPorTipoPista(turmaId);
        resposta.put("tempoPorPista", tempoPorPista);

        return resposta;
    }


}
package app.service;


import app.model.Misterio;
import app.model.Pista;
import app.model.Resposta;

import app.model.dto.AvaliacaoRequestDto;
import app.model.dto.MisterioResponseDto;
import app.model.dto.PistaDto;
import app.repository.MisterioRepository;
import app.repository.PistaRepository;
import app.repository.RespostaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MisterioService {

    private final MisterioRepository misterioRepository;
    private final PistaRepository pistaRepository;
    private final RespostaRepository respostaRepository;

    private final Random random = new Random();

    public List<Misterio> sortearMisterios(int qtd) {
        List<Misterio> sorteados = misterioRepository.sorteiaMisterio(qtd);
        if (sorteados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum mistério disponível para a sessão " );
        }
        return sorteados;
    }

    public List<Pista> sortearPistas(Misterio misterio, int qtd) {
        List<Pista> pistas = pistaRepository.retornaPistasVinculadasMisterio(misterio);
        if (pistas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma pista encontrada para o mistério: " + misterio);
        }
        Collections.shuffle(pistas, random);
        return pistas.stream().limit(qtd).collect(Collectors.toList());
    }

    public List<Resposta> montarAlternativas(Long misterioId) {
        Misterio misterio = misterioRepository.buscaMisterio(misterioId)
                .orElseThrow(() -> new IllegalArgumentException("Mistério não encontrado: " + misterioId));

        Resposta correta = respostaRepository.buscaCorretaMisterio(misterioId);
        List<Resposta> incorretas = respostaRepository.buscaIncorretas( 3);

        if (incorretas.size() < 3) {
            throw new IllegalArgumentException("Não há alternativas incorretas suficientes.");
        }

        incorretas.add(correta);
        return embaralharAlternativas(incorretas);
    }

    public List<Resposta> embaralharAlternativas(List<Resposta> respostas) {
        Collections.shuffle(respostas, random);
        return respostas;
    }

    public boolean validarResposta(Long misterioId, @Valid AvaliacaoRequestDto respostaId) {
        Misterio misterio = misterioRepository.buscaMisterio(misterioId)
                .orElseThrow(() -> new NoSuchElementException("Mistério não encontrado: " + misterioId));

        Resposta correta = respostaRepository.buscaCorretaMisterio(misterioId);
        boolean acertou = correta.getId().equals(respostaId);
        return acertou;
    }

    public MisterioResponseDto listarMisterioSessao(Long sessaoId) {
        List<Misterio> misterio = misterioRepository.buscarPorSessao(sessaoId);
        Misterio misterio1 = misterio.get(0);
        List<PistaDto>  pistas = new ArrayList<>();

        return new MisterioResponseDto(misterio1.getId(),
                misterio1.getTitulo(),
                misterio1.getPistas()
                        .stream()
                        .map(Pista::getConteudo)
                        .toList(),
                pistas
                );
    }
}
package app.service;

import app.model.Progresso;
import app.model.dto.PistaUsoCreateDto;
import app.repository.PistaUsoRepository;
import app.repository.ProgressoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PistaUsoService {

    private final ProgressoRepository progressoRepository;
    private final PistaUsoRepository pistaUsoRepository;

    public Long registrarPistaUso(PistaUsoCreateDto dto) {
        Progresso progresso = progressoRepository
                .buscarPorSessaoMisterio(dto.sessaoMisterioId())
                .orElseGet(() -> {
                    Progresso novo = new Progresso();
                    novo.setSessaoMisterioId(dto.sessaoMisterioId());
                    novo.setPontuacaoObtida(0);
                    novo.setTempoResolucaoMs(0L);
                    novo.setResolvido(false);
                    Long idGerado = progressoRepository.inserir(novo);
                    novo.setId(idGerado);
                    return novo;
                });

        return pistaUsoRepository.inserirPistaUso(
                progresso.getId(),
                dto.sessaoMisterioId(),
                dto.pistaId(),
                dto.ordem(),
                dto.tipoPista()
        );
    }
}
package app.service;

import app.exception.EmailJaCadastradoException;
import app.model.dto.ProfessorRequestDTO;
import app.model.dto.ProfessorResponseDTO;
import app.model.Professor;
import app.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    public ProfessorResponseDTO cadastrar(ProfessorRequestDTO dto) {
        if (repository.emailExiste(dto.email())) {
            throw new EmailJaCadastradoException(dto.email());
        }

        String hashSenha = gerarHashSenha(dto.senha());
        Professor professor = new Professor(dto.nome(), dto.email(), hashSenha);
        repository.save(professor);

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail()
        );
    }

    private String gerarHashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
}
package app.service;

import app.model.Progresso;
import app.model.dto.ProgressoCreateDto;
import app.repository.ProgressoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressoService {

    private final ProgressoRepository progressoRepository;

    public Long registrarOuAtualizar(ProgressoCreateDto dto) {
        Progresso existente = progressoRepository
                .buscarPorSessaoMisterio(dto.sessaoMisterioId())
                .orElse(null);

        if (existente == null) {
            Progresso novo = new Progresso();
            novo.setSessaoMisterioId(dto.sessaoMisterioId());
            novo.setPontuacaoObtida(dto.pontuacaoObtida() != null ? dto.pontuacaoObtida() : 0);
            novo.setTempoResolucaoMs(dto.tempoResolucaoMs() != null ? dto.tempoResolucaoMs() : 0L);
            novo.setResolvido(dto.resolvido() != null ? dto.resolvido() : false);
            return progressoRepository.inserir(novo);
        } else {
            existente.setPontuacaoObtida(
                    dto.pontuacaoObtida() != null ? dto.pontuacaoObtida() : existente.getPontuacaoObtida());
            existente.setTempoResolucaoMs(
                    dto.tempoResolucaoMs() != null ? dto.tempoResolucaoMs() : existente.getTempoResolucaoMs());
            if (dto.resolvido() != null) {
                existente.setResolvido(dto.resolvido());
            }
            progressoRepository.atualizar(existente);
            return existente.getId();
        }
    }
}
package app.service;

import app.exception.BusinessRuleException;
import app.exception.ResourceNotFoundException;
import app.model.Misterio;
import app.model.Sessao;
import app.model.StatusSessao;
import app.model.Turma;
import app.model.dto.MisterioSessaoDTO;
import app.model.dto.SessaoMisterioResponseDTO;
import app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessaoService {

        private final MisterioRepository misterioRepository;
        private final SessaoRepository sessaoRepository;
        private final TurmaRepository turmaRepository;
    private final PistaRepository pistaRepository;
    private final RespostaRepository respostaRepository;

       @Transactional
       public SessaoMisterioResponseDTO iniciarNovoJogo(Long idTurma, List<Misterio> misterios) {
           Turma turma;
           try {
               turma = turmaRepository.buscarTurmaEntity(idTurma);
           } catch (EmptyResultDataAccessException e) {
               throw new ResourceNotFoundException("Turma com id " + idTurma + " não encontrada.");
           }

           if (misterioRepository.count() == 0) {
               throw new BusinessRuleException("Não é possível iniciar o jogo, pois não há mistérios cadastrados.");
           }

           Sessao novaSessao = new Sessao(turma);
           Sessao sessaoCriada = sessaoRepository.salvarSessao(novaSessao);

           List<MisterioSessaoDTO> misterioDTOs = new ArrayList<>();

           for (Misterio misterio : misterios) {
               Long sessaoMisterioId = misterioRepository.adicionarMisterioNaSessao(sessaoCriada.getIdSessao(), misterio.getId());
               misterio.setPistas(pistaRepository.retornaPistasVinculadasMisterio(misterio));
               misterio.setRespostas(respostaRepository.alternativasMisterioID(misterio));

               misterioDTOs.add(MisterioSessaoDTO.from(misterio, sessaoMisterioId));
           }

           return new SessaoMisterioResponseDTO(
                   sessaoCriada.getIdSessao(),
                   turma.getId(),
                   misterioDTOs
           );
       }
    public Sessao finalizarSessao(Long idSessao){
        Sessao sessao = sessaoRepository.acharPeloId(idSessao)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão com id " + idSessao + "nao encontrada."));
        sessao.setStatusSessao(StatusSessao.FINALIZADA);
        sessaoRepository.salvarSessao(sessao);
        return sessao;
    }

}
package app.service;

import app.model.Progresso;
import app.model.dto.TentativaCreateDto;
import app.repository.ProgressoRepository;
import app.repository.TentativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TentativaService {

    private final ProgressoRepository progressoRepository;
    private final TentativaRepository tentativaRepository;

    public Long registrarTentativa(TentativaCreateDto dto) {
        Long sessaoMisterioId = progressoRepository.buscarSessaoMisterioId(dto.sessaoId(), dto.misterioId());
        if (sessaoMisterioId == null) {
            throw new IllegalArgumentException("Relação sessão-mistério não encontrada para os IDs fornecidos.");
        }

        Progresso progresso = progressoRepository
                .buscarPorSessaoMisterio(sessaoMisterioId)
                .orElseGet(() -> {
                    Progresso novo = new Progresso();
                    novo.setSessaoMisterioId(sessaoMisterioId);
                    novo.setPontuacaoObtida(0);
                    novo.setTempoResolucaoMs(0L);
                    novo.setResolvido(false);
                    Long idGerado = progressoRepository.inserir(novo);
                    novo.setId(idGerado);
                    return novo;
                });

        long tempoMs = dto.tempoSegundos() != null ? dto.tempoSegundos() * 1000L : 0L;

        return tentativaRepository.inserirTentativa(
                dto.respostaId(),
                dto.correta() != null && dto.correta(),
                tempoMs
        );
    }
}
package app.service;

import app.model.dto.TurmaResponseDto;
import app.repository.TurmaRepository;
import app.model.dto.TurmaRequestDto;
import app.model.Professor;
import app.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;

    public Turma cadastrarTurma(TurmaRequestDto turma, Professor professor) {

        if (turma.nome().isEmpty()) {
            throw new IllegalArgumentException("O nome da turma não pode ser vazio.");
        }

        Turma novaTurma = new Turma();
        novaTurma.setNome(turma.nome().toUpperCase());
        novaTurma.setAno(turma.ano());
        novaTurma.setTurno(turma.turno());
        novaTurma.setProfessor(professor);


        return turmaRepository.save(novaTurma);
    }

    public List<TurmaResponseDto> listarTodasTurmas() {
        List<TurmaResponseDto> turmas = turmaRepository.buscarTurmas();
        return turmas;
    };

    public List<TurmaResponseDto> listarTurmas(Professor professor) {
        List<Turma> listaTurmas = turmaRepository.listarTurmasDoProfessor(professor.getId());

        return listaTurmas.stream()
                .map(t -> new TurmaResponseDto(
                        t.getId(),
                        t.getNome(),
                        t.getAno(),
                        t.getTurno()
                ))
                .toList();
    }

    public void deletarTurma(Long id) {
        turmaRepository.deletarTurma(id);
    }

    public TurmaResponseDto modificarTurma(Long id, TurmaRequestDto dto, Professor professor) {
        return turmaRepository.modificarTurma(id, professor.getId(), dto);
    }

    public TurmaResponseDto buscarTurma(Long id) {

        return turmaRepository.buscarTurmaEspecifica(id);
    }

    public Turma buscarTurmaEntity(Long id){
        return turmaRepository.buscarTurmaEntity(id);
    }
}package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DetetivesDoSapiens {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DetetivesDoSapiens.class, args);
    }

    @Bean public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedMethods("GET", "POST", "DELETE",  "PUT");
            }
        };
    }
}
package app;

import app.model.Pergunta;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


@RestController
public class Teste {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/teste")
	public String teste(){
		return "Backend funcionando";
	}

	@GetMapping("/getPergunta")
public ResponseEntity<Pergunta> getPergunta() {
    String sql = "SELECT * FROM pergunta_prova WHERE id_perguntas = 1";

    Pergunta pergunta = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
        Pergunta p = new Pergunta();
        p.setId(rs.getInt("id_perguntas"));
        p.setTexto(rs.getString("texto"));
        p.setImagemUrl(rs.getString("imagemUrl"));
        p.setAudioUrl(rs.getString("audioUrl"));        
        int idAlternativas = rs.getInt("alternativas");
        p.setAlternativas(getAlternativas(idAlternativas));
        return p;
    });

    return ResponseEntity.ok(pergunta);
}

	private List<String> getAlternativas(int idAlternativas) {
		String sql = "SELECT opcao1, opcao2, opcao3, opcao4 FROM alternativas_prova WHERE id_alternativas = ?";
	
		return jdbcTemplate.queryForObject(sql, new Object[]{idAlternativas}, (rs, rowNum) -> {
			List<String> alternativas = new ArrayList<>();
			alternativas.add(rs.getString("opcao1"));
			alternativas.add(rs.getString("opcao2"));
			alternativas.add(rs.getString("opcao3"));
			alternativas.add(rs.getString("opcao4"));
			return alternativas;
		});
	}
}