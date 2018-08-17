package today.pls.plscore.bungee.commands;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import today.pls.plscore.bungee.PLSBungeeCore;

public class PLSCommand extends Command{

    PLSBungeeCore pl;
    PLSCommandHandler handler;

    public PLSCommand(PLSBungeeCore _pl) {
        super("pls","plscore.pls");
        pl = _pl;

        handler = new PLSCommandHandler(pl);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer){
            handler.onCommand(((ProxiedPlayer) commandSender).getUniqueId(),args);
        }else{
            handler.onCommand(null,args);
        }
    }
}
