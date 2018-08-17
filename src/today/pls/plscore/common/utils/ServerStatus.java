package today.pls.plscore.common.utils;

public class ServerStatus {
    public ServerStatus(long uptime, double tps1m, double tps5m, double tps15m, long memCap, long memAloc, long memFree) {
        this.uptime = uptime;
        this.tps1m = tps1m;
        this.tps5m = tps5m;
        this.tps15m = tps15m;
        this.memCap = memCap;
        this.memAloc = memAloc;
        this.memFree = memFree;
    }

    public ServerStatus() {
    }

    public int ping;
    public long uptime;
    public double tps1m, tps5m, tps15m;
    public long memCap;
    public long memAloc;
    public long memFree;
    public int playerCap, playerCount;
}
