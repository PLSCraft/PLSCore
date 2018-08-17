package today.pls.plscore.api;

import today.pls.plscore.bungee.Server;
import today.pls.plscore.common.PLSCore;
import today.pls.plscore.common.utils.Callback;
import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.common.utils.ServerStatus;

import java.util.UUID;

public class PLSCoreAPI {

    private static PLSCoreAPI instance;

    PLSCore pc;

    public PLSCoreAPI(PLSCore _pc){
        pc = _pc;
        instance = this;
    }

    public static PLSCoreAPI getInstance(){
        return instance;
    }

    /**
     * Checks whether the given server is existent and online.
     * @param name of the server
     * @return whether it exists and is online
     */
    public boolean haveServer(String name){
        return pc.getServers().contains(name);
    }

    /**
     * Get information about the server, or null if nonexistant
     * @param name of the server
     * @param cb callback to call when data is retrieved
     */
    public void getServer(String name, Callback<ServerStatus> cb){
        pc.getServerStatus(name,cb);
    }

    /**
     * Get a player's server-based information based on their username
     * @param name of the player
     * @param cb The playerinfo object, or null if the player does not exist
     */
    public void getPlayer(String name, Callback<PlayerInfo> cb){
        pc.findPlayer(name,(id,t) -> {
            if(id != null)
                getPlayer(id,cb);
            else
                cb.done(null,null);
        });
    }

    /**
     * Get a player's server-based information based on their UUID
     * @param id uuid of the player
     * @param cb Calls back with the player's PlayerInfo object or null if they do not exist.
     */
    public void getPlayer(UUID id, Callback<PlayerInfo> cb){
        pc.getPlayerInfo(id,cb);
    }

    public void deleteInstance(){
        instance = null;
    }
}
