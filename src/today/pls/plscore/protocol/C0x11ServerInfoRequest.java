package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class C0x11ServerInfoRequest extends Packet {

    public String serverName;

    public C0x11ServerInfoRequest() {
    }

    public C0x11ServerInfoRequest(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x11);
        dos.writeUTF(serverName);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        C0x11ServerInfoRequest p = new C0x11ServerInfoRequest();
        try{
            p.serverName = dis.readUTF();
            return p;
        }catch(IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
