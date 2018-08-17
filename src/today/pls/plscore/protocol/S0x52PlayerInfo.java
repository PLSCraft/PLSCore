package today.pls.plscore.protocol;

import today.pls.plscore.common.utils.PlayerInfo;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class S0x52PlayerInfo extends Packet {

    public boolean exists;
    public PlayerInfo info;

    public S0x52PlayerInfo() {
    }

    public S0x52PlayerInfo(boolean exists, PlayerInfo info) {
        this.exists = exists;
        this.info = info;
        assert this.info.id != null;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x52);
        dos.writeUTF(info.id.toString());
        dos.writeBoolean(exists);
        if(exists) {
            dos.writeUTF(info.worldName);
            dos.writeDouble(info.x);
            dos.writeDouble(info.y);
            dos.writeDouble(info.z);
        }
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x52PlayerInfo p = new S0x52PlayerInfo();
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
