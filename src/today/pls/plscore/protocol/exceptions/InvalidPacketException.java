package today.pls.plscore.protocol.exceptions;

public  class InvalidPacketException extends Exception {
    public InvalidPacketException(Exception e) {
        super(e);
    }

    public InvalidPacketException(String s) {
        super(s);
    }
}