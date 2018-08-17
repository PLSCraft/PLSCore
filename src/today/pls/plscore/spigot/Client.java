package today.pls.plscore.spigot;

import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import today.pls.plscore.common.utils.Callback;
import today.pls.plscore.common.utils.DisconnectReason;
import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.common.utils.ServerStatus;
import today.pls.plscore.protocol.*;
import today.pls.plscore.protocol.exceptions.ConnectionNotReadyException;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.*;

public class Client {

    boolean ready = false;
    boolean supported = true;

    PLSSpigotCore pl;
    Socket s;

    DataInputStream dis;
    DataOutputStream dos;

    BukkitTask listenerTask;

    String serverName;
    ArrayList<String> servers;

    public Client(PLSSpigotCore _p, String _host, int _port) {
        pl = _p;
        connect(_host,_port);

    }

    public void connect(String host, int port) {
        if(!supported) return;
        try {
            pl.getLogger().info("Connecting to PLSBungeeCore Server.");
            System.out.println(listenerTask);
            if(listenerTask != null)
                listenerTask.cancel();

            playerUUIDCallbacks.clear();
            playerInfoCallbacks.clear();
            serverStatusCallbacks.clear();

            s = new Socket(host,port);
            listenerTask = pl.getServer().getScheduler().runTaskAsynchronously(pl, this::run);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            new C0x01JoinRequest(pl.getServer().getIp(), pl.getServer().getPort()).Construct(dos);
        } catch (IOException e) {
            pl.getLogger().severe("Could not connect to PLSBungeeCore server. Retrying in 5 seconds.");
        }
    }

    private HashMap<String, ArrayList<Callback<UUID>>> playerUUIDCallbacks = new HashMap<>();
    void findPlayer(String name, Callback<UUID> cb){
        if(!ready) {cb.done(null,new ConnectionNotReadyException()); return;}
        Player plyr;

        Callback<UUID> scb = (s,t) -> { try{ cb.done(s,t);  }catch (Exception e){ e.printStackTrace(); } };

        if( ( plyr = pl.getServer().getPlayerExact(name)) != null){
            scb.done(plyr.getUniqueId(),null);
        }else if( ! ready ) {
            scb.done(null, new ConnectionNotReadyException());
        }else {
            try {
                if(playerUUIDCallbacks.containsKey(name)){
                    playerUUIDCallbacks.get(name).add(scb);
                }else{
                    //only send a new request if there's no repetitions, dont want to flood lol
                    new C0x10PlayerUUIDRequest(name).Construct(dos);
                    ArrayList<Callback<UUID>> a = new ArrayList<>();
                    a.add(scb);
                    playerUUIDCallbacks.put(name, a);
                }
            } catch (IOException ignored) { }
        }
    }

    private HashMap<UUID, ArrayList<Callback<PlayerInfo>>> playerInfoCallbacks = new HashMap<>();
    void getPlayerInfo(UUID id, Callback<PlayerInfo> cb){
        if(!ready) {cb.done(null,new ConnectionNotReadyException()); return;}
        Player plyr;

        Callback<PlayerInfo> scb = (s,t) -> { try{ cb.done(s,t);  }catch (Exception e){ e.printStackTrace(); } };

        if( ( plyr = pl.getServer().getPlayer(id)) != null){
            Location pll = plyr.getLocation();
            scb.done(new PlayerInfo(id,pll.getX(),pll.getY(),pll.getZ(),pll.getWorld().getName(),serverName),null);
        }else if( ! ready ) {
            scb.done(null, new ConnectionNotReadyException());
        }else {
            try {

                if(playerInfoCallbacks.containsKey(id)){
                    playerInfoCallbacks.get(id).add(scb);
                }else{
                    //only send a new request if there's no repetitions, dont want to flood lol
                    new C0x12PlayerInfoRequest(id).Construct(dos);
                    ArrayList<Callback<PlayerInfo>> a = new ArrayList<>();
                    a.add(scb);
                    playerInfoCallbacks.put(id, a);
                }
            } catch (IOException e) {
                cb.done(null, e);
            }
        }
    }

