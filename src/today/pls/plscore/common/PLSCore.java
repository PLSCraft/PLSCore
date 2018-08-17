package today.pls.plscore.common;

import today.pls.plscore.common.utils.Callback;
import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.common.utils.ServerStatus;

import java.util.List;
import java.util.UUID;

public interface PLSCore {
    void findPlayer(String name, Callback<UUID> cb);
    void getPlayerInfo(UUID id, Callback<PlayerInfo> cb);
    void getServerStatus(String server, Callback<ServerStatus> cb);
    List<String> getServers();
}
