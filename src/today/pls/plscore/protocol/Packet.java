package today.pls.plscore.protocol;

import today.pls.plscore.protocol.exceptions.InvalidPacketException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public abstract class Packet {

    public static final int PROTOCOL_VERSION = 0x12C0FFEE;

    private static HashMap<Byte,Class<? extends Packet>> clientPackets = new HashMap<>();
    private static HashMap<Byte,Class<? extends Packet>> serverPackets = new HashMap<>();
    static{
        /* * * * * * * * * *
         * CLIENT  PACKETS *
         * * * * * * * * * */
        //Protocol
        clientPackets.put((byte) 0x01, C0x01JoinRequest.class);

        //C->S Requests (id + 0x40 = server response id
        clientPackets.put((byte) 0x10, C0x10PlayerUUIDRequest.class);
        clientPackets.put((byte) 0x11, C0x11ServerInfoRequest.class);
        clientPackets.put((byte) 0x12, C0x12PlayerInfoRequest.class);

        //C->S Responses (id - 0x40 = server request id)
        clientPackets.put((byte) 0x60, C0x60PlayerInfo.class);
        clientPackets.put((byte) 0x61, C0x61ServerStatus.class);

        /* * * * * * * * * *
         * SERVER  PACKETS *
         * * * * * * * * * */

        //Protocol
        serverPackets.put((byte) 0x02, S0x02Ready.class);
        serverPackets.put((byte) 0x03, S0x03Disconnect.class);
        serverPackets.put((byte) 0x05, S0x05ServerLeftGroup.class);
        serverPackets.put((byte) 0x06, S0x06ServerJoinedGroup.class);

        //S->C Requests (id + 0x40 = client response id)
        serverPackets.put((byte) 0x20, S0x20PlayerInfoRequest.class);
        serverPackets.put((byte) 0x21, S0x21ServerStatusRequest.class);

        //S->C Responses (id - 0x40 = client request id)
        serverPackets.put((byte) 0x50, S0x50PlayerUUID.class);
        serverPackets.put((byte) 0x51, S0x51ServerInfo.class);
        serverPackets.put((byte) 0x52, S0x52PlayerInfo.class);
    }

    public byte Id;

    public Packet(){}

    public abstract void Construct(DataOutputStream dos) throws IOException;

    public static Packet Parse(boolean clientSide, DataInputStream dis) throws InvalidPacketException{
        Class<? extends Packet> p;
        try {
            byte id = dis.readByte();
            if (clientSide) {
                p = clientPackets.get(id);
            } else {
                p = serverPackets.get(id);
            }
            if(p==null) throw new InvalidPacketException("Unknown ID " +(clientSide?"C":"S")+id );
            Packet pkt = p.newInstance().ParsePacket(dis);
            pkt.Id = id;
            return pkt;
        }catch (IOException ioe){
            ioe.printStackTrace();
            //couldnt read id byte. bad stuff, most likely dead conn
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public abstract Packet ParsePacket(DataInputStream dis) throws InvalidPacketException;

    @Override
    public String toString() {
        return "P{Id="+Id+"}";
    }
}
