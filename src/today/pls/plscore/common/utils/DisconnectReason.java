package today.pls.plscore.common.utils;

public enum DisconnectReason {
    PEACEFUL_CLOSURE,
    BAD_JOIN_REQUEST,
    PROTOCOL_VARIATION;

    public static DisconnectReason fromInteger(int x) {
        switch(x) {
            case 0: return PEACEFUL_CLOSURE;
            case 10: return BAD_JOIN_REQUEST;
            case 11: return PROTOCOL_VARIATION;
        }
        return null;
    }

    public int toInteger(){
        switch(this){
            case PEACEFUL_CLOSURE:   return 0;
            case BAD_JOIN_REQUEST:   return 10;
            case PROTOCOL_VARIATION: return 11;
        }

        return -1;
    }
}
