package today.pls.plscore.common.utils;

import java.util.UUID;

public class PlayerInfo {

    public PlayerInfo(UUID id) {
        this.id = id;
    }

    public PlayerInfo(UUID id, double x, double y, double z, String worldName, String server) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.server = server;
    }

    public UUID id;
    public double x, y, z;
    public String server;
    public String worldName;
}
