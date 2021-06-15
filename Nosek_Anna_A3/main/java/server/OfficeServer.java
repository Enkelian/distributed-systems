package server;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;


public class OfficeServer {


    public void run(String[] args) {
        int status = 0;
        Communicator communicator = null;

        try	{
            communicator = Util.initialize(args);

            ObjectAdapter adapter = communicator.createObjectAdapter("Adapter1");

            OfficeServantLocator servantLocator = new OfficeServantLocator(adapter);
            adapter.addServantLocator(servantLocator, "");


            adapter.activate();

            System.out.println("Entering event processing loop...");

            communicator.waitForShutdown();

        }
        catch (Exception e) {
            System.err.println(e);
            status = 1;
        }
        if (communicator != null) {
            try {
                communicator.destroy();
            }
            catch (Exception e) {
                System.err.println(e);
                status = 1;
            }
        }
        System.exit(status);
    }


    public static void main(String[] args)
    {
        OfficeServer app = new OfficeServer();
        app.run(args);
    }
}
