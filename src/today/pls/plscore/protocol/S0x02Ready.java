package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S0x02Ready extends Packet {

    public String[] servers;
    public String name;

    public S0x02Ready(){}

    public S0x02Ready(String _name, String[] _slist){
        name = _name;
        servers = _slist;
    }

    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x02);
        dos.writeUTF(name);
        dos.writeInt(servers.length);
        for(int i=0;i<servers.length;i++) {
            dos.writeUTF(servers[i]);
        }
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x02Ready p = new S0x02Ready();
        try {
            p.name = dis.readUTF();
            int scount = dis.readInt();
            String[] servers = new String[scount];
            for (int i = 0; i < scount; i++) {
                servers[i] = dis.readUTF();
            }
            p.servers = servers;
            return p;
        } catch (IOException e) {
            throw new InvalidPacketException(e);
        }
    }
}
