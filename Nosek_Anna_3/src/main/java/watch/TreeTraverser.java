package watch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;


public class TreeTraverser {

    private ZooKeeper zooKeeper;
    private String startingNode;

    public TreeTraverser(ZooKeeper zooKeeper, String startingNode){
        this.zooKeeper = zooKeeper;
        this.startingNode = startingNode;
    }

    private String getIndentString(int indent) {
        return "|  ".repeat(Math.max(0, indent));
    }

    private void printNode(String node, int indent, StringBuilder sb){
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(node);
        sb.append("\n");

    }


    private void printTree(String path, int indent, StringBuilder sb) throws InterruptedException, KeeperException {
        String[] pathTokenized = path.split("/");
        String nodeName = pathTokenized[pathTokenized.length - 1];

        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(nodeName);
        sb.append("/");
        sb.append("\n");


        if(zooKeeper.exists(path, null) != null){
            for(String child : zooKeeper.getChildren(path, null)){
                if(zooKeeper.getChildren(path +"/"+ child, null).size() != 0){
                    printTree(path + "/" + child, indent + 1, sb);
                }
                else{
                    printNode(child, indent + 1, sb);
                }
            }

        }

    }

    public void printTree() throws InterruptedException, KeeperException {
        if(zooKeeper.exists(startingNode, null) == null) return;
        StringBuilder sb = new StringBuilder();
        printTree(startingNode, 0, sb);
        System.out.println(sb);
    }

    public int getTreeNodesCount() throws InterruptedException, KeeperException {
        return countTreeNodes(startingNode);
    }

    private int countTreeNodes(String node) throws InterruptedException, KeeperException {
        int childrenCount = 0;
        if(zooKeeper.exists(node, null) != null){
            for(String child : zooKeeper.getChildren(node, null)){
                childrenCount += countTreeNodes(node + "/" + child);
                childrenCount++;
            }
        }
        return childrenCount;
    }


}
