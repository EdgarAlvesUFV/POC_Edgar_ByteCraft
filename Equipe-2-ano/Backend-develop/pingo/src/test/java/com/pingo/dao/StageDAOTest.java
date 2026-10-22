package com.pingo.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pingo.model.entity.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

@ExtendWith(MockitoExtension.class)
public class StageDAOTest {
    @Mock
    private DataSource dataSource;

    @InjectMocks
    private StageDAOImpl stageDAO;

    @Test
    void save_WhenStageNotExist_ShouldCreateStage() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(pstmt.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        // given
        Stage request = new Stage();
        request.setName("Fase 1");
        request.setNumber(1);

        // when
        stageDAO.save(request);

        verify(conn, times(1)).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));
        verify(pstmt, times(1)).setString(1, request.getName());
        verify(pstmt, times(1)).setInt(2, request.getNumber());
        verify(pstmt, times(1)).executeUpdate();
        verify(rs, times(1)).next();
    }

    @Test
    void findAll_WhenExistStages_ShouldReturnStages() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getInt("is_active")).thenReturn(1,1, 0);

        List<Stage> request = stageDAO.findAll();

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(4)).next();
        assertEquals(2, request.size());
    }

    @Test
    void findAll_WhenNotExistStages_ShouldReturnEmptyList() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<Stage> request = stageDAO.findAll();

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(0, request.size());
    }

    @Test
    void findById_WhenNotExistStage_ShouldReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        
        Stage request = stageDAO.findById(1000);
        
        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1000);
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertNull(request);
    }

    @Test
    void findById_WhenExistStage_ShouldReturnStage() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("is_active")).thenReturn(1);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Fase 1");
        when(rs.getInt("number")).thenReturn(1);

        Stage request = stageDAO.findById(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(request.getId(), 1);
    }

    @Test
    void findByName_WhenNotExistStage_ShouldReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Stage request = stageDAO.findByName("Fase 1");

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "Fase 1");
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertNull(request);
    }

    @Test
    void findByName_WhenExistStage_ShouldReturnStage() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("is_active")).thenReturn(1);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Fase 1");
        when(rs.getInt("number")).thenReturn(1);

        Stage request = stageDAO.findByName("Fase 1");

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "Fase 1");
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(request.getName(), "Fase 1");
    }

    @Test
    void findByNumber_WhenNotExistStage_ShouldReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Stage request = stageDAO.findByNumber(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertNull(request);
    }

    @Test
    void findByNumber_WhenExistStage_ShouldReturnStage() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("is_active")).thenReturn(1);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Fase 1");
        when(rs.getInt("number")).thenReturn(1);

        Stage request = stageDAO.findByNumber(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(request.getNumber(), 1);
    }

    @Test
    void update_WhenNotExistStage_ShouldReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        Stage stage = new Stage();
        stage.setId(1);
        stage.setName("Fase 1");
        stage.setNumber(1);

        Stage request = stageDAO.update(stage);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "Fase 1");
        verify(pstmt, times(1)).setInt(2, 1);
        verify(pstmt, times(1)).setInt(3, 1);
        verify(pstmt, times(1)).executeUpdate();
        assertNull(request);
    }

    @Test
    void update_WhenExistStage_ShouldUpdateStage() throws SQLException {
        Stage stage = new Stage();
        stage.setId(1);
        stage.setName("Fase 1");
        stage.setNumber(1);

        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("is_active")).thenReturn(1);

        Stage request = stageDAO.update(stage);

        verify(conn, times(2)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "Fase 1");
        verify(pstmt, times(1)).setInt(2, 1);
        verify(pstmt, times(1)).setInt(3, 1);
        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(1)).executeQuery();
        assertEquals(1, request.getId());
    }

    @Test
    void delete_WhenExistStage_ShouldDeleteStage() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        stageDAO.delete(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeUpdate();
    }

}
