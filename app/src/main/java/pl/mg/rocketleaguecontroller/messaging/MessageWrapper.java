package pl.mg.rocketleaguecontroller.messaging;

import java.nio.ByteBuffer;

/**
 * Created by Maciej on 27.02.2016.
 */
public class MessageWrapper {

    public static byte[] wrapMessage(int type, byte[] content){
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(4+content.length).array();
        byte[] typeBytes = ByteBuffer.allocate(4).putInt(type).array();
        byte[] output = new byte[lengthBytes.length+typeBytes.length+content.length];
        System.arraycopy(lengthBytes,0,output,0,lengthBytes.length);
        System.arraycopy(typeBytes,0,output,lengthBytes.length,typeBytes.length);
        System.arraycopy(content,0,output,lengthBytes.length+typeBytes.length,content.length);
        return output;
    }

    public static byte[] wrapMessage(int type){
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(4).array();
        byte[] typeBytes = ByteBuffer.allocate(4).putInt(type).array();
        byte[] output = new byte[lengthBytes.length+typeBytes.length];
        System.arraycopy(lengthBytes,0,output,0,lengthBytes.length);
        System.arraycopy(typeBytes,0,output,lengthBytes.length,typeBytes.length);
        return output;
    }

}
