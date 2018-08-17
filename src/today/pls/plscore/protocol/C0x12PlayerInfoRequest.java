package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class C0x12PlayerInfoRequest extends Packet {

    public UUID id;

    public C0x12PlayerInfoRequest() {
    }

    public C0x12PlayerInfoRequest(UUID id) {
        this.id = id;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x12);
        dos.writeUTF(id.toString());
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        C0x12PlayerInfoRequest p = new C0x12PlayerInfoRequest();
        try{
            p.id = UUID.fromString(dis.readUTF());
            return p;
        }catch (IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
