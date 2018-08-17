package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S0x06ServerJoinedGroup extends Packet {
    public String sname;

    public S0x06ServerJoinedGroup() {
    }

    public S0x06ServerJoinedGroup(String sname) {
        this.sname = sname;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x06);
        dos.writeUTF(sname);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x06ServerJoinedGroup p = new S0x06ServerJoinedGroup();
        try{
            p.sname = dis.readUTF();
            return p;
        }catch (IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
