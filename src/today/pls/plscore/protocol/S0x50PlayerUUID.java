package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class S0x50PlayerUUID extends Packet {

    public UUID id;
    public boolean found;
    public String name;

    public S0x50PlayerUUID(){}

    public S0x50PlayerUUID(String _name, boolean _found, UUID _id){
        name = _name;
        found = _found;
        id = _id;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x50);
        dos.writeUTF(name);
        dos.writeBoolean(found);

        if(found)
            dos.writeUTF(id.toString());
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x50PlayerUUID p =  new S0x50PlayerUUID();
        try {
            p.name = dis.readUTF();
            p.found = dis.readBoolean();

            if(p.found)
                p.id = UUID.fromString(dis.readUTF());
            return p;
        } catch (IOException e) {
            throw new InvalidPacketException(e);
        }
    }
}
