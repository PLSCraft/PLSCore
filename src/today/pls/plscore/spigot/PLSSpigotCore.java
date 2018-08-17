package today.pls.plscore.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import today.pls.plscore.api.PLSCoreAPI;
import today.pls.plscore.common.utils.Callback;
import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.common.utils.ServerStatus;
import today.pls.plscore.spigot.commands.PLSCommand;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PLSSpigotCore extends JavaPlugin implements today.pls.plscore.common.PLSCore {

    String host;
    int port;

    Client c;

    BukkitTask ensureConn;

    @Override
    public void onEnable() {
        super.onEnable();
        
        if(!loadConfig()){
            return;
        }

        new PLSCoreAPI(this);

        c = new Client(this, host, port);

        getCommand("pls").setExecutor(new PLSCommand(this));

        //Ensure we are connected every 5 seconds (20 ticks per second)
        ensureConn = getServer().getScheduler().runTaskTimerAsynchronously(this, this::ensureConnection, 0, 20*5);

        getLogger().info("PLSBungeeCore enabled");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if(PLSCoreAPI.getInstance()!=null)
            PLSCoreAPI.getInstance().deleteInstance();

        getLogger().info("Closing socket to server...");
        try {
            c.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("PLSBungeeCore Disabled");
    }

    private void ensureConnection(){
        if(!c.supported){
            getLogger().severe("This version of PLSCore is not compatible with the version you have installed on bungeecord. The plugin is effectively disabled.");
            ensureConn.cancel();
            return;
        }
        try {
            if (c.s == null || c.s.isClosed()) {
                c.ready = false;
                c.connect(host, port);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void findPlayer(String name, Callback<UUID> cb) {
        c.findPlayer(name,cb);
    }
    public void getPlayerInfo(UUID id, Callback<PlayerInfo> cb) {
        c.getPlayerInfo(id,cb);
    }
    public void getServerStatus(String server, Callback<ServerStatus> cb) {
        c.getServerStatus(server,cb);
    }

    private boolean loadConfig() {
        saveDefaultConfig();
        host = getConfig().getString("host");
        port = getConfig().getInt("port");

        return true;
    }

    public List<String> getServers() {
        return c.servers;
    }
}
