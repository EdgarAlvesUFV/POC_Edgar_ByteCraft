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
        {"cwe_id":"CWE-798", "description":"Use of Hard-coded Credentials"},
        {"cwe_id":"CWE-209", "description":"Generation of Error Message Containing Sensitive Information"},
        {"cwe_id":"CWE-330", "description":"Use of Insufficiently Random Values"}
    ]
}

=== CÓDIGO-FONTE ===
package com.pingo.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserAuthenticationFilter userAuthenticationFilter;

    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {
        "/auth/authenticate"
    };

    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {
        "/progress/save",
        "/progress/get/**",
        "/stage/create",
        "/stage/list",
        "/score/create",
        "/score/update",
        "/score/get/**",
        "/score/total/**",
        "/difficult/create",
        "/difficult/list",
        "/network-devices/phase-setup"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true); 
                    config.addAllowedOrigin("http://localhost:3000");
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    return config;
                }))
                .csrf(csrf -> csrf.disable()) // Desativa a proteção contra CSRF
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(requests -> requests // Habilita a autorização para as requisições HTTP
                .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).permitAll()
                .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_REQUIRED).authenticated()
                .anyRequest().denyAll()).addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}package com.pingo.config;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.entity.User;
import com.pingo.service.interfaces.JwtTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserDAO userDAO; 

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (checkIfEndpointIsNotPublic(request)) {
            String token = recoveryToken(request); // Recupera o token do cabeçalho Authorization da requisição
            if (token != null) {
                String subject = jwtTokenService.getSubjectFromToken(token); 
                User user = userDAO.findByNickname(subject); // Busca o usuário pelo nickname
                UserDetailsImpl userDetails = new UserDetailsImpl(user); // Cria um UserDetails com o usuário encontrado

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new RuntimeException("O token está ausente.");
            }
        }
        filterChain.doFilter(request, response); // Continua o processamento da requisição
    }

    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    private boolean checkIfEndpointIsNotPublic(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !Arrays.asList(SecurityConfig.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).contains(requestURI);
    }

}package com.pingo.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pingo.model.entity.User;

public class UserDetailsImpl implements UserDetails {

    private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getNickname();
    } 

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}package com.pingo.controller;

import com.pingo.model.dto.DifficultRequestDTO;
import com.pingo.model.dto.DifficultResponseDTO;
import com.pingo.service.interfaces.DifficultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/difficult")
public class DifficultController {

    @Autowired
    private DifficultService difficultService;

    @PostMapping("/create")
    public ResponseEntity<DifficultResponseDTO> createDifficult(@RequestBody DifficultRequestDTO difficultRequestDTO) {
        DifficultResponseDTO responseDTO = difficultService.createDifficult(difficultRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<DifficultResponseDTO>> listDifficulties() {
        List<DifficultResponseDTO> difficulties = difficultService.listDifficulties();
        return ResponseEntity.ok(difficulties);
    }

}package com.pingo.controller;

import com.pingo.model.dto.PhaseSetupResponseDTO;
import com.pingo.service.interfaces.NetworkDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/network-devices")
public class NetworkDeviceController {
    private final NetworkDeviceService networkDeviceService;

    public NetworkDeviceController(NetworkDeviceService networkDeviceService) {
        this.networkDeviceService = networkDeviceService;
    }

    @GetMapping("/phase-setup")
    public ResponseEntity<PhaseSetupResponseDTO> getPhaseSetup() {
        return ResponseEntity.ok(networkDeviceService.getPhaseSetup());
    }
}package com.pingo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.ScoreCreateRequestDTO;
import com.pingo.model.dto.ScoreResponseDTO;
import com.pingo.model.dto.ScoreUpdateRequestDTO;
import com.pingo.model.dto.TotalScoreResponseDTO;
import com.pingo.service.interfaces.ScoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/score")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;

    @PostMapping("/create")
    public ScoreResponseDTO postMethodName(@RequestBody ScoreCreateRequestDTO request) {
        return scoreService.createScore(request);
    }

    @PatchMapping("/update")
    public ScoreResponseDTO updateScore(@RequestBody ScoreUpdateRequestDTO request) {
        return scoreService.updateScore(request);
    }

    @GetMapping("/get/{userId}/{stageId}")
    public ScoreResponseDTO getScoreByUserAndStage(@PathVariable int userId, @PathVariable int stageId) {
        return scoreService.getScoreByUserAndStage(userId, stageId);
    }

    @GetMapping("/total/{userId}")
    public TotalScoreResponseDTO getTotalScoreByUserId(@PathVariable int userId) {
        return scoreService.getTotalScoreByUserId(userId);
    }
    
}
package com.pingo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;
import com.pingo.service.interfaces.StageService;

@RestController
@RequestMapping("/stage")
public class StageController {
    private StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }   

    @PostMapping("/create")
    public String createStage(@RequestBody StageRequestDTO request) {
        return stageService.createStage(request);
    }

