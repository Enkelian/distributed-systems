package client;

import java.io.IOException;
import java.net.*;

public class Client {

    private static Socket tcpSocket;
    private static MulticastSocket multicastSocket;
    private static InetAddress inetAddress;


    public static void main(String[] args) throws IOException {

        String hostName = "localhost";
        int portNumber = 12345;

        inetAddress = InetAddress.getByName(hostName);


        int multicastPort = 12346;
        InetAddress mcastaddr = InetAddress.getByName("228.5.6.7");
        InetSocketAddress group = new InetSocketAddress(mcastaddr, multicastPort);
        NetworkInterface netIf = NetworkInterface.getByName("bge0");

        ThreadManager threadManager = new ThreadManager(4);

        try {
            tcpSocket = new Socket(hostName, portNumber);

            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.joinGroup(group, netIf);

            DatagramSocket datagramSocket = new DatagramSocket();
            TCPListener tcpListener = new TCPListener(tcpSocket, threadManager);
            UDPListener udpListener = new UDPListener(datagramSocket, inetAddress, portNumber, threadManager);


            String nick = tcpListener.receiveMessage();
            System.out.println("Received nick " + nick);
            String nickMessage = "Register " + nick;


            MulticastListener multicastListener = new MulticastListener(multicastSocket, nick, group, threadManager);

            InputReader inputReader = new InputReader(
                    tcpListener,
                    udpListener,
                    multicastListener,
                    threadManager
            );


            udpListener.sendMessage(nickMessage);

            threadManager.startRunnable(udpListener);
            threadManager.startRunnable(multicastListener);
            threadManager.startRunnable(tcpListener);
            threadManager.startRunnable(inputReader);

            threadManager.shutdown();
            threadManager.awaitTermination();

            System.out.println("Closing client...");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tcpSocket != null) tcpSocket.close();
            if (multicastSocket != null) multicastSocket.leaveGroup(group, netIf);
            if (multicastSocket != null) multicastSocket.close();
        }
    }
}
