package com.pingo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.jdbc.Sql;

import com.pingo.model.entity.User;
import com.pingo.model.entity.UserProgress;

import javax.sql.DataSource;

@ExtendWith(MockitoExtension.class)
public class UserProgressDAOTest {
    @Mock
    private DataSource dataSource;

    @InjectMocks
    private UserProgressDAOImpl userProgressDAO;
    

    @Test
    void save_WhenNotExistingUser_ShouldSaveUserProgress() throws SQLException {
        UserProgress userProgress = new UserProgress();
        userProgress.setCurrentStage(1);
        User user = new User();
        user.setId(1);
        userProgress.setUser(user);
        userProgress.setId(1);
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);
        when(pstmt.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        userProgressDAO.save(userProgress);

        verify(conn, times(1)).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));
        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(1)).setInt(1, userProgress.getUser().getId());
        verify(pstmt, times(1)).setInt(2, userProgress.getCurrentStage());
    }

    @Test
    void findAll_WhenNotExistingUserProgress_ShouldReturnEmptyList() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<UserProgress> userProgresses = userProgressDAO.findAll();

        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(0, userProgresses.size());
    }

    @Test
    void findAll_WhenExistingUserProgress_ShouldReturnListOfUserProgress() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, true, true,false);
        when(rs.getInt("is_active")).thenReturn(1, 1, 1, 1);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("current_stage")).thenReturn(2);
        when(rs.getInt("user_id")).thenReturn(1);

        List<UserProgress> userProgresses = userProgressDAO.findAll();

        verify(pstmt, times(3)).executeQuery();
        verify(rs, times(5)).next();
        assertEquals(2, userProgresses.size()); 
    }

    @Test
    void findByUserId_WhenNotExistingUserProgress_ShouldReturnNull() throws SQLException{
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserProgress request = userProgressDAO.findByUserId(1);

        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertNull(request);
    }

    @Test
    void findByUserId_WhenExistingUserProgress_ShouldReturnUserProgress() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("is_active")).thenReturn(1, 1);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("current_stage")).thenReturn(2);

        UserProgress request = userProgressDAO.findByUserId(1);

        verify(pstmt, times(2)).executeQuery();
        verify(rs, times(2)).next();
        assertThat(request).isNotNull();
    }

    @Test
    void update_WhenNotExistingUserProgress_ShouldReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);
        UserProgress userProgress = new UserProgress();
        userProgress.setId(1);
        userProgress.setCurrentStage(1);
        userProgress.setUser(new User());
        userProgress.getUser().setId(1);

        UserProgress request = userProgressDAO.update(userProgress);

        
        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).setInt(2, 1);
        assertNull(request);
    }

    @Test
    void update_WhenExistingUserProgress_ShouldReturnUserProgress() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("is_active")).thenReturn(1);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("current_stage")).thenReturn(1);

        UserProgress userProgress = new UserProgress();
        userProgress.setId(1);
        userProgress.setCurrentStage(1);
        userProgress.setUser(new User());
        userProgress.getUser().setId(1);

        UserProgress request = userProgressDAO.update(userProgress);

        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(3)).setInt(1, 1);
        verify(pstmt, times(1)).setInt(2, 1);
        assertThat(request).isNotNull();
    }

    @Test
    void delete_WhenNotExistingUserProgress_ShouldNothing() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        userProgressDAO.delete(1);

        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(1)).setInt(1, 1);
    }

    @Test
    void delete_WhenExistingUserProgress_ShouldDelete() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        userProgressDAO.delete(1);

        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(1)).setInt(1, 1);
    }
}
