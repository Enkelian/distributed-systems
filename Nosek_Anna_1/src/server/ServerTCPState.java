package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerTCPState {

    private Map<String, ServerTCPRunnable> clientRunnables;
    private ServerSocket serverSocket;
    private int lastID;

    public ServerTCPState(ServerSocket serverSocket){
        this.clientRunnables = new HashMap<>();
        this.serverSocket = serverSocket;
        this.lastID = 0;
    }

    public ServerTCPRunnable acceptNewClient() throws IOException {
        Socket newClientSocket = serverSocket.accept();
        ServerTCPRunnable client = new ServerTCPRunnable(newClientSocket, this, "" + lastID);
        registerNewClient("" + lastID, client);
        incrementID();
        return client;
    }

    private void incrementID(){
        lastID++;
    }

    public synchronized void registerNewClient(String nick, ServerTCPRunnable client) {
        clientRunnables.put(nick, client);
    }

    public synchronized void sendToAllExcept(String nick, String msg){

        msg = nick + ": " + msg;
        System.out.println(msg);

        for(String serverNick : clientRunnables.keySet()) {
            if(!nick.equals(serverNick))
                clientRunnables.get(serverNick).sendMsg(msg);
        }
    }

    public synchronized void removeClient(String nick){
        clientRunnables.remove(nick);
    }

}
