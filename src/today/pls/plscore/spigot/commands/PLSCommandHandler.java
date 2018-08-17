package today.pls.plscore.spigot.commands;

import org.bukkit.entity.Player;
import today.pls.plscore.spigot.PLSSpigotCore;

import java.util.UUID;

public class PLSCommandHandler extends today.pls.plscore.common.commands.PLSCommandHandler {

    PLSSpigotCore pl;

    public PLSCommandHandler(PLSSpigotCore pl) {
        this.pl = pl;
    }

    @Override
    public void sendMessage(UUID to, String msg) {
        if(to == null){
            pl.getServer().getConsoleSender().sendMessage(msg);
        }else{
            Player p = pl.getServer().getPlayer(to);
            if(p == null)
                return;

            p.sendMessage(msg);
        }
    }

    @Override
    public void serverInfo(UUID sender, String server, boolean detailed) {
        pl.getServerStatus(server,(serverStatus,throwable) -> {
            if(serverStatus == null){
                sendMessage(sender,"There is no server called "+server);
                return;
            }

            String ramstr = "";
            ramstr += serverStatus.memFree/1024/1024 + "MB Free | ";
            ramstr += serverStatus.memAloc/1024/1024 + "MB Alloc | ";
            ramstr += serverStatus.memCap/1024/1024 + "MB Max";

            String respStr = "Status of the " + server + " server:" +
                    "\nPlayers : " + serverStatus.playerCount + "/" + serverStatus.playerCap +
                    "\nUptime : "+formatDuration(serverStatus.uptime) +
                    "\nTPS (1m 5m 15m) : "+String.format("%.2f %.2f %.2f",serverStatus.tps1m,serverStatus.tps5m,serverStatus.tps15m) +
                    "\nRAM : " + ramstr;

            sendMessage(sender,respStr);
        });
    }

    @Override
    public void serverList(UUID sender) {
        String servlist = "";
        for (String s: pl.getServers()) {
            servlist += s + ",";
        }
        servlist = servlist.substring(0,servlist.length()-1);
        sendMessage(sender,"Server List: "+servlist);
    }

    @Override
    public void teleport(UUID sender, UUID from, String to) {

    }

    @Override
    public void teleport(UUID sender, String from, String to) {

    }

    @Override
    public void findPlayer(UUID sender, String username, boolean getInfo) {
        pl.findPlayer(username,(uuid, throwable) -> {
            if(uuid == null){
                sendMessage(sender, "Player "+username+" is not currently online.");
            } else {
                pl.getPlayerInfo(uuid, (info, throwable1) -> {
                    if (getInfo) {
                        if(info == null){
                            sendMessage(sender, "Player "+username+" is on "+info.server+"." +
                                    "\nThe server will not give us information for the player");
                            return;
                        }

                        String posStr = "(";
                        posStr += Math.round(info.x*100)/100 + ",";
                        posStr += Math.round(info.y*100)/100 + ",";
                        posStr += Math.round(info.z*100)/100 + ")";

                        //TODO get session time from PLSAutoRank
                        sendMessage(sender, "Player "+username+" is on "+info.server+"." +
                                "\nIn world " + info.worldName + " @ " + posStr);
                    } else {
                        sendMessage(sender, "Player "+username+" is online! " +
                                "Currently on "+info.server+".");
                    }
                });
            }
        });
    }
}
