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

    // Endpoints que requerem autenticação para serem acessados
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
        // Verifica se o endpoint requer autenticação antes de processar a requisição
        if (checkIfEndpointIsNotPublic(request)) {
            String token = recoveryToken(request); // Recupera o token do cabeçalho Authorization da requisição
            if (token != null) {
                String subject = jwtTokenService.getSubjectFromToken(token); 
                User user = userDAO.findByNickname(subject); // Busca o usuário pelo nickname
                UserDetailsImpl userDetails = new UserDetailsImpl(user); // Cria um UserDetails com o usuário encontrado

                // Cria um objeto de autenticação do Spring Security
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new RuntimeException("O token está ausente.");
            }
        }
        filterChain.doFilter(request, response); // Continua o processamento da requisição
    }

    // Recupera o token do cabeçalho Authorization da requisição
    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    // Verifica se o endpoint requer autenticação antes de processar a requisição
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.pingo.dao.interfaces.ScoreDAO;
import com.pingo.model.entity.Score;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;

@Repository
public class ScoreDAOImpl implements ScoreDAO {
    private final DataSource dataSource;

    public ScoreDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Score score) {
        String sql = "INSERT INTO Scores(points, stage_id, user_id, created_at, updated_at, is_active) VALUES (?,?,?,NOW(),NOW(),1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Executando query sql
            pstmt.setInt(1, score.getPoints());
            pstmt.setInt(2, score.getStage().getId());
            pstmt.setInt(3, score.getUser().getId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    score.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o score", e);
        }
    }

    @Override
    public List<Score> findAll() {
        String sql = "SELECT * FROM Scores;";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Executa query sql
            while (rs.next()) {
                if(rs.getInt("is_active") == 1) {
                    Score score = new Score();
                    score.setId(rs.getInt("id"));
                    score.setPoints(rs.getInt("points"));

                    User user = new User();
                    String userSql = "SELECT * FROM Users WHERE id = ?;";
                    try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                        userPstmt.setInt(1, rs.getInt("id"));
                        try (ResultSet userRs = userPstmt.executeQuery()) {
                            if (userRs.next()) {
                                if(userRs.getInt("is_active") == 1) {
                                    user.setId(userRs.getInt("id"));
                                    user.setNickname(userRs.getString("nickname"));
                                    user.setPassword(userRs.getString("password"));
                                }
                            }
                        }
                    }
                    score.setUser(user);

                    Stage stage = new Stage();
                    String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                    try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                        stagePstmt.setInt(1, rs.getInt("id"));
                        try (ResultSet stageRs = stagePstmt.executeQuery()) {
                            if (stageRs.next()) {
                                if(stageRs.getInt("is_active") == 1) {
                                    stage.setId(stageRs.getInt("id"));
                                    stage.setName(stageRs.getString("name"));
                                    stage.setNumber(stageRs.getInt("number"));
                                }
                            }
                        }
                    }
                    score.setStage(stage);

                    scores.add(score);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar todos os scores", e);
        }
        return scores;
    }

    @Override
    public Score findById(int scoreId) {
        String sql = "SELECT * FROM Scores WHERE id = ?;";
        Score score = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, scoreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);

                        return score;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar score por id", e);
        }
        return null;
    }

    @Override
    public List<Score> findByUserId(int userId) {
        String sql = "SELECT * FROM Scores WHERE user_id = ?;";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar scores por user_id", e);
        }
        return scores;
    }

    @Override
    public List<Score> findByStageId(int stageId) {
        String sql = "SELECT * FROM Scores WHERE stage_id = ?;";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, stageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar scores por stage_id", e);
        }
        return scores;
    }

    @Override
    public Score findByUserIdAndStageId(int userId, int stageId) {
       String sql = "SELECT * FROM Scores WHERE user_id = ? AND stage_id = ?;";
       Score score = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);

                        return score;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar score por userId e stageId", e);
        }
        return null;
    }

    @Override
    public Score update(Score score) {
        String sql = "UPDATE Scores SET points = ?, stage_id = ?, user_id = ?, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, score.getPoints());
            pstmt.setInt(2, score.getStage().getId());
            pstmt.setInt(3, score.getUser().getId());
            pstmt.setInt(4, score.getId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Retorna usuário atualizado
                return findById(score.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o score", e);
        }
        return null;
    }

    @Override
    public void delete(int scoreId) {
        String sql = "DELETE FROM Scores WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query SQL
            pstmt.setInt(1, scoreId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o score", e);
        }
    }
    
}package com.pingo.dao;

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
            // Executando query sql
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
}package com.pingo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.entity.User;

