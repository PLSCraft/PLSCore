package today.pls.plscore.protocol;

import today.pls.plscore.common.utils.ServerStatus;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class C0x61ServerStatus extends Packet {

    public ServerStatus serverStatus;

    public C0x61ServerStatus() {

    }

    public C0x61ServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x61);
        dos.writeLong(serverStatus.uptime);
        dos.writeDouble(serverStatus.tps1m);
        dos.writeDouble(serverStatus.tps5m);
        dos.writeDouble(serverStatus.tps15m);
        dos.writeLong(serverStatus.memCap);
        dos.writeLong(serverStatus.memAloc);
        dos.writeLong(serverStatus.memFree);
        dos.writeInt(serverStatus.playerCap);
        dos.writeInt(serverStatus.playerCount);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        C0x61ServerStatus p = new C0x61ServerStatus();
        try{
            ServerStatus s = new ServerStatus();
            s.uptime = dis.readLong();
            s.tps1m = dis.readDouble();
            s.tps5m = dis.readDouble();
            s.tps15m = dis.readDouble();
            s.memCap = dis.readLong();
            s.memAloc = dis.readLong();
            s.memFree = dis.readLong();
            s.playerCap = dis.readInt();
            s.playerCount = dis.readInt();

            p.serverStatus = s;
            return p;
        }catch(IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
