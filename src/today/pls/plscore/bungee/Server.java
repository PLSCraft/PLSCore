package today.pls.plscore.bungee;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    PLSBungeeCore pl;
    ServerSocket ss;

    public Server(PLSBungeeCore _pl, int _port) throws IOException {
        pl = _pl;
        ss = new ServerSocket(_port);
    }

    @Override
    public void run() {
        while(ss.isBound()){
            try {
                Socket s = ss.accept();
                pl.newClient(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