@Repository
public class UserDAOImpl implements UserDAO {
    private final DataSource dataSource;

    public UserDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO Users(nickname, password, created_at, updated_at, is_active) VALUES (?, ?, NOW(), NOW(), 1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Executando query sql
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

            /// Recupera as chaves (IDs) que o banco gerou automaticamente após o INSERT
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM Users;";
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executa query sql
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Verifica se o usuário está ativo
                if(rs.getInt("is_active") == 1) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNickname(rs.getString("nickname"));
                    user.setPassword(rs.getString("password"));
                    users.add(user); // Adiciona usuário na lista
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Verifica se o usuário está ativo
                    if(rs.getInt("is_active") == 1) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setNickname(rs.getString("nickname"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User findByNickname(String nickname) {
        String sql = "SELECT * FROM Users WHERE nickname = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Verifica se o usuário está ativo
                    if(rs.getInt("is_active") == 1) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setNickname(rs.getString("nickname"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET nickname = ?, password = ?, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Retorna usuário atualizado
                return findById(user.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "UPDATE Users SET is_active = 0, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query SQL
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
package com.pingo.dao;

import com.pingo.dao.interfaces.UserProgressDAO;
import com.pingo.model.entity.User;
import com.pingo.model.entity.UserProgress;
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
public class UserProgressDAOImpl implements UserProgressDAO {
    private final DataSource dataSource;

    public UserProgressDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(UserProgress userProgress) {
        String sql = "INSERT INTO `UserProgress`(user_id, current_stage, created_at, updated_at, is_active) VALUES (?,?,NOW(),NOW(),1);";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userProgress.getUser().getId());
            pstmt.setInt(2, userProgress.getCurrentStage());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    userProgress.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o progresso do usuário", e);
        }
    }

    @Override
    public List<UserProgress> findAll() {
        String sql = "SELECT * FROM `UserProgress`";
        List<UserProgress> usersProgress = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));

                if(rs.getInt("is_active") == 1) {
                    String userSql = "SELECT * FROM Users WHERE id = ?";
                    try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                        userPstmt.setInt(1, user.getId());
                        try (ResultSet userRs = userPstmt.executeQuery()) {
                            if (userRs.next()) {
                                if(userRs.getInt("is_active") == 1) {
                                    user.setNickname(userRs.getString("nickname"));
                                }
                            }
                        }
                    }

                    UserProgress userProgress = new UserProgress();
                    userProgress.setId(rs.getInt("id"));
                    userProgress.setCurrentStage(rs.getInt("current_stage"));
                    userProgress.setUser(user);
                    usersProgress.add(userProgress);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ver o progresso de todos os usuários", e);
        }
        return usersProgress;
    }

    @Override
    public UserProgress findByUserId(int userId) {
        String sql = "SELECT * FROM `UserProgress` WHERE user_id = ?";
        UserProgress userProgress = null;

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        userProgress = new UserProgress();
                        userProgress.setId(rs.getInt("id"));
                        userProgress.setCurrentStage(rs.getInt("current_stage"));

                        User user = new User();
                        user.setId(userId);

                        String userSql = "SELECT * FROM Users WHERE id = ?";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, userId);
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setNickname(userRs.getString("nickname"));
                                    }
                                }
                            }
                        }
                        userProgress.setUser(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ver o progresso de um usuário", e);
        }
        return userProgress;
    }

    @Override
    public UserProgress update(UserProgress userProgress) {
        String sql = "UPDATE `UserProgress` SET current_stage = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userProgress.getCurrentStage());
            pstmt.setInt(2, userProgress.getId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                return findByUserId(userProgress.getUser().getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o progresso de um usuário", e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM `UserProgress` WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o progresso de um usuário", e);
        }
    }
}package com.pingo.model.dto;

import com.pingo.model.entity.DifficultLevel;

public class DifficultRequestDTO {
    private int noiseAmount;
    private int lightAmount;
    private float speed;
    private DifficultLevel level;

    public DifficultRequestDTO(int noiseAmount, int lightAmount, float speed, DifficultLevel level) {
        this.noiseAmount = noiseAmount;
        this.lightAmount = lightAmount;
        this.speed = speed;
        this.level = level;
    }

    public int getNoiseAmount() {
        return noiseAmount;
    }

    public int getLightAmount() {
        return lightAmount;
    }

    public float getSpeed() {
        return speed;
    }

    public DifficultLevel getLevel() {
        return level;
    }
}
package com.pingo.model.dto;

import com.pingo.model.entity.DifficultLevel;

public class DifficultResponseDTO {
    private Integer id;
    private int noiseAmount;
    private int lightAmount;
    private float speed;
    private DifficultLevel level;

