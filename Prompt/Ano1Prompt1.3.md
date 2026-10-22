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
}
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