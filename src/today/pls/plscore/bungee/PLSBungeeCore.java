package today.pls.plscore.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import today.pls.plscore.api.PLSCoreAPI;
import today.pls.plscore.bungee.commands.PLSCommand;
import today.pls.plscore.common.PLSCore;
import today.pls.plscore.common.utils.Callback;
import today.pls.plscore.common.utils.DisconnectReason;
import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.common.utils.ServerStatus;
import today.pls.plscore.protocol.C0x01JoinRequest;
import today.pls.plscore.protocol.Packet;
import today.pls.plscore.protocol.S0x03Disconnect;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PLSBungeeCore extends Plugin implements PLSCore {

    private int serverPort;

    private Server s;

    public HashMap<String,ClientConnection> clients = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();

        saveDefaultConfig();

        new PLSCoreAPI(this);

        getProxy().getPluginManager().registerCommand(this, new PLSCommand(this));

        try{
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            serverPort = configuration.getInt("port");
        }catch (IOException ioe){
            getLogger().severe("COULD NOT LOAD CONFIG. DISABLING");
            return;
        }

        try {
            s = new Server(this, serverPort);
            getProxy().getScheduler().runAsync(this,s);
        } catch (IOException e) {
            getLogger().severe("COULD NOT CREATE/RUN SERVER. DISABLING");
            return;
        }

        getLogger().info("PLSBungeeCore Loaded");
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void newClient(Socket sck) throws IOException {
        DataInputStream is = new DataInputStream(sck.getInputStream());

        try {
            Packet p = Packet.Parse(true,is);
            if(p instanceof C0x01JoinRequest){
                C0x01JoinRequest cjr = (C0x01JoinRequest)p;

                if(cjr.protocolVersion != Packet.PROTOCOL_VERSION){
                    closeSocket(sck,DisconnectReason.PROTOCOL_VARIATION);
                    return;
                }

                ServerInfo si = getInfoByHost(cjr.host,cjr.port);

                if(si == null){
                    getLogger().severe("RECEIVED CONNECTION FROM UNKNOWN SERVER @ "+cjr.host+":"+cjr.port);
                    closeSocket(sck,DisconnectReason.PROTOCOL_VARIATION);
                    return;
                }

                if(clients.containsKey(si.getName())){ //Kill old connections from the same server.
                    ClientConnection oldC = clients.get(si.getName());
                    oldC.s.close();
                    clients.remove(si.getName());
                }

                ClientConnection c = new ClientConnection(this,cjr.host,cjr.port, si,sck);

                for (ClientConnection cl : clients.values()) {
                    cl.sendServerJoin(si.getName());
                }

                clients.put(si.getName(),c);
                getLogger().info("New client connection from " + c.host + " : " + c.port + " ~~> " + si.getName());

                c.sendReady(clients.values());
            }else{
                sck.close();
            }
        } catch (InvalidPacketException e) {
            e.printStackTrace();
        } catch (IOException e){
            //whatever...
        }
    }

    public void closeSocket(Socket sck, DisconnectReason closeReason) {
        try {
            new S0x03Disconnect(closeReason).Construct(new DataOutputStream(sck.getOutputStream()));
            sck.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        PLSCoreAPI.getInstance().deleteInstance();

        getProxy().getScheduler().cancel(this);
    }

    private ServerInfo getInfoByHost(String host, int port) {
        InetSocketAddress haddr = new InetSocketAddress(host,port);
        for(ServerInfo si: getProxy().getServers().values()){
            if(si.getAddress().equals(haddr)){
                return si;
            }
        }
        return null;
    }

    public void onClientClose(ClientConnection clientConnection) {
        getLogger().info("Client Closed @" + clientConnection.host + " : " + clientConnection.port);

        for (ClientConnection cl : clients.values()) {
            cl.sendServerLeave(clientConnection.info.getName());
        }

        try {
            clientConnection.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients.remove(clientConnection.info.getName());
    }

    public ProxiedPlayer findPlayer(String name) {
        return getProxy().getPlayer(name);
    }

    public ProxiedPlayer findPlayer(UUID id) {
        return getProxy().getPlayer(id);
    }

    @Override
    public void findPlayer(String name, Callback<UUID> cb) {
        ProxiedPlayer p = getProxy().getPlayer(name);
        if(p != null)
            cb.done(p.getUniqueId(),null);
        else
            cb.done(null,null);
    }

    public void getPlayerInfo(UUID id, Callback<PlayerInfo> cb) {
        ProxiedPlayer pp = findPlayer(id);
        if(clients.containsKey(pp.getServer().getInfo().getName())){
            clients.get(pp.getServer().getInfo().getName()).getPlayerInfo(id,cb);
        }else{
            cb.done(null,null);
        }
    }

    public void getServerStatus(String server, Callback<ServerStatus> cb) {
        if(clients.containsKey(server)){
            clients.get(server).getServerStatus(cb);
        }else{
            cb.done(null,null);
        }
    }

    @Override
    public List<String> getServers() {
        return new ArrayList<>(clients.keySet());
    }
}
