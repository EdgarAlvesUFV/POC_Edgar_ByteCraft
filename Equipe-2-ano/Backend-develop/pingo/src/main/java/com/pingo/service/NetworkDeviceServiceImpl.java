package com.pingo.service;

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
