package client;

import java.io.*;

public class InputReader implements Runnable {


    private BufferedReader reader;
    private TCPListener tcpListener;
    private UDPListener udpListener;
    private MulticastListener multicastListener;
    private ThreadManager manager;

    public InputReader(TCPListener tcpListener,
                       UDPListener udpListener,
                       MulticastListener multicastListener,
                       ThreadManager manager) {

        this.reader = new BufferedReader(new InputStreamReader(System.in));

        this.tcpListener = tcpListener;
        this.udpListener = udpListener;
        this.multicastListener = multicastListener;
        this.manager = manager;
    }

    private void sendTCPMessage(String msg){
        tcpListener.sendMessage(msg);
    }

    private void sendUDPMessage() throws IOException {
        udpListener.sendMessage(getMessage());
    }

    private void sendMulticastMessage() throws IOException {
        multicastListener.sendMessage(getMessage());
    }

    private String getMessage(){

        return ("           ,''',\n" +
                "         .' ., .',                                  ../'''',\n" +
                "        .'. %%, %.',                            .,/' .,%   :\n" +
                "       .'.% %%%,`%%%'.    .....,,,,,,.....   .,%%% .,%%'. .'\n" +
                "       : %%% %%%%%%',:%%>>%>' .,>>%>>%>%>>%%>,.   `%%%',% :\n" +
                "       : %%%%%%%'.,>>>%'   .,%>%>%'.,>%>%' . `%>>>,. `%%%:'\n" +
                "       ` %%%%'.,>>%'  .,%>>%>%' .,%>%>%' .>>%,. `%%>>,. `%\n" +
                "        `%'.,>>>%'.,%%%%%%%' .,%%>%%>%' >>%%>%>>%.`%% %% `,\n" +
                "        ,`%% %%>>>%%%>>%%>%%>>%>>%>%%%  %%>%%>%%>>%>%%%' % %,\n" +
                "      ,%>%'.>>%>%'%>>%%>%%%%>%'                 `%>%>>%%.`%>>%.\n" +
                "    ,%%>' .>%>%'.%>%>>%%%>>%' ,%%>>%%>%>>%>>%>%%,.`%%%>%%. `%>%.\n" +
                "   ` ,%' .>%%%'.%>%>>%' .,%%%%%%%%'          `%%%%%%.`%%>%% .%%>\n" +
                "   .%>% .%%>' :%>>%%'.,%%%%%%%%%'.%%%%%' `%%%%.`%%%%%.%%%%> %%>%.\n" +
                "  ,%>%' >>%%  >%' `%%%%'     `%%%%%%%'.,>,. `%%%%'     `%%%>>%%>%\n" +
                ".%%>%' .%%>'  %>>%, %% oO ~ Oo %%%>>'.>>>>>>. `% oO ~ Oo'.%%%'%>%,\n" +
                "%>'%> .%>%>%  %%>%%%'  `OoooO'.%%>>'.>>>%>>%>>.`%`OoooO'.%%>% '%>%\n" +
                "%',%' %>%>%'  %>%>%>% .%,>,>,   `>'.>>%>%%>>>%>.`%,>,>' %%%%> .>%>,\n" +
                "` %>% `%>>%%. `%%% %' >%%%%%%>,  ' >>%>>%%%>%>>> >>%%' ,%%>%'.%%>>%.\n" +
                " .%%'  %%%%>%.   `>%%. %>%%>>>%.>> >>>%>%%%%>%>>.>>>'.>%>%>' %>>%>%%\n" +
                " `.%%  `%>>%%>    %%>%  %>>>%%%>>'.>%>>>>%%%>>%>>.>',%>>%'  ,>%'>% '\n" +
                "  %>'  %%%%%%'    `%%'  %%%%%> >' >>>>%>>%%>>%>>%> %%>%>' .%>%% .%%\n" +
                " %>%>, %>%%>>%%,  %>%>% `%%  %>>  >>>%>>>%%>>>>%>>  %%>>,%>%%'.%>%,\n" +
                "%>%>%%, `%>%%>%>%, %>%%> ,%>%>>>.>>`.,.  `\"   ..'>.%. % %>%>%'.%>%%;\n" +
                "%'`%%>%  %%>%%  %>% %'.>%>>%>%%>>%::.  `,   /' ,%>>>%>. >%>%'.%>%'%'\n" +
                "` .%>%'  >%%% %>%%'.>%>%;''.,>>%%>%%::.  ..'.,%>>%>%>,`%  %'.>%%' '\n" +
                "  %>%>%% `%>  >%%'.%%>%>>%>%>%>>>%>%>>%,,::,%>>%%>%>>%>%% `>>%>'\n" +
                "  %'`%%>%>>%  %>'.%>>%>%>>;'' ..,,%>%>%%/::%>%%>>%%,,.``% .%>%%\n" +
                "  `    `%>%>>%%' %>%%>>%>>%>%>%>%%>%/'       `%>%%>%>>%%% ' .%'\n" +
                "        %'  `%>% `%>%%;'' .,>>%>%/',;;;;;,;;;;,`%>%>%,`%'   '\n" +
                "        `    `  ` `%>%%%>%%>%%;/ @a;;;;;;;;;;;a@  >%>%%'\n" +
                "                   `/////////';, `@a@@a@@a@@aa@',;`//'\n" +
                "                      `//////.;;,,............,,;;//'\n" +
                "                          `////;;;;;;;;;;;;;;;;;/'\n" +
                "                             `/////////////////'");
    }



    @Override
    public void run() {

        while(true){

            System.out.print("Input: ");

            try {

                if(Thread.currentThread().isInterrupted()){
                    System.out.println("The server has closed.");
                    if(manager.shouldStop()) return;
                }

                String msg = reader.readLine();

                switch (msg) {
                    case "U" -> sendUDPMessage();
                    case "M" -> sendMulticastMessage();
                    case "close" -> {
                        System.out.println("Closing thread...");
                        sendTCPMessage(msg);
                        manager.shutdownNow();
                        return;
                    }
                    default -> sendTCPMessage(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
