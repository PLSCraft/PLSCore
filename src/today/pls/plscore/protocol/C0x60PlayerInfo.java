package today.pls.plscore.protocol;

import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class C0x60PlayerInfo extends Packet{

    public boolean exists;
    public PlayerInfo info;

    public C0x60PlayerInfo() {}

    public C0x60PlayerInfo(boolean exists, PlayerInfo info) {
        this.exists = exists;
        this.info = info;
        assert this.info.id != null;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x60);
        dos.writeBoolean(exists);
        dos.writeUTF(info.id.toString());
        if(exists) {
            dos.writeDouble(info.x);
            dos.writeDouble(info.y);
            dos.writeDouble(info.z);
            dos.writeUTF(info.worldName);
        }
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        C0x60PlayerInfo p = new C0x60PlayerInfo();
        try{
            p.exists = dis.readBoolean();
            PlayerInfo i = new PlayerInfo(UUID.fromString(dis.readUTF()));

            if(p.exists) {
                i.x = dis.readDouble();
                i.y = dis.readDouble();
                i.z = dis.readDouble();
                i.worldName = dis.readUTF();
            }

            p.info = i;
            return p;
        }catch(IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