    @GetMapping("/list")
    public List<StageResponseDTO> listStages() {
        return stageService.listStages();
    }
}package com.pingo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;
import com.pingo.service.interfaces.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public UserResponseDTO authenticate(@RequestBody UserRequestDTO UserRequestDTO) {
        return userService.authenticate(UserRequestDTO);
    }
}
package com.pingo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;
import com.pingo.service.interfaces.UserProgressService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/progress")
public class UserProgressController {
    UserProgressService userProgressService;
    
    public UserProgressController(UserProgressService userProgressService) {
        this.userProgressService = userProgressService;
    }
    
    @PostMapping("/save")
    public String saveProgress(@RequestBody UserProgressRequestDTO request) {
        return userProgressService.saveProgress(request);
    }

    @GetMapping("/get")
    public UserProgressResponseDTO getUserProgress(@RequestParam String nickname) {
        return userProgressService.getUserProgress(nickname);
    }

}
package com.pingo.dao.interfaces;

import java.util.List;

import com.pingo.model.entity.Difficult;

public interface DifficultDAO {

    void save(Difficult difficult);

    List<Difficult> findAll();

    Difficult findById(int id);

    Difficult update(Difficult difficult);

    void delete(int id);

}
package com.pingo.dao.interfaces;

import java.util.List;
import com.pingo.model.entity.NetworkDevice;
import com.pingo.model.entity.DeviceType;

public interface NetworkDeviceDAO {
    NetworkDevice findById(int id);
    List<NetworkDevice> findAll();
    List<NetworkDevice> findByType(DeviceType type);
    boolean findByHasDefect(boolean hasDefect);
    void update(NetworkDevice device);
    void delete(NetworkDevice device);
    void save(NetworkDevice device);
}
package com.pingo.dao.interfaces;

import java.util.List;

import com.pingo.model.entity.Score;

public interface ScoreDAO {
    void save(Score score);

    List<Score> findAll();

    Score findById(int scoreId);

    List<Score> findByUserId(int userId);

    List<Score> findByStageId(int stageId);

    Score findByUserIdAndStageId(int userId, int stageId);

    Score update(Score score);

    void delete(int scoreId);
}
package com.pingo.dao.interfaces;

import java.util.List;
import com.pingo.model.entity.Stage;

public interface StageDAO {

    public void save(Stage stage);

    public List<Stage> findAll();

    public Stage findById(int id);

    public Stage findByName(String name);

    public Stage findByNumber(int number);

    public Stage update(Stage stage);

    public void delete(int id);
}
package com.pingo.dao.interfaces;

import java.util.List;

import com.pingo.model.entity.User;

public interface UserDAO {

    void save(User user);

    List<User> findAll();

    User findById(int id);

    User findByNickname(String nickname);

    User update(User user);

    void delete(int id);
    
}
package com.pingo.dao.interfaces;
import java.util.List;

import com.pingo.model.entity.UserProgress;

public interface UserProgressDAO {

    public void save(UserProgress userProgress);

    public List<UserProgress> findAll();

    public UserProgress findByUserId(int userId);

    public UserProgress update(UserProgress userProgress);

    public void delete(int id);
}
package com.pingo.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pingo.dao.interfaces.DifficultDAO;
import com.pingo.model.entity.Difficult;

@Repository
public class DifficultDAOImpl implements DifficultDAO {
    
    public void save(Difficult difficult) {
        String sql = "INSERT INTO Difficulties(noise_amount, light_amount, speed, level, created_at, updated_at, is_active) VALUES(?,?,?,?,NOW(),NOW(),1);";
        
    }

    public List<Difficult> findAll() {
        String sql = "SELECT * FROM Difficulties;";
        return null;
    }

    public Difficult findById(int id) {
        String sql = "SELECT * FROM Difficulties WHERE id = ?;";
        return null;
    }

    public Difficult update(Difficult difficult) {
        String sql = "UPDATE Difficulties SET noise_amount = ?, light_amount = ?, speed = ?, level = ?, updated_at = NOW() WHERE id = ?;";
        return null;
    }   

    public void delete(int id) {
        String sql = "DELETE FROM Difficulties WHERE id = ?;";
    }
}
package com.pingo.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import java.util.ArrayList;
import com.pingo.dao.interfaces.NetworkDeviceDAO;
import com.pingo.model.entity.NetworkDevice;
import com.pingo.model.entity.DeviceType;
import org.springframework.stereotype.Repository;

@Repository
public class NetworkDeviceDAOimpl implements NetworkDeviceDAO {
    private final DataSource dataSource;

