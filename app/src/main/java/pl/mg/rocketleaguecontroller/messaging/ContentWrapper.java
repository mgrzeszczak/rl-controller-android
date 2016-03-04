package pl.mg.rocketleaguecontroller.messaging;

import java.nio.ByteBuffer;

/**
 * Created by Maciej on 27.02.2016.
 */
public class ContentWrapper {

    private static final int INTEGER_SIZE = 4;

    public static byte[] wrapContent(int... content){
        byte[] output = new byte[INTEGER_SIZE*content.length];
        for (int i=0;i<content.length;i++){
            byte[] arr = ByteBuffer.allocate(INTEGER_SIZE).putInt(content[i]).array();
            System.arraycopy(arr,0,output,i*INTEGER_SIZE,INTEGER_SIZE);
        }
        return output;
    }

}
