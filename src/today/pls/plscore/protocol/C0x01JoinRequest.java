package today.pls.plscore.protocol;

import sun.security.ssl.ProtocolVersion;
import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class C0x01JoinRequest extends Packet {

    public String host;
    public int port;
    public int protocolVersion;

    public C0x01JoinRequest(){}

    public C0x01JoinRequest(String _host, int _port){
        host = _host;
        port = _port;
    }

    @Override
    public void Construct(DataOutputStream dos) throws IOException {
        dos.writeByte(0x01);
        dos.writeInt(PROTOCOL_VERSION);
        dos.writeUTF(host);
        dos.writeInt(port);
    }

    @Override
    public Packet ParsePacket(DataInputStream dis) throws InvalidPacketException {
        C0x01JoinRequest p = new C0x01JoinRequest();
        try {
            p.protocolVersion = dis.readInt();
            p.host = dis.readUTF();
            p.port = dis.readInt();
            return p;
        } catch (IOException e) {
            throw new InvalidPacketException(e);
        }
    }
}
