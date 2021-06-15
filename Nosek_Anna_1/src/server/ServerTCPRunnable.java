package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerTCPRunnable implements Runnable {

    private PrintWriter out;
    private BufferedReader in;
    private ServerTCPState serverState;
    private String nick;

    public ServerTCPRunnable(Socket clientSocket, ServerTCPState serverState, String nick) throws IOException {
        this.serverState = serverState;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.nick = nick;
    }

    public PrintWriter getOut(){
        return out;
    }

    public String getNick(){ return nick; }

    public void sendMsg(String msg){
        getOut().println(msg);
    }

    @Override
    public void run(){

        sendMsg(nick);

        while(true){

            try {

                String msg = in.readLine();

                if(msg.equals("close")){
                    System.out.println("Closing " + nick);
                    serverState.removeClient(nick);
                    return;
                }

                serverState.sendToAllExcept(nick, msg);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

}
