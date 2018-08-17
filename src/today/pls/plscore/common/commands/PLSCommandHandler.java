package today.pls.plscore.common.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class PLSCommandHandler {

    public static HashMap<String,String> permissionMap = new HashMap<>();

    static{
        permissionMap.put("teleport","plscore.tp");
        permissionMap.put("find","plscore.find");
        permissionMap.put("servers","plscore.servers");
    }

    public void onCommand(UUID commandSender, String[] args){

        if(args.length==0){
            sendMessage(commandSender,"Usage: /pls help");
            return;
        }

        String subcommand = args[0];
        args = Arrays.copyOfRange(args,1,args.length);

        if(commandSender == null){ // Console
            switch (subcommand.toLowerCase()){
                case "tp":
                case "teleport":
                    sendMessage(null,"Console cannot teleport, stop it");
                    break;
                case "f":
                case "find":
                    if(args.length != 1){
                        sendMessage(null,"Command Usage: /p find <username> ");
                    } else {
                        findPlayer(null, args[0], true);
                    }
                    break;
                case "s":
                case "server":
                    if(args.length != 1){
                        sendMessage(null,"Command Usage: /p server <servername> ");
                    } else {
                        serverInfo(null, args[0], false);
                    }
                    break;
                case "ss":
                case "servers":
                    serverList(null);
                    break;
                default:
                    sendMessage(null,"The "+subcommand+" command does not exist!");
            }
        }else{ // Player
            switch (subcommand.toLowerCase()){
                case "tp":
                case "teleport":
                    if(args.length == 1){
                        teleport(commandSender, commandSender, args[0]);
                    }else if(args.length == 2){
                        teleport(commandSender, args[0],args[1]);
                    }else{
                        sendMessage(commandSender,"Command Usage: /p tp [from] <to> ");
                    }
                    break;
                case "f":
                case "find":
                    if(args.length != 1){
                        sendMessage(commandSender,"Command Usage: /p find <username> ");
                    } else {
                        findPlayer(commandSender, args[0], true);
                    }
                    break;
                case "s":
                case "server":
                    if(args.length != 1){
                        sendMessage(commandSender,"Command Usage: /p server <servername>");
                    } else {
                        serverInfo(commandSender, args[0], false);
                    }
                    break;
                case "ss":
                case "servers":
                    serverList(commandSender);
                    break;
                default:
                    sendMessage(commandSender,"The "+subcommand+" command does not exist!");
            }
        }
    }

    protected static String formatDuration(long duration) {
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
        return String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
    }
    /**
     * RESPOND TO A COMMAND, THIS IS NOT THE /p msg COMMAND
     * @param to
     */
    public abstract void sendMessage(UUID to, String msg);

    //Command handlers
    public abstract void serverInfo(UUID sender, String server, boolean detailed);

    public abstract void serverList(UUID sender);

    public abstract void teleport(UUID sender, UUID from, String to);

    public abstract void teleport(UUID sender, String from, String to);

    public abstract void findPlayer(UUID sender, String username, boolean getInfo);
}
