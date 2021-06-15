package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private static ServerSocket serverSocket;
    private static DatagramSocket datagramSocket;
    private static final int MAX_CLIENT_THREADS = 9;

    public static void main(String[] args) throws IOException, InterruptedException {

        int portNumber = 12345;

        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENT_THREADS + 1);

        try {

            serverSocket = new ServerSocket(portNumber);

            ServerTCPState serverState = new ServerTCPState(serverSocket);

            datagramSocket = new DatagramSocket(portNumber);

            threadPool.submit(new ServerUDPRunnable(datagramSocket));

            while(true){
                ServerTCPRunnable clientTCPRunnable = serverState.acceptNewClient();
                threadPool.submit(clientTCPRunnable);
                System.out.println(clientTCPRunnable.getNick() + " connected");
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(serverSocket != null) serverSocket.close();
            if(datagramSocket != null) datagramSocket.close();
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            threadPool.shutdownNow();
        }
    }
}
