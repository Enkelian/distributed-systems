package watch;

import org.apache.zookeeper.KeeperException;

import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppRunner {


    public static final String ROOT_NODE = "/z";
    public final String HOST;
    private final NodeWatcher nodeWatcher;
    private final TreeTraverser treeTraverser;

    public AppRunner(String host, String command) throws InterruptedException, KeeperException, IOException {

        this.HOST = host;

        ZooKeeper zk = new ZooKeeper(HOST, 3000, null);

        this.treeTraverser = new TreeTraverser(zk, ROOT_NODE);
        this.nodeWatcher = new NodeWatcher(zk, ROOT_NODE, command, treeTraverser);
    }

    public void start() throws InterruptedException, KeeperException, IOException {
        nodeWatcher.start();
        run();
    }

    public void run() throws IOException, InterruptedException, KeeperException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String input = reader.readLine();
            if(input.equals("p")) treeTraverser.printTree();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        if(args.length != 2){
            System.out.println("Expected arguments: HOST PROGRAM_TO_RUN ");
            return;
        }
        AppRunner appRunner = new AppRunner(args[0], args[1]);
        appRunner.start();

    }


}
