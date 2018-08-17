package today.pls.plscore.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import today.pls.plscore.common.utils.Callback;
import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.common.utils.ServerStatus;
import today.pls.plscore.protocol.*;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ClientConnection {

    private boolean ready;

    PLSBungeeCore p;

    public String host;
    public int port;
    public ServerInfo info;
    public Socket s;

    DataOutputStream dos;
    DataInputStream dis;

    public ClientConnection(PLSBungeeCore pl, String _host, int _port, ServerInfo _info, Socket _s) throws IOException {
        p = pl;
        s = _s;
        host = _host;
        port = _port;
        info = _info;
        ready = false;

        dos = new DataOutputStream(s.getOutputStream());
        dis = new DataInputStream(s.getInputStream());

        p.getProxy().getScheduler().runAsync(p, this::run);
    }

    public void sendReady(Collection<ClientConnection> serverlist) throws IOException {
        String[] servers = new String[serverlist.size()];

        int i=0;
        for (ClientConnection s: serverlist) {
            servers[i++] = s.info.getName();
        }

        synchronized (dos){
            new S0x02Ready(info.getName(),servers).Construct(dos);
        }
        ready = true;
    }

    HashMap<UUID, ArrayList<Callback<PlayerInfo>>> playerInfoCallbacks = new HashMap<>();
    public void getPlayerInfo(UUID who, Callback<PlayerInfo> cb){
        if(!ready) return;
        try {

            if(playerInfoCallbacks.containsKey(who)){
                playerInfoCallbacks.get(who).add(cb);
            }else{
                //only send a new request if there's no repetitions, dont want to flood lol
                synchronized (dos) {
                    new S0x20PlayerInfoRequest(who).Construct(dos);
                }
                ArrayList<Callback<PlayerInfo>> a = new ArrayList<>();
                a.add(cb);
                playerInfoCallbacks.put(who, a);
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    ArrayList<Callback<ServerStatus>> serverStatusCallbacks = new ArrayList<>();
    public void getServerStatus(Callback<ServerStatus> cb){
        if(!ready) return;
        try {
            if(serverStatusCallbacks.size() == 0){
                synchronized (dos) {
                    new S0x21ServerStatusRequest().Construct(dos);
                }
                serverStatusCallbacks.add(cb);
            }else{
                serverStatusCallbacks.add(cb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServerLeave(String name) {
        if(!ready) return;

        try {
            synchronized (dos) {
                new S0x05ServerLeftGroup(name).Construct(dos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServerJoin(String name) {
        if(!ready) return;

        try {
            synchronized (dos) {
                new S0x06ServerJoinedGroup(name).Construct(dos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!s.isClosed()){
                Packet pkt = Packet.Parse(true,dis);
                if(pkt == null) break;

                switch (pkt.Id){
                    case 0x10:{ //C0x10PlayerUUIDRequest
                        C0x10PlayerUUIDRequest pr = (C0x10PlayerUUIDRequest) pkt;
                        ProxiedPlayer pl = p.findPlayer(pr.name);
                        synchronized (dos) {
                            new S0x50PlayerUUID(pr.name, pl != null, pl != null ? pl.getUniqueId() : null).Construct(dos);
                        }
                        break;
                    }
                    case 0x11:{ //C0x11ServerInfoRequest
                        C0x11ServerInfoRequest pr = (C0x11ServerInfoRequest) pkt;
                        p.getServerStatus(pr.serverName,(s,t)->{
                            try {
                                synchronized (dos) {
                                    new S0x51ServerInfo(pr.serverName, s != null, s).Construct(dos);
                                }
                            } catch (IOException ignored) { }
                        });
                        break;
                    }
                    case 0x12:{ //C0x12PlayerInfoRequest
                        C0x12PlayerInfoRequest pr = (C0x12PlayerInfoRequest) pkt;
                        p.getPlayerInfo(pr.id,(pi,t)->{
                            try {
                                if (pi != null) {
                                    synchronized (dos) {
                                        new S0x52PlayerInfo(true, pi).Construct(dos);
                                    }
                                } else {
                                    synchronized (dos) {
                                        new S0x52PlayerInfo(false, new PlayerInfo(pr.id)).Construct(dos);
                                    }
                                }
                            }catch (IOException ignored) { }
                        });
                        break;
                    }
                    case 0x60:{ //C0x60PlayerInfo
                        C0x60PlayerInfo rp = (C0x60PlayerInfo) pkt;
                        if(playerInfoCallbacks.containsKey(rp.info.id)){
                            for (Callback<PlayerInfo> infoCallback : playerInfoCallbacks.get(rp.info.id)) {
                                infoCallback.done(rp.exists?rp.info:null,null);
                            }
                            playerInfoCallbacks.remove(rp.info.id);
                        }
                        break;
                    }
                    case 0x61:{ //C0x61ServerStatus
                        C0x61ServerStatus rp = (C0x61ServerStatus) pkt;
                        for (Callback<ServerStatus> statusCallback : serverStatusCallbacks) {
                            statusCallback.done(rp.serverStatus,null);
                        }
                        serverStatusCallbacks.clear();
                        break;
                    }
                }
            }
            ready = false;
            p.onClientClose(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidPacketException e) {
            e.printStackTrace();
            //this literally cannot happen unless something goes extremely wrong....
        }
    }
}
