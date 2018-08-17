package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class C0x10PlayerUUIDRequest extends Packet {

    public String name;

    public C0x10PlayerUUIDRequest(){}

    public C0x10PlayerUUIDRequest(String _name){
        name = _name;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x10);
        dos.writeUTF(name);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        C0x10PlayerUUIDRequest p =  new C0x10PlayerUUIDRequest();
        try {
            p.name = dis.readUTF();
            return p;
        } catch (IOException e) {
            throw new InvalidPacketException(e);
        }
    }
}
