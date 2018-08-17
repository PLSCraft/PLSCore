package today.pls.plscore.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import today.pls.plscore.spigot.PLSSpigotCore;

public class PLSCommand implements CommandExecutor {

    PLSSpigotCore pl;
    PLSCommandHandler handler;

    public PLSCommand(PLSSpigotCore pl) {
        this.pl = pl;
        this.handler = new PLSCommandHandler(pl);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            handler.sendMessage(((Player) commandSender).getUniqueId(),"How did you reach this command, bungeecord should have intercepted it.");
        }else{
            handler.onCommand(null,args);
        }
        return true;
    }
}
