package com.pingo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pingo.model.entity.User;

import javax.sql.DataSource;

@ExtendWith(MockitoExtension.class)
public class UserDAOTest {
    @Mock
    private DataSource dataSource;

    @InjectMocks
    private UserDAOImpl userDAO;

    @Test
    void save_WhenNewUser_ShouldCreateNewUser() throws SQLException {
        // given
        User user = new User();
        user.setNickname("test");
        user.setPassword("123");

        Connection conn = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        userDAO.save(user);

        verify(conn, times(1)).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));
        verify(preparedStatement, times(1)).setString(1, "test");
        verify(preparedStatement, times(1)).setString(2, "123");
        verify(preparedStatement, times(1)).executeUpdate();
        verify(rs, times(1)).next();
        
    }

    @Test
    void findAll_WhenNotExistingUsers_ShouldReturnEmptyList() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<User> user = userDAO.findAll();

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(user.size(), 0);
    }

    @Test
    void findAll_WhenExistingUsers_ShouldReturnAllUsersActives() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getInt("is_active")).thenReturn(1,0, 1);

        List<User> user = userDAO.findAll();

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(4)).next();
        assertEquals(user.size(), 2);
    }

    
    @Test
    void findById_WhenExistingUser_ShoulReturnUser() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);   

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("nickname")).thenReturn("user");
        when(rs.getString("password")).thenReturn("123");
        when(rs.getInt("is_active")).thenReturn(1,0);

        User user = userDAO.findById(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(user.getId(), 1);
    }

    @Test
    void findById_WhenNotExistUser_ShoulReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);   

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        User user = userDAO.findById(1000);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1000);
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertNull(user);
    }

    @Test
    void findByNickname_WhenExistingUser_ShoulReturnUser() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);   

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("nickname")).thenReturn("user");
        when(rs.getString("password")).thenReturn("123");
        when(rs.getInt("is_active")).thenReturn(1);

        User user = userDAO.findByNickname("user");

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "user");
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertEquals(user.getNickname(), "user");
    }

    @Test
    void findByNickname_WhenNotExistUser_ShoulReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);   

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        User user = userDAO.findByNickname("nonexistent");

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "nonexistent");
        verify(pstmt, times(1)).executeQuery();
        verify(rs, times(1)).next();
        assertNull(user);
    }

    @Test
    void update_WhenUserNotExist_ShouldReturnNull() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        User user = new User();
        user.setId(1);
        user.setNickname("user");
        user.setPassword("1234");

        User newUser = userDAO.update(user);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "user");
        verify(pstmt, times(1)).setString(2, "1234");
        verify(pstmt, times(1)).setInt(3, 1);
        verify(pstmt, times(1)).executeUpdate();
        assertNull(newUser);
    }

    @Test
    void update_WhenUserExist_ShouldUpdateUser() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("nickname")).thenReturn("user");
        when(rs.getString("password")).thenReturn("123");
        when(rs.getInt("is_active")).thenReturn(1);

        User user = new User();
        user.setId(1);
        user.setNickname("user");
        user.setPassword("1234");

        User newUser = userDAO.update(user);

        verify(conn, times(2)).prepareStatement(anyString());
        verify(pstmt, times(1)).setString(1, "user");
        verify(pstmt, times(1)).setString(2, "1234");
        verify(pstmt, times(1)).setInt(3, 1);
        verify(pstmt, times(1)).executeUpdate();
        assertEquals(newUser.getNickname(), "user");
    }

    @Test
    void delete_WhenNotUserExist_ShouldNorDeleteUser() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        userDAO.delete(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeUpdate();
        assertEquals(0, pstmt.executeUpdate());
    }

    @Test
    void delete_WhenUserExist_ShouldDeleteUser() throws SQLException {
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt= mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        userDAO.delete(1);

        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).setInt(1, 1);
        verify(pstmt, times(1)).executeUpdate();
    }
}
