package today.pls.plscore.protocol;

import today.pls.plscore.common.utils.ServerStatus;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S0x51ServerInfo extends Packet {

    public String name;
    public boolean exists;
    public ServerStatus status;

    public S0x51ServerInfo() {
    }

    public S0x51ServerInfo(String name, boolean exists, ServerStatus status) {
        this.name = name;
        this.exists = exists;
        this.status = status;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x51);
        dos.writeUTF(name);
        dos.writeBoolean(exists);
        if(exists) {
            dos.writeInt(status.ping);
            dos.writeInt(status.playerCap);
            dos.writeInt(status.playerCount);
            dos.writeLong(status.uptime);
            dos.writeDouble(status.tps1m);
            dos.writeDouble(status.tps5m);
            dos.writeDouble(status.tps15m);
            dos.writeLong(status.memCap);
            dos.writeLong(status.memAloc);
            dos.writeLong(status.memFree);
        }
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x51ServerInfo p = new S0x51ServerInfo();
        try{
            p.name = dis.readUTF();
            p.exists = dis.readBoolean();

            if(p.exists) {
                ServerStatus s = new ServerStatus();
                s.ping = dis.readInt();
                s.playerCap = dis.readInt();
                s.playerCount = dis.readInt();
                s.uptime = dis.readLong();
                s.tps1m = dis.readDouble();
                s.tps5m = dis.readDouble();
                s.tps15m = dis.readDouble();
                s.memCap = dis.readLong();
                s.memAloc = dis.readLong();
                s.memFree = dis.readLong();
                p.status = s;
            }

            return p;
        }catch (IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