    public DifficultResponseDTO(Integer id, int noiseAmount, int lightAmount, float speed, DifficultLevel level) {
        this.id = id;
        this.noiseAmount = noiseAmount;
        this.lightAmount = lightAmount;
        this.speed = speed;
        this.level = level;
    }

    public Integer getId() {
        return id;
    }

    public int getNoiseAmount() {
        return noiseAmount;
    }

    public int getLightAmount() {
        return lightAmount;
    }

    public float getSpeed() {
        return speed;
    }

    public DifficultLevel getLevel() {
        return level;
    }
}
package com.pingo.model.dto;

import com.pingo.model.entity.DeviceType;

public class NetworkDeviceResponseDTO {
    private int id;
    private String name;
    private DeviceType type;
    private boolean hasDeffect;
    private int positionY;
    private int positionX;

    public NetworkDeviceResponseDTO(int id, String name, DeviceType type, boolean hasDeffect, int positionY, int positionX) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.hasDeffect = hasDeffect;
        this.positionY = positionY;
        this.positionX = positionX;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DeviceType getType() {
        return type;
    }

    public boolean isHasDeffect() {
        return hasDeffect;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }
}
package com.pingo.model.dto;

import java.util.List;

public class PhaseSetupResponseDTO {
    private List<NetworkDeviceResponseDTO> devices;
    private List<List<int[]>> correctPaths;

    public PhaseSetupResponseDTO(List<NetworkDeviceResponseDTO> devices, List<List<int[]>> correctPaths) {
        this.devices = devices;
        this.correctPaths = correctPaths;
    }

    public List<NetworkDeviceResponseDTO> getDevices() {
        return devices;
    }

    public List<List<int[]>> getCorrectPaths() {
        return correctPaths;
    }
}