    private HashMap<String, ArrayList<Callback<ServerStatus>>> serverStatusCallbacks = new HashMap<>();
    void getServerStatus(String name, Callback<ServerStatus> cb) {
        if(!ready) {cb.done(null,new ConnectionNotReadyException()); return;}

        Callback<ServerStatus> scb = (s,t) -> { try{ cb.done(s,t);  }catch (Exception e){ e.printStackTrace(); } };

        try {
            if(serverStatusCallbacks.containsKey(name)){
                serverStatusCallbacks.get(name).add(scb);
            }else{
                //only send a new request if there's no repetitions, dont want to flood lol
                new C0x11ServerInfoRequest(name).Construct(dos);
                ArrayList<Callback<ServerStatus>> a = new ArrayList<>();
                a.add(scb);
                serverStatusCallbacks.put(name, a);
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    public void run(){
        pl.getLogger().info("Listening for packets...");
        try {
            while (!s.isClosed()){

                Packet p;
                try {
                    p = Packet.Parse(false,dis);
                } catch (InvalidPacketException e) {
                    e.printStackTrace();
                    continue;
                }
                if(p == null) break;

                switch (p.Id){
                    case 0x02: { //S0x02Ready
                        this.ready = true;
                        S0x02Ready rp = (S0x02Ready) p;
                        serverName = rp.name;
                        servers = new ArrayList<>(Arrays.asList(rp.servers));
                        pl.getLogger().info("Connected & Ready.");
                        break;
                    }
                    case 0x03: {
                        s.close();
                        S0x03Disconnect rp = (S0x03Disconnect) p;
                        if(rp.reason == DisconnectReason.PROTOCOL_VARIATION || rp.reason == DisconnectReason.BAD_JOIN_REQUEST){
                            supported = false;
                        }
                        break;
                    }
                    case 0x05: { //S0x05ServerLeftGroup
                        S0x05ServerLeftGroup rp = (S0x05ServerLeftGroup) p;
                        servers.remove(rp.sname);
                        break;
                    }
                    case 0x06: { //S0x06ServerJoinedGroup
                        S0x06ServerJoinedGroup rp = (S0x06ServerJoinedGroup) p;
                        servers.add(rp.sname);
                        break;
                    }
                    case 0x20: { //S0x20PlayerInfoRequest
                        S0x20PlayerInfoRequest rp = (S0x20PlayerInfoRequest) p;
                        Player plyr = pl.getServer().getPlayer(rp.who);
                        if(plyr != null){
                            Location pll = plyr.getLocation();
                            new C0x60PlayerInfo(true, new PlayerInfo(rp.who,pll.getX(),pll.getY(),pll.getZ(),pll.getWorld().getName(),serverName)).Construct(dos);
                        }else{
                            new C0x60PlayerInfo(false, new PlayerInfo(rp.who)).Construct(dos);
                        }
                        break;
                    }
                    case 0x21: { //S0x21ServerStatusRequest
                        ServerStatus s = new ServerStatus();
                        s.uptime = ManagementFactory.getRuntimeMXBean().getUptime();
                        s.tps1m = MinecraftServer.getServer().recentTps[0];
                        s.tps5m = MinecraftServer.getServer().recentTps[1];
                        s.tps15m = MinecraftServer.getServer().recentTps[2];
                        s.memCap = Runtime.getRuntime().maxMemory();
                        s.memAloc = Runtime.getRuntime().totalMemory();
                        s.memFree = Runtime.getRuntime().freeMemory();
                        s.playerCap = pl.getServer().getMaxPlayers();
                        s.playerCount = pl.getServer().getOnlinePlayers().size();

                        C0x61ServerStatus spkt = new C0x61ServerStatus();
                        spkt.serverStatus = s;
                        spkt.Construct(dos);
                        break;
                    }
                    case 0x50: { //S0x50PlayerUUID
                        S0x50PlayerUUID rp = (S0x50PlayerUUID) p;
                        if(playerUUIDCallbacks.containsKey(rp.name)){
                            ArrayList<Callback<UUID>> r = playerUUIDCallbacks.remove(rp.name);
                            for (Callback<UUID> uuidCallback : r) {
                                uuidCallback.done(rp.found?rp.id:null,null);
                            }
                            playerUUIDCallbacks.remove(rp.name);
                        }
                        break;
                    }
                    case 0x51: { //S0x51ServerInfo
                        S0x51ServerInfo rp = (S0x51ServerInfo) p;
                        if(serverStatusCallbacks.containsKey(rp.name)){
                            ArrayList<Callback<ServerStatus>> r = serverStatusCallbacks.remove(rp.name);
                            for (Callback<ServerStatus> statusCallback : r) {
                                statusCallback.done(rp.exists?rp.status:null,null);
                            }
                            serverStatusCallbacks.remove(rp.name);
                        }
                        break;
                    }
                    case 0x52: { //S0x52PlayerInfo
                        S0x52PlayerInfo rp = (S0x52PlayerInfo) p;
                        if(playerInfoCallbacks.containsKey(rp.info.id)){
                            ArrayList<Callback<PlayerInfo>> r = playerInfoCallbacks.remove(rp.info.id);
                            for (Callback<PlayerInfo> infoCallback : r) {
                                infoCallback.done(rp.exists?rp.info:null,null);
                            }

                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            pl.getLogger().warning("Socket closed, or crashed.");
            ready = false;
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            s = null;
        }
    }

}