    public NetworkDeviceDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public NetworkDevice findById(int id) {
        String sql = "SELECT * FROM NetworkDevices WHERE id = ? AND is_active = 1;";
        
        try (Connection conn = dataSource.getConnection()) {
            var stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                NetworkDevice device = new NetworkDevice();
                device.setId(rs.getInt("id"));
                device.setName(rs.getString("name"));
                device.setHasDeffect(rs.getBoolean("has_deffect"));
                device.setType(DeviceType.valueOf(rs.getString("type")));
                device.setDescription(rs.getString("description"));
                return device;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<NetworkDevice> findAll() {
        String sql = "SELECT * FROM NetworkDevices WHERE is_active = 1;";
        List<NetworkDevice> devices = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            var stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                NetworkDevice device = new NetworkDevice();
                device.setId(rs.getInt("id"));
                device.setName(rs.getString("name"));
                device.setHasDeffect(rs.getBoolean("has_deffect"));
                device.setType(DeviceType.fromDbValue(rs.getInt("type")));
                device.setDescription(rs.getString("description"));
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    @Override
    public List<NetworkDevice> findByType(DeviceType type) {
        String sql = "SELECT * FROM NetworkDevices WHERE type = ? AND is_active = 1;";
        List<NetworkDevice> devices = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()){
            var stmt = conn.prepareStatement(sql);
            stmt.setString(1, type.toString());
            var rs = stmt.executeQuery();
            while (rs.next()) {
                NetworkDevice device = new NetworkDevice();
                device.setId(rs.getInt("id"));
                device.setName(rs.getString("name"));
                device.setHasDeffect(rs.getBoolean("has_deffect"));
                device.setType(DeviceType.valueOf(rs.getString("type")));
                device.setDescription(rs.getString("description"));
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    @Override
    public boolean findByHasDefect(boolean hasDefect) {
        String sql = "SELECT COUNT(*) > 0 as exists_defect FROM NetworkDevices WHERE has_deffect = ? AND is_active = 1;";
        try (Connection conn = dataSource.getConnection()){
            var stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, hasDefect);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("exists_defect");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(NetworkDevice device) {
        String sql = "UPDATE NetworkDevices SET name = ?, type = ?, has_deffect = ?, description = ?, updated_at = NOW() WHERE id = ? AND is_active = 1;";
        try (Connection conn = dataSource.getConnection()) {
            var stmt = conn.prepareStatement(sql);
            stmt.setString(1, device.getName());
            stmt.setString(2, device.getType().toString());
            stmt.setBoolean(3, device.getHasDeffect());
            stmt.setString(4, device.getDescription());
            stmt.setInt(5, device.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(NetworkDevice device) {
        String sql = "UPDATE NetworkDevices SET is_active = 0, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection()){
            var stmt = conn.prepareStatement(sql);
            stmt.setInt(1, device.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(NetworkDevice device) {
        String sql = "INSERT INTO NetworkDevices (name, type, has_deffect, description, created_at, updated_at, is_active) VALUES (?, ?, ?, ?, NOW(), NOW(), 1);";
        try (Connection conn = dataSource.getConnection()){
            var stmt = conn.prepareStatement(sql);
            stmt.setString(1, device.getName());
            stmt.setString(2, device.getType().toString());
            stmt.setBoolean(3, device.getHasDeffect());
            stmt.setString(4, device.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.pingo.dao;

import com.pingo.dao.interfaces.StageDAO;
import com.pingo.model.entity.Stage;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@Repository
public class StageDAOImpl implements StageDAO {
    private final DataSource dataSource;

    public StageDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Stage stage) {
        String sql = "INSERT INTO Stages(name, number, created_at, updated_at, is_active) VALUES (?,?,NOW(),NOW(),1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, stage.getName());
            pstmt.setInt(2, stage.getNumber());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    stage.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o estágio", e);
        }
    }

   @Override
     public List<Stage> findAll() {
        String sql = "SELECT * FROM Stages;";
        List<Stage> stages = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Executa query sql
            while (rs.next()) {
                if(rs.getInt("is_active") == 1) {
                    Stage stage = new Stage();
                    stage.setId(rs.getInt("id"));
                    stage.setName(rs.getString("name"));
                    stage.setNumber(rs.getInt("number"));
                    stages.add(stage);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar todos os estágios", e);
        }
        return stages;
    }

    @Override
    public Stage findById(int id) {
        String sql = "SELECT * FROM Stages WHERE id = ?;";
        Stage stage = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        stage = new Stage();
                        stage.setId(rs.getInt("id"));
                        stage.setName(rs.getString("name"));
                        stage.setNumber(rs.getInt("number"));
                        return stage;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar estágio por id", e);
        }
        return null;
    }

    @Override
    public Stage findByName(String name) {
        String sql = "SELECT * FROM Stages WHERE name = ?;";
        Stage stage = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql      
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        stage = new Stage();
                        stage.setId(rs.getInt("id"));
                        stage.setName(rs.getString("name"));
                        stage.setNumber(rs.getInt("number"));
                        return stage;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar estágio por nome", e);
        }
        return null;
    }

    @Override
    public Stage findByNumber(int number) {
        String sql = "SELECT * FROM Stages WHERE number = ?;";
        Stage stage = null;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        stage = new Stage();
                        stage.setId(rs.getInt("id"));
                        stage.setName(rs.getString("name"));
                        stage.setNumber(rs.getInt("number"));
                        return stage;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar estágio por número", e);
        }
        return null;
    }

    @Override
    public Stage update(Stage stage) {
        String sql = "UPDATE Stages SET name = ?, number = ?, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setString(1, stage.getName());
            pstmt.setInt(2, stage.getNumber());
            pstmt.setInt(3, stage.getId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Retorna usuário atualizado
                return findById(stage.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o estágio", e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Stages WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query SQL
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o estágio", e);
        }
    }
}
