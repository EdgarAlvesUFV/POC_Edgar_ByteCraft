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

