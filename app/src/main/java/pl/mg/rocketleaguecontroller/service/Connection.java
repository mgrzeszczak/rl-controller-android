package pl.mg.rocketleaguecontroller.service;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

import pl.mg.rocketleaguecontroller.R;
import pl.mg.rocketleaguecontroller.activities.communication.BroadcastDispatcher;
import pl.mg.rocketleaguecontroller.activities.communication.Dispatch;
import pl.mg.rocketleaguecontroller.exception.ConnectionException;
import pl.mg.rocketleaguecontroller.injection.Injector;
import pl.mg.rocketleaguecontroller.messaging.ContentWrapper;
import pl.mg.rocketleaguecontroller.messaging.MessagePriority;
import pl.mg.rocketleaguecontroller.messaging.MessageType;
import pl.mg.rocketleaguecontroller.messaging.MessageWrapper;

import static pl.mg.rocketleaguecontroller.messaging.MessageType.ACCEPTED;
import static pl.mg.rocketleaguecontroller.messaging.MessageType.INVALID_MSG_TYPE;
import static pl.mg.rocketleaguecontroller.messaging.MessageType.SERVER_STOPPED;

/**
 * Created by Maciej on 24.02.2016.
 */
public class Connection {

    @Inject
    Context context;
    @Inject
    TaskScheduler taskScheduler;

    private static final int HEADER_SIZE = 8;
    private static final int INTEGER_SIZE = 4;
    private static final int BUFFER_SIZE = 128;

    private Object lock = new Object();

    private Socket socket = null;
    private Listener listener = null;
    private Messenger messenger = null;

    private boolean connected = false;

    public Connection(){
        Injector.INSTANCE.getApplicationComponent().inject(this);
    }
    /***
     *
     * @param host
     * @param port
     * @param controllerId
     */
    public void connect(String host, int port, int controllerId) {
        try {
            beginConnection(host,port,controllerId);
        } catch (IOException e){
            e.printStackTrace();
            BroadcastDispatcher.dispatch(Dispatch.CONNECTION_ERROR,context);
        }
    }

    public void disconnect() {
        synchronized(lock){
            if (connected == false) return;
            messenger.takePoisonPill();
            connected = false;
            try {
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            socket = null;
            listener = null;
            messenger = null;
            BroadcastDispatcher.dispatch(Dispatch.DISCONNECTED,context);
        }
    }

    private void beginConnection(String host, int port, int controllerId) throws IOException {
        // Connect socket
        SocketAddress address = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.connect(address, context.getResources().getInteger(R.integer.socket_timeout));

        synchronized(lock){
            connected = true;
        }
        // Initiate messenger
        messenger = new Messenger(socket.getOutputStream());
        taskScheduler.schedule(messenger);
        // Send init message with controller id
        int resp = handshake(socket, controllerId);
        if (resp!= ACCEPTED) {
            synchronized(lock) {
                connected = false;
                messenger.takePoisonPill();
                socket.close();
                socket = null;
                messenger = null;
                listener = null;
            }
            Log.d("resp","Resp: "+resp);
            BroadcastDispatcher.dispatch(Dispatch.CONNECTION_ERROR,context,resp);
            return;
        }
        // Success
        listener = new Listener(socket.getInputStream());
        taskScheduler.schedule(listener);
        BroadcastDispatcher.dispatch(Dispatch.CONNECTED, context);
    }

    private int handshake(Socket socket, int controllerId) throws IOException{
        messenger.queueMessage(MessageWrapper.wrapMessage(MessageType.ID,ContentWrapper.wrapContent(controllerId)));
        int[] resp = readHeader(socket.getInputStream());
        return resp[1];
    }

    public boolean isConnected(){
        synchronized(lock){
            return connected;
        }
    }

    private void handleMessage(int type, byte[] content){
        switch (type){
            case SERVER_STOPPED:
                disconnect();
                break;
            case INVALID_MSG_TYPE:
                break;
        }
    }

    public void write(byte[] message, MessagePriority priority){
        if (!isConnected()) return;
        if (priority == MessagePriority.LOW) messenger.queueMessage(message);
        else messenger.queuePriorityMessage(message);
    }

    private int[] readHeader(InputStream is) throws IOException {
        byte[] buffer = new byte[HEADER_SIZE];
        int read = 0;

        while (read < HEADER_SIZE){
            int lastRead = is.read(buffer,read,HEADER_SIZE-read);
            Log.e("lastRead",String.valueOf(lastRead));
            if (lastRead==-1){
                disconnect();
                break;
            }
            read+=lastRead;
        }

        byte[] blength = new byte[INTEGER_SIZE];
        byte[] btype = new byte[INTEGER_SIZE];

        System.arraycopy(buffer, 0, blength, 0, INTEGER_SIZE);
        System.arraycopy(buffer, INTEGER_SIZE, btype, 0, INTEGER_SIZE);
        int[] out = new int[2];
        out[0] = ByteBuffer.allocate(INTEGER_SIZE).wrap(blength).order(ByteOrder.LITTLE_ENDIAN).getInt() - INTEGER_SIZE;
        out[1] = ByteBuffer.allocate(INTEGER_SIZE).wrap(btype).order(ByteOrder.LITTLE_ENDIAN).getInt();
        return out;
    }

    private byte[] readContent(InputStream is, int length) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];

        int savedLength = length;
        while (length>0){
            int read = is.read(buffer,0,length);
            if (read==-1){
                disconnect();
                break;
            }
            length-=read;
            baos.write(buffer,0,read);
        }
        return baos.toByteArray();
    }

    private class Listener implements Runnable {
        private InputStream is;
        public Listener(InputStream is){
            this.is = is;
        }
        @Override
        public void run() {
            try {
                listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            disconnect();
            Log.d("otherThreads", "Listener dies");
        }
        private void listen() throws IOException {
            while (isConnected()){
                int[] header = readHeader(is);
                int length = header[0];
                int type = header[1];
                byte[] content = readContent(is,length);
                handleMessage(type,content);
            }
        }
    }

    private class Messenger implements Runnable {

        private static final int QUEUE_MAX_SIZE = 10;
        private byte[] poisonPill;
        private BlockingQueue<byte[]> messageQueue = new LinkedBlockingQueue<>();

        private BlockingDeque<byte[]> messageDeque = new LinkedBlockingDeque<>();
        private OutputStream os;

        public Messenger(OutputStream os){
            this.os = os;
            this.poisonPill = new byte[1];
        }

        public void queueMessage(byte[] message){
            //if (messageQueue.size()<QUEUE_MAX_SIZE) messageQueue.add(message);
            if (messageDeque.size()<QUEUE_MAX_SIZE) messageDeque.add(message);
        }

        public void queuePriorityMessage(byte[] message){
            //messageQueue.offer(message);
            messageDeque.offer(message);
        }

        @Override
        public void run() {
            while (isConnected()){
                byte[] message = null;
                try {
                    //message = messageQueue.take();
                    message = messageDeque.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (checkPoison(message)) {
                    Log.d("other threads", "Messanger poisoned");
                    break;
                }

                try {
                    os.write(message);
                } catch (IOException e){
                    disconnect();
                }
            }
            Log.d("otherThreads", "Messenger dies");
        }

        private boolean checkPoison(byte[] message){
            return message==poisonPill;
        }

        public void takePoisonPill(){
            messageQueue.add(poisonPill);
        }

    }
}
