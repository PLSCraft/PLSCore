package today.pls.plscore.protocol;

import today.pls.plscore.common.utils.DisconnectReason;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S0x03Disconnect extends Packet {

    public DisconnectReason reason;

    public S0x03Disconnect() {
    }

    public S0x03Disconnect(DisconnectReason reason) {
        this.reason = reason;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x03);
        dos.writeByte(reason.toInteger());
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        S0x03Disconnect p = new S0x03Disconnect();
        try{
            p.reason = DisconnectReason.fromInteger(dis.readInt());
            return p;
        }catch(IOException e){
            throw new InvalidPacketException(e);
        }
    }
}
