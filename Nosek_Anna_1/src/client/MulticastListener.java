package client;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class MulticastListener implements Runnable {

    private MulticastSocket socket;
    private byte[] msgBuffer;
    private String nick;
    private InetSocketAddress groupAddress;
    private ThreadManager manager;
    private static final int MSG_SIZE = 57400;

    public MulticastListener(MulticastSocket multicastSocket, String nick, InetSocketAddress groupAddress, ThreadManager manager) throws SocketException {
        this.socket = multicastSocket;
        this.msgBuffer = new byte[MSG_SIZE];
        this.nick = nick;
        this.groupAddress = groupAddress;
        this.manager = manager;

        socket.setSoTimeout(100);

    }


    public void sendMessage(String msg) throws IOException {

        byte[] buffer = (nick + "\n" + msg).getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress);
        socket.send(packet);
    }


    @Override
    public void run() {


        while(true){

            if(Thread.currentThread().isInterrupted() && manager.shouldStop()){
                System.out.println("Multicast stopped");
                return;
            }

            Arrays.fill(msgBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(msgBuffer, msgBuffer.length);
            try {
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                if(!msg.startsWith(nick)) {
                    System.out.println("\n" +msg);
                    System.out.print("Input: ");
                }
            }
            catch (SocketTimeoutException e) {}
            catch (IOException e) {
                System.out.println("Multicast " + e.getMessage());
                return;
            }
        }

    }
}
