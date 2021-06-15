package watch;

import com.sun.source.tree.Tree;
import org.apache.zookeeper.*;

import java.io.IOException;

public class NodeWatcher implements Watcher {

    private final String ROOT_NODE;
    private ZooKeeper zooKeeper;
    private TreeTraverser treeTraverser;
    private int currentChildrenCount;
    private Process process;
    private String command;

    public NodeWatcher(ZooKeeper zooKeeper, String node, String command, TreeTraverser traverser) throws InterruptedException, KeeperException {
        this.zooKeeper = zooKeeper;
        this.treeTraverser = traverser;
        this.currentChildrenCount = treeTraverser.getTreeNodesCount();
        this.ROOT_NODE = node;
        this.command = command;
    }

    public void start() throws InterruptedException, KeeperException {
        zooKeeper.addWatch(ROOT_NODE, this, AddWatchMode.PERSISTENT_RECURSIVE);
    }

    @Override
    public void process(WatchedEvent event) {

        try{
            switch (event.getType()) {

                case NodeCreated -> {

                    if (event.getPath().equals(ROOT_NODE)) {
                        process = Runtime.getRuntime().exec(command);
                    }
                    else {
                        currentChildrenCount++;
                        System.out.println("Current children count: " + currentChildrenCount);
                    }

                }
                case NodeDeleted -> {
                    if (event.getPath().equals(ROOT_NODE) && process.isAlive()){
                        process.destroy();
                        currentChildrenCount = 0;
                    }
                    else{
                        currentChildrenCount--;
                    }
                }
            }
            zooKeeper.addWatch(event.getPath(), this, AddWatchMode.PERSISTENT_RECURSIVE);

        } catch (KeeperException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

}


