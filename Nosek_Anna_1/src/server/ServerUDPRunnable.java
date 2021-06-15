package server;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

public class ServerUDPRunnable implements Runnable {

    private DatagramSocket socket;
    private byte[] msgBuffer;
    private HashMap<SocketAddress, String> clientNicks;
    private static final int MSG_SIZE = 57400;

    public ServerUDPRunnable(DatagramSocket datagramSocket) throws SocketException {
        this.socket = datagramSocket;
        this.msgBuffer = new byte[MSG_SIZE];
        this.clientNicks = new HashMap<>();
    }

    private void register(SocketAddress address, String nickMsg) {
        String nick = nickMsg.split(" ")[1];
        nick = nick.replace("\0", "");
        clientNicks.put(address, nick);
        System.out.println("Registered " + nick);
    }

    private void sendToAllExcept(SocketAddress address, String msg) throws IOException {

        msg = clientNicks.get(address) + "\n" + msg;

        for(SocketAddress addr : clientNicks.keySet()){
            if(!addr.equals(address)){
                sendTo(addr, msg);
                System.out.println("UDP: sent to " + clientNicks.get(addr));
            }
        }
    }

    private void sendTo(SocketAddress address, String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
        socket.send(packet);
    }

    @Override
    public void run() {

        while(true){

            Arrays.fill(msgBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(msgBuffer, msgBuffer.length);
            try {
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                if(msg.startsWith("Register")){
                    register(receivePacket.getSocketAddress(), msg);
                }
                else {
                    SocketAddress addr = receivePacket.getSocketAddress();
                    System.out.println("UDP: received from " + clientNicks.get(addr));
                    sendToAllExcept(addr, msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
