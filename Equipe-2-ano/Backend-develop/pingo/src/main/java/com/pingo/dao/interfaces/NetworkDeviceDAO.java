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
