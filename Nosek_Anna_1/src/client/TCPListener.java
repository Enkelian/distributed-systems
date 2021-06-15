package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

public class TCPListener implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ThreadManager manager;

    public TCPListener(Socket socket, ThreadManager manager) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.manager = manager;

        socket.setSoTimeout(100);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void sendMessage(String msg){
        out.println(msg);
    }

    @Override
    public void run() {
        while(true){
            try{

                if(Thread.currentThread().isInterrupted() && manager.shouldStop()){
                    System.out.println("TCP stopped");
                    return;
                }

                String receivedMsg = receiveMessage();
                if(receivedMsg == null) break;

                System.out.println("\n" + receivedMsg);
                System.out.print("Input: ");
            }
            catch (SocketTimeoutException e){ }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("TCP " + e.getMessage());
                return;
            }

        }
        System.out.println("Server has closed the connection");
        manager.shutdownNow();
    }
}
