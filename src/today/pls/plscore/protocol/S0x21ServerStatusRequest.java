package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S0x21ServerStatusRequest extends Packet {

    public S0x21ServerStatusRequest() {

    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x21);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        return new S0x21ServerStatusRequest();
    }
}