package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ScoreCreateRequestDTO {
    @NotBlank
    private int userId;
    @NotBlank
    private int stageId;

    public ScoreCreateRequestDTO(int userId, int stageId) {
        this.userId = userId;
        this.stageId = stageId;
    }

    public int getUserId() {
        return userId;
    }

    public int getStageId() {
        return stageId;
    }
}package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ScoreResponseDTO {
    @NotBlank
    private int id;
    @NotBlank
    private int points;
    @NotBlank
    private int stageId;
    @NotBlank
    private int userId;

    public ScoreResponseDTO(int id, int points, int stageId, int userId) {
        this.id = id;
        this.points = points;
        this.stageId = stageId;
        this.userId = userId;
    }
    
    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public int getStageId() {
        return stageId;
    }

    public int getUserId() {
        return userId;
    }
}
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ScoreUpdateRequestDTO {
    @NotBlank
    private int points;
    @NotBlank
    private int stageId;
    @NotBlank
    private int userId;

    public ScoreUpdateRequestDTO(int points, int stageId, int userId) {
        this.points = points;
        this.stageId = stageId;
        this.userId = userId;
    }

    public int getPoints() {
        return points;
    }

    public int getStageId() {
        return stageId;
    }

    public int getUserId() {
        return userId;
    }   
}
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class StageRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private int number;

    public StageRequestDTO(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class StageResponseDTO {
    @NotBlank
    private String name;
    @NotBlank
    private int number;

    public StageResponseDTO(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class TotalScoreResponseDTO {
    @NotBlank
    private int userId;
    @NotBlank
    private int points;

    public TotalScoreResponseDTO(int userId, int points) {
        this.userId = userId;
        this.points = points;
    }

    public int getUserId() {
        return userId;
    }

    public int getPoints() {
        return points;
    }
}package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProgressRequestDTO {
    @NotBlank
    private String nickname;
    @NotBlank
    private int currentStage;

    public UserProgressRequestDTO(String nickname, int currentStage) {
        this.nickname = nickname;
        this.currentStage = currentStage;
    }

    public String getNickname() {
        return nickname;
    }

    public int getCurrentStage() {
        return currentStage;
    }
}
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProgressResponseDTO {
    @NotBlank
    private String nickname;
    @NotBlank
    private int currentStage;

    public UserProgressResponseDTO(String nickname, int currentStage) {
        this.nickname = nickname;
        this.currentStage = currentStage;
    }

    public String getNickname() {
        return nickname;
    }

    public int getCurrentStage() {
        return currentStage;
    }
}
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {
    @NotBlank()
    private String nickname;
    @NotBlank()
    private String password;

    public UserRequestDTO(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }
}
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserResponseDTO {
    @NotBlank()
    private String nickname;
    @NotBlank()
    private String accessToken;

    public UserResponseDTO(String nickname, String accessToken) {
        this.nickname = nickname;
        this.accessToken = accessToken;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
package com.pingo.model.entity;

public enum DeviceType {
    INITIAL(0),
    CENTRAL(1),
    FINAL(2),
    COMMON(3);

    private final int value;
    
    DeviceType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static DeviceType fromDbValue(int value) {
        for (DeviceType type : DeviceType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Valor de tipo de dispositivo inválido: " + value);
    }
}
package com.pingo.model.entity;

public class Difficult {
    private int id;
    private int noiseAmount;
    private int lightAmount;
    private float speed;
    private DifficultLevel level;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNoiseAmount() {
        return noiseAmount;
    }
    public void setNoiseAmount(int noiseAmount) {
        this.noiseAmount = noiseAmount;
    }
    public int getLightAmount() {
        return lightAmount;
    }
    public void setLightAmount(int lightAmount) {
        this.lightAmount = lightAmount;
    }
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public DifficultLevel getLevel() {
        return level;
    }
    public void setLevel(DifficultLevel level) {
        this.level = level;
    }
}
package com.pingo.model.entity;

public enum DifficultLevel {
    One,
    Two,
    Three
}
package com.pingo.model.entity;

public class NetworkDevice {
    int id;
    String name;
    boolean hasDeffect;
    DeviceType type;
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHasDeffect() {
        return hasDeffect;
    }

    public void setHasDeffect(boolean hasDeffect) {
        this.hasDeffect = hasDeffect;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
package com.pingo.model.entity;

public class Score {
    private int id;
    private int points;
    private Stage stage;
    private User user;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public Stage getStage(){
        return stage;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void addPoints(int points){
        this.points += points;
    }

    public void resetPoints(){
        this.points = 0;
    }

}
package com.pingo.model.entity;

public class Stage {
    private int id;
    private String name;
    private int number;
 
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getNumber(){
        return number;
    }

    public void setNumber(int number){
        this.number = number;
    }
}
package com.pingo.model.entity;

public class User {
    private int id;
    private String nickname;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
package com.pingo.model.entity;

public class UserProgress {
    private int id;
    private User user;
    private int currentStage;
    
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public int getCurrentStage(){
        return currentStage;
    }

    public void setCurrentStage(int currentStage){
        this.currentStage = currentStage;
    }

}

package com.pingo.service.interfaces;

import com.pingo.model.dto.DifficultRequestDTO;
import com.pingo.model.dto.DifficultResponseDTO;
import java.util.List;

public interface DifficultService {
    DifficultResponseDTO createDifficult(DifficultRequestDTO difficultRequestDTO);
    List<DifficultResponseDTO> listDifficulties();
}package com.pingo.service.interfaces;

import org.springframework.stereotype.Service;
import com.pingo.config.UserDetailsImpl; 

@Service
public interface JwtTokenService {

    public String generateToken(UserDetailsImpl user);

    public String getSubjectFromToken(String token);

}package com.pingo.service.interfaces;

import com.pingo.model.dto.NetworkDeviceResponseDTO;
import com.pingo.model.dto.PhaseSetupResponseDTO;
import java.util.List;

public interface NetworkDeviceService {
    PhaseSetupResponseDTO getPhaseSetup();
    public List<List<int[]>> defineValidPath(List<NetworkDeviceResponseDTO> devices);
}package com.pingo.service.interfaces;

import com.pingo.model.dto.ScoreCreateRequestDTO;
import com.pingo.model.dto.ScoreResponseDTO;
import com.pingo.model.dto.ScoreUpdateRequestDTO;
import com.pingo.model.dto.TotalScoreResponseDTO;
import com.pingo.model.entity.Score;

public interface ScoreService {
    
    public ScoreResponseDTO createScore(ScoreCreateRequestDTO request);

    public Score addPointsToScore(int userId, int stageId, int points);

    public ScoreResponseDTO getScoreByUserAndStage(int userId, int stageId);

    public ScoreResponseDTO updateScore(ScoreUpdateRequestDTO request);

    public TotalScoreResponseDTO getTotalScoreByUserId(int userId);
}
package com.pingo.service.interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;

@Service
public interface StageService {
    
    public String createStage(StageRequestDTO request);

    public List<StageResponseDTO> listStages();
}package com.pingo.service.interfaces;

import org.springframework.stereotype.Service;

import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;

@Service
public interface UserProgressService {
    public String saveProgress(UserProgressRequestDTO request);

    public UserProgressResponseDTO getUserProgress(String nickname);
}
package com.pingo.service.interfaces;

import org.springframework.stereotype.Service;

import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;


@Service
public interface UserService {
    public UserResponseDTO authenticate(UserRequestDTO userRequestDTO);
}
package com.pingo.service;

import com.pingo.dao.interfaces.DifficultDAO;
import com.pingo.model.dto.DifficultRequestDTO;
import com.pingo.model.dto.DifficultResponseDTO;
import com.pingo.model.entity.Difficult;
import com.pingo.service.interfaces.DifficultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DifficultServiceImpl implements DifficultService {

    @Autowired
    private DifficultDAO difficultDAO;

    @Override
    public DifficultResponseDTO createDifficult(DifficultRequestDTO difficultRequestDTO) {
        Difficult difficult = new Difficult();
        difficult.setNoiseAmount(difficultRequestDTO.getNoiseAmount());
        difficult.setLightAmount(difficultRequestDTO.getLightAmount());
        difficult.setSpeed(difficultRequestDTO.getSpeed());
        difficult.setLevel(difficultRequestDTO.getLevel());
        
        difficultDAO.save(difficult);
        return convertToResponseDTO(difficult);
    }

    @Override
    public List<DifficultResponseDTO> listDifficulties() {
        return difficultDAO.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private DifficultResponseDTO convertToResponseDTO(Difficult difficult) {
        DifficultResponseDTO responseDTO = new DifficultResponseDTO(difficult.getId(), difficult.getNoiseAmount(), difficult.getLightAmount(), difficult.getSpeed(), difficult.getLevel());
        return responseDTO;
    }
}package com.pingo.service;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.pingo.config.UserDetailsImpl;
import com.pingo.service.interfaces.JwtTokenService; 

@Service
public class JwtTokenServiceImpl implements JwtTokenService{

    private static final String SECRET_KEY = "anfjsiurfyu1wgryu3gf26brf67gj7efg24r4f"; // Chave secreta utilizada para gerar e verificar o token 

    private static final String ISSUER = "pizzurg-api"; // Emissor do token

    @Override
    public String generateToken(UserDetailsImpl user) {
        try {
            // Define o algoritmo HMAC SHA256 para criar a assinatura do token passando a chave secreta definida
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(ISSUER) // Emissor do token
                    .withIssuedAt(creationDate()) // Define a data de emissão do token
                    .withExpiresAt(expirationDate()) // Define a data de expiração do token
                    .withSubject(user.getUsername()) // Define o assunto do token (Nickname)
                    .sign(algorithm); // Assina o token usando o algoritmo especificado
        } catch (JWTCreationException exception){
            throw new JWTCreationException("Erro ao gerar token.", exception);
        }
    }

    @Override
    public String getSubjectFromToken(String token) {
        try {
            // Define o algoritmo HMAC SHA256 para verificar a assinatura do token passando a chave secreta definida
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER) // Define o emissor do token
                    .build()
                    .verify(token) // Verifica a validade do token
                    .getSubject(); // Obtém o assunto (neste caso, o nome de usuário) do token
        } catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token inválido ou expirado.");
        }
    }

    private Instant creationDate() {
        // Cria data de início da validade do token
        return ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
    }

    private Instant expirationDate() {
        // Data ded expiração do Token está para 12 horas
        return ZonedDateTime.now(ZoneId.of("America/Recife")).plusHours(12).toInstant();
    }

}package com.pingo.service;

import com.pingo.dao.interfaces.NetworkDeviceDAO;
import com.pingo.model.dto.NetworkDeviceResponseDTO;
import com.pingo.model.dto.PhaseSetupResponseDTO;
import com.pingo.model.entity.NetworkDevice;
import com.pingo.service.interfaces.NetworkDeviceService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NetworkDeviceServiceImpl implements NetworkDeviceService {
    private NetworkDeviceDAO networkDeviceDAO;

    public NetworkDeviceServiceImpl(NetworkDeviceDAO networkDeviceDAO) {
        this.networkDeviceDAO = networkDeviceDAO;
    }

    @Override
    public PhaseSetupResponseDTO getPhaseSetup() {
        List<NetworkDevice> allDevices = networkDeviceDAO.findAll();
        List<NetworkDeviceResponseDTO> devices = allDevices.stream()
            .map(device -> new NetworkDeviceResponseDTO(
                device.getId(),
                device.getName(),
                device.getType(),
                device.getHasDeffect(),
                0,
                0
            ))
            .collect(Collectors.toList());
        List<NetworkDeviceResponseDTO> outputdevices = generateRandomPositions(devices);
        List<List<int[]>> validPaths = defineValidPath(outputdevices);

        return new PhaseSetupResponseDTO(outputdevices, validPaths);
    }  

    public List<NetworkDeviceResponseDTO> generateRandomPositions(List<NetworkDeviceResponseDTO> devices) {
        boolean[][] occupied = new boolean[3][3];
        Collections.shuffle(devices);
        List<NetworkDeviceResponseDTO> output = new ArrayList<>();
        Random rand = new Random();
        Optional<NetworkDeviceResponseDTO> deviceOpt;

        // PRIMEIRO MODEM
        deviceOpt = devices.stream()
            .filter(d -> !output.contains(d))
            .filter(d -> {
                return "modem".equals(d.getName()) 
                    && !d.isHasDeffect() 
                    && d.getType().getValue() == 0;
            })
            .findFirst();
        if (deviceOpt.isEmpty()) return null;
        
        int x = rand.nextInt(3);
        int y = 0;
        occupied[x][y] = true;
        NetworkDeviceResponseDTO device = deviceOpt.get();
        device.setPositionX(x);
        device.setPositionY(y);
        output.add(device);
        
        // SEGUNDO MODEM
        deviceOpt = devices.stream()
            .filter(d -> !output.contains(d))
            .filter(d -> {
                return "modem".equals(d.getName()) 
                    && !d.isHasDeffect() 
                    && d.getType().getValue() ==  2;
            })
            .findFirst();
        
        if (deviceOpt.isEmpty()) return null;
        
        x = rand.nextInt(3);
        y = 2;
        occupied[x][y] = true;
        device = deviceOpt.get();
        device.setPositionX(x);
        device.setPositionY(y);
        output.add(device);

        // SWITCH
        deviceOpt = devices.stream()
            .filter(d -> !output.contains(d))
            .filter(d -> {
                return "switch".equals(d.getName()) 
                    && !d.isHasDeffect()
                    && d.getType().getValue() ==  1;
            })
            .findFirst();
        
        if (deviceOpt.isEmpty()) return null;
        
        x = rand.nextInt(3);
        y = 1;
        occupied[x][y] = true;
        device = deviceOpt.get();
        device.setPositionX(x);
        device.setPositionY(y);
        output.add(device);

        while(output.size() < 9) {
            do {
                x = rand.nextInt(3);
                y = rand.nextInt(3);
            } while (occupied[x][y]);
            final int finalY = y;
            deviceOpt = devices.stream()
                .filter(d -> !output.contains(d))
                .filter(d -> {
                    return d.getType().getValue() == finalY;
                })
            .findFirst();
            if (deviceOpt.isEmpty()) return null;
            
            device = deviceOpt.get();
            occupied[x][y] = true;
            device.setPositionX(x);
            device.setPositionY(y);
            output.add(device);
        }
        output.sort(Comparator.comparingInt(NetworkDeviceResponseDTO::getPositionY)
            .thenComparingInt(NetworkDeviceResponseDTO::getPositionX));
        return output;
    }


    @Override
    public List<List<int[]>> defineValidPath(List<NetworkDeviceResponseDTO> devices) {
        Map<String, NetworkDeviceResponseDTO> positionMap = new HashMap<>();
        NetworkDeviceResponseDTO startDevice = null;
        NetworkDeviceResponseDTO endDevice = null;
        int switchX = -1;
        int switchY = -1;

        for (NetworkDeviceResponseDTO d : devices) {
            positionMap.put(d.getPositionX() + "-" + d.getPositionY(), d);
            if (d.getPositionY() == 0 
                    && d.getName().toLowerCase().contains("modem")
                    && d.isHasDeffect() == false) {
                startDevice = d;
            }
            if (d.getPositionY() == 2 
                    && d.getName().toLowerCase().contains("modem")
                    && d.isHasDeffect() == false) {
                endDevice = d;
            }
            if (d.getName().toLowerCase().contains("switch") && !d.isHasDeffect()) {
                switchX = d.getPositionX();
                switchY = d.getPositionY();
            }
        }

        if (startDevice == null || endDevice == null || switchX == -1) {
            return new ArrayList<>();
        }

        final int targetX = endDevice.getPositionX();
        final int targetY = endDevice.getPositionY();
        
        final int finalSwitchX = switchX;
        final int finalSwitchY = switchY;

        List<List<int[]>> allPaths = new ArrayList<>();
        int[][] visitCount = new int[3][3];
        List<int[]> currentPath = new ArrayList<>();

        class PathFinder {
            void search(int x, int y) {
                visitCount[x][y]++;
                currentPath.add(new int[]{x, y});

                if (x == targetX && y == targetY) {
                    allPaths.add(new ArrayList<>(currentPath));
                } else {
                    int[] sameColMoves = {-1, 1};
                    for (int dx : sameColMoves) {
                        int nx = x + dx;
                        int ny = y;
                        checkAndMove(nx, ny);
                    }

                    if (y < 2) {
                        int ny = y + 1;
                        for (int nx = 0; nx <= 2; nx++) { 
                            checkAndMove(nx, ny);
                        }
                    }
                }

                visitCount[x][y]--;
                currentPath.remove(currentPath.size() - 1);
            }

            private void checkAndMove(int nx, int ny) {
                if (nx < 0 || nx > 2 || ny < 0 || ny > 2) return;
                if (visitCount[nx][ny] >= 2) return;
                if (nx == finalSwitchX && ny == finalSwitchY) {
                    if (visitCount[nx][ny] >= 1) {
                        return;
                    }
                }

                String key = nx + "-" + ny;
                NetworkDeviceResponseDTO nextDevice = positionMap.get(key);
                if (nextDevice != null && !nextDevice.isHasDeffect()) {
                    search(nx, ny);
                }
            }
        }

        new PathFinder().search(startDevice.getPositionX(), startDevice.getPositionY());
        return allPaths.stream()
            .filter(path -> {
                for (int[] pos : path) {
                    NetworkDeviceResponseDTO dev = positionMap.get(pos[0] + "-" + pos[1]);
                    if (dev != null && dev.getName().toLowerCase().contains("switch")) {
                        return true;
                    }
                }
                return false;
            })
            .sorted(Comparator.comparingInt(List::size))
            .collect(Collectors.toList());
    }
}
package com.pingo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.interfaces.ScoreDAO;
import com.pingo.dao.interfaces.StageDAO;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.dto.ScoreCreateRequestDTO;
import com.pingo.model.dto.ScoreResponseDTO;
import com.pingo.model.dto.ScoreUpdateRequestDTO;
import com.pingo.model.dto.TotalScoreResponseDTO;
import com.pingo.model.entity.Score;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;
import com.pingo.service.interfaces.ScoreService;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreDAO scoreDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired 
    private StageDAO stageDAO;

    public ScoreResponseDTO createScore(ScoreCreateRequestDTO request) {
        User user = userDAO.findById(request.getUserId());
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }

        Stage stage = stageDAO.findById(request.getStageId());
        if(stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase não encontrada");
        }

        Score score = new Score();
        score.resetPoints();
        score.setStage(stage);
        score.setUser(user);
        scoreDAO.save(score);

        return new ScoreResponseDTO(score.getId(), score.getPoints(), stage.getId(), user.getId());
    }

    public Score addPointsToScore(int userId, int stageId, int points) {
        Score score = scoreDAO.findByUserIdAndStageId(userId, stageId);
        score.setPoints(score.getPoints() + points);
        scoreDAO.save(score);
        return score;
    }

    public ScoreResponseDTO getScoreByUserAndStage(int userId, int stageId) {
        User user = userDAO.findById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }

        Stage stage = stageDAO.findById(stageId);
        if(stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase não encontrada");
        }

        Score score = scoreDAO.findByUserIdAndStageId(userId, stageId);
        if (score == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Score não encontrado");
        }
        return new ScoreResponseDTO(score.getId(), score.getPoints(), score.getStage().getId(), score.getUser().getId());
    }

    public ScoreResponseDTO updateScore(ScoreUpdateRequestDTO request) {
        User user = userDAO.findById(request.getUserId());
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }

        Stage stage = stageDAO.findById(request.getStageId());
        if(stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase não encontrada");
        }

        Score score = scoreDAO.findByUserIdAndStageId(request.getUserId(), request.getStageId());
        if (score == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Score não encontrado");
        }
        score.setPoints(request.getPoints());
        score.setStage(stage);
        score.setUser(user);
        scoreDAO.update(score);
        return new ScoreResponseDTO(score.getId(), score.getPoints(), score.getStage().getId(), score.getUser().getId());
    }

    public TotalScoreResponseDTO getTotalScoreByUserId(int userId) {
        List<Score> scores = scoreDAO.findByUserId(userId);
        int totalPoints = 0;
        for (Score score : scores) {
            totalPoints += score.getPoints();
        }
        return new TotalScoreResponseDTO(userId, totalPoints);
    }
}
package com.pingo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.interfaces.StageDAO;
import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;
import com.pingo.model.entity.Stage;
import com.pingo.service.interfaces.StageService;

@Service
public class StageServiceImpl implements StageService {
    private final StageDAO stageDAO;

    public StageServiceImpl(StageDAO stageDAO) {
        this.stageDAO = stageDAO;
    }

    @Override
    public String createStage(StageRequestDTO request) {
        Stage stage = stageDAO.findByName(request.getName());
        if(stage != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase já existente");
        }
        stage = stageDAO.findByNumber(request.getNumber());
        if(stage != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase já existente");
        }
        stage = new Stage();
        stage.setName(request.getName());
        stage.setNumber(request.getNumber());
        stageDAO.save(stage);
        return "Fase criada com sucesso";
    }

    @Override
    public List<StageResponseDTO> listStages() {
        List<Stage> stages = stageDAO.findAll();
        if(stages == null) {
            return List.of();
        }
        return stages.stream()
            .map(stage -> new StageResponseDTO(stage.getName(), stage.getNumber()))
            .collect(Collectors.toList());
    }
}
package com.pingo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pingo.config.UserDetailsImpl;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.entity.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String nickName) throws UsernameNotFoundException {
        User user = userDAO.findByNickname(nickName);
        return new UserDetailsImpl(user);
    }

}package com.pingo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.interfaces.StageDAO;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.dao.interfaces.UserProgressDAO;
import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;
import com.pingo.model.entity.UserProgress;
import com.pingo.service.interfaces.UserProgressService;

@Service
public class UserProgressServiceImpl implements UserProgressService {
    private final UserDAO userDAO;
    private final UserProgressDAO userProgressDAO;
    private final StageDAO stageDAO;

    public UserProgressServiceImpl(UserDAO userDAO, UserProgressDAO userProgressDAO, StageDAO stageDAO) {   
        this.userDAO = userDAO;
        this.userProgressDAO = userProgressDAO;
        this.stageDAO = stageDAO;
    }

    @Override
    public String saveProgress(UserProgressRequestDTO request) {
        User user = userDAO.findByNickname(request.getNickname());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }
        Stage stage = stageDAO.findByNumber(request.getCurrentStage());
        if (stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase "+ request.getCurrentStage() + " não encontrada");
        }

        UserProgress userProgress = userProgressDAO.findByUserId(user.getId());
        if (userProgress == null) {
            userProgress = new UserProgress();
            userProgress.setUser(user);
            userProgress.setCurrentStage(1);
            userProgressDAO.save(userProgress);
            return "Progresso do usuário criado com sucesso";
        }
        userProgress.setCurrentStage(request.getCurrentStage());
        userProgressDAO.update(userProgress);
        return "Progresso do usuário atualizado com sucesso";
    }

    @Override
    public UserProgressResponseDTO getUserProgress(String nickname) {
        User user = userDAO.findByNickname(nickname);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }
        UserProgress userProgress = userProgressDAO.findByUserId(user.getId());
        if (userProgress == null) {
            userProgress = new UserProgress();
            userProgress.setUser(user);
            userProgress.setCurrentStage(1);
            userProgressDAO.save(userProgress);
        }
        return new UserProgressResponseDTO(user.getNickname(), userProgress.getCurrentStage());
    }
    
}
package com.pingo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.pingo.config.SecurityConfig;
import com.pingo.config.UserDetailsImpl;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.dto.UserRequestDTO;
import com.pingo.model.dto.UserResponseDTO;
import com.pingo.model.entity.User;
import com.pingo.service.interfaces.JwtTokenService;
import com.pingo.service.interfaces.UserService;


@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtTokenService jwtTokenService;

    @Autowired
    private final SecurityConfig securityConfiguration;

    private final UserDAO userDAO;
    
    public UserServiceImpl(AuthenticationManager authenticationManager, UserDAO userDAO, JwtTokenService jwtTokenService, SecurityConfig securityConfiguration) {
        this.authenticationManager = authenticationManager;
        this. jwtTokenService = jwtTokenService;
        this.securityConfiguration = securityConfiguration;
        this.userDAO = userDAO;
    }
    @Override
    public UserResponseDTO authenticate(UserRequestDTO userRequestDTO) {
        // Busca usuário pelo nickname
        User user = userDAO.findByNickname(userRequestDTO.getNickname());
        UserDetailsImpl userDetails;

        if (user == null) {
            // Registro de um novo usuário
            user = new User();
            user.setNickname(userRequestDTO.getNickname());
            user.setPassword(securityConfiguration.passwordEncoder().encode(userRequestDTO.getPassword()));
            userDAO.save(user);
            userDetails = new UserDetailsImpl(user);
        } else {
            // Login do Usuário
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userRequestDTO.getNickname(), userRequestDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            userDetails = (UserDetailsImpl) authentication.getPrincipal();
        }
        // Retorna DTO com nickname e token
        return new UserResponseDTO(user.getNickname(), jwtTokenService.generateToken(userDetails));
    }
}
package com.pingo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PingoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingoApplication.class, args);
    }

}
