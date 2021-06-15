package client;


import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPListener implements Runnable {

    private DatagramSocket socket;
    private byte[] msgBuffer;
    private InetAddress inetAddress;
    private int portNumber;
    private ThreadManager manager;
    private static final int MSG_SIZE = 57400;

    public UDPListener(DatagramSocket socket, InetAddress inetAddress, int portNumber, ThreadManager manager) throws SocketException {
        this.socket = socket;
        this.msgBuffer = new byte[MSG_SIZE];
        this.inetAddress = inetAddress;
        this.portNumber = portNumber;
        this.manager = manager;

        socket.setSoTimeout(100);
    }

    public void sendMessage(String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, portNumber);
        socket.send(packet);
    }

    @Override
    public void run() {

        while(true){

            if(Thread.currentThread().isInterrupted() && manager.shouldStop()){
                System.out.println("UDP stopped");
                return;
            }

            Arrays.fill(msgBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(msgBuffer, msgBuffer.length);
            try {
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                System.out.println("\n" + msg);
                System.out.print("Input: ");
            }
            catch (SocketTimeoutException e){ }
            catch (IOException e) {
                System.out.println("UDP " + e.getMessage());
                return;
            }
        }

    }
}
