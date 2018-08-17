package today.pls.plscore.bungee.commands;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import today.pls.plscore.bungee.PLSBungeeCore;

import java.util.UUID;

public class PLSCommandHandler extends today.pls.plscore.common.commands.PLSCommandHandler {

    PLSBungeeCore pl;

    public PLSCommandHandler(PLSBungeeCore _pl){
        pl = _pl;
    }

    @Override
    public void sendMessage(UUID to, String msg) {
        if(to == null){
            pl.getProxy().getConsole().sendMessage(new TextComponent(msg));
        }else{
            ProxiedPlayer p = pl.getProxy().getPlayer(to);
            if(p == null)
                return;

            p.sendMessage(new TextComponent(msg));
        }
    }

    @Override
    public void serverInfo(UUID sender, String server, boolean detailed){
        ServerInfo s = pl.getProxy().getServerInfo(server);
        if(s != null){
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

                if(detailed){
                    s.ping((serverPing, throwable1) -> {
                        //TODO this
                    });
                }else{
                    sendMessage(sender,respStr);
                }
            });
        }
    }

    @Override
    public void serverList(UUID sender) {
        String servlist = "";
        for (ServerInfo s: pl.getProxy().getServers().values()) {
            if(pl.clients.containsKey(s.getName()))
            servlist += s.getName() + ",";
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

    void playerInfo(final UUID sender, final UUID id) {
        pl.getPlayerInfo(id, (playerInfo, throwable) -> {
            ProxiedPlayer p = pl.findPlayer(id);

            if(playerInfo == null){
                sendMessage(sender, "Player "+p.getName()+" is on "+p.getServer().getInfo().getName()+"." +
                        "\nThe server will not give us information for the player");
                return;
            }

            String posStr = "(";
            posStr += Math.round(playerInfo.x*100)/100 + ",";
            posStr += Math.round(playerInfo.y*100)/100 + ",";
            posStr += Math.round(playerInfo.z*100)/100 + ")";

            //TODO get session time from PLSAutoRank
            sendMessage(sender, "Player "+p.getName()+" is on "+p.getServer().getInfo().getName()+"." +
                    "\nIn world " + playerInfo.worldName + " @ " + posStr);
        });
    }

    @Override
    public void findPlayer(UUID sender, String username, boolean getInfo) {
        ProxiedPlayer p = pl.findPlayer(username);

        if(p == null) {
            sendMessage(sender, "Player "+username+" is not currently online.");
        } else {
            if (getInfo) {
                playerInfo(sender, p.getUniqueId());
            }else{
                sendMessage(sender, "Player "+username+" is online! " +
                        "Currently on "+p.getServer().getInfo().getName()+".");
            }
        }
    }
}
