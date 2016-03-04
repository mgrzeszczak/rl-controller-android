package pl.mg.rocketleaguecontroller.messaging;

/**
 * Created by Maciej on 27.02.2016.
 */
public class MessageType {

    public static final int ACCEPTED = 0x01;
    public static final int ID = 0x02;

    public static final int L_AXIS_X = 0x11;
    public static final int DRIVE = 0x12;
    public static final int JUMP = 0x13;
    public static final int BOOST = 0x14;
    public static final int CONTROL_LOCK = 0x15;
    public static final int START = 0x16;
    public static final int DRIFT = 0x17;
    public static final int CAMERA = 0x18;

    public static final int ID_INVALID = 0x31;
    public static final int ID_TAKEN = 0x32;
    public static final int SERVER_STOPPED = 0x33;
    public static final int INVALID_MSG_TYPE = 0x34;

}
