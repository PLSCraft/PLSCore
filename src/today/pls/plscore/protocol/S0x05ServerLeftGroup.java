package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S0x05ServerLeftGroup extends Packet {

    public String sname;

    public S0x05ServerLeftGroup() {
    }

    public S0x05ServerLeftGroup(String sname) {
        this.sname = sname;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x05);
        dos.writeUTF(sname);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x05ServerLeftGroup p = new S0x05ServerLeftGroup();
        try{
            p.sname = dis.readUTF();
            return p;
        }catch (IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
