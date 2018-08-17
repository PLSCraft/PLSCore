package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class S0x20PlayerInfoRequest extends Packet{

    public UUID who;

    public S0x20PlayerInfoRequest(){}

    public S0x20PlayerInfoRequest(UUID _who) {
        who = _who;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x20);
        dos.writeUTF(who.toString());
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x20PlayerInfoRequest p = new S0x20PlayerInfoRequest();
        try{
            p.who = UUID.fromString(dis.readUTF());
            return p;
        }catch (IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
