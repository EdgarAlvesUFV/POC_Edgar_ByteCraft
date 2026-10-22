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