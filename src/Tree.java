import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

public class Tree {
    private Node rootPointer;
    public Node leafHeader;      //Change to private!!!
    private int height;
    private int NODE_FANOUT = 5;

    public Tree() {
        rootPointer = new Node(true);
        leafHeader = rootPointer;
        height = 0;
    }

    public Tree(String address) {                //initialize a tree object with a file address
        rootPointer = new Node(true);
        leafHeader = rootPointer;
        height = 0;
        ReadFile(address);
    }

    /**
     * this method to search the value within the query range
     *
     * @param key1 search key value 1
     * @param key2 search key value 2
     * @return a linked list of integer
     */
    public LinkedList<Integer> Search(int key1, int key2) {

        LinkedList<Integer> result = rootPointer.Search(key1, key2);       // can be used to collect range search result

        if (result == null) {
            System.out.println("None");
        }
        System.out.println("search " + key1 + " " + key2 + " found " + result.size() + " result(s)");
        return result;

    }

    /**
     * This method displays the statistic of b+-tree
     */
    public void DumpStatistics() {
        int nodeNum = 0;

        int dataEntryNum = 0;
        int indexEntryNum = 0;
        if (rootPointer != null) {
            nodeNum = GetNodeNum(rootPointer);
            dataEntryNum = GetDataEntryNum(rootPointer);
            indexEntryNum = GetIndexEntryNum(rootPointer);
        }
        float fillFactor = (float) (dataEntryNum + indexEntryNum) / (float) nodeNum;

        System.out.println("Total number of nodes: " + nodeNum);
        System.out.println("Total number of data entries: " + dataEntryNum);
        System.out.println("Total number of index entries: " + indexEntryNum);
        System.out.println("Average fill factor of nodes: " + fillFactor);
        System.out.println("Height: " + height);
    }

    /**
     * This method get the number of node in b+-tree
     *
     * @param pointer the pointer which starts searching
     * @return the number of node in the start pointer
     */
    private int GetNodeNum(Node pointer) {
        int total = 0;
        if (pointer.GetIsLeafNode()) {
            return NODE_FANOUT - 1;
        } else {
            total += NODE_FANOUT - 1;
            for (Node node : pointer.GetPointerList()) {
                int nodeNum = GetNodeNum(node);
                total += nodeNum;
            }

            return total;
        }
    }

    /**
     * This method get the number of data entries in b+-tree
     *
     * @param pointer the pointer which start searching
     * @return return the number of data entries in the start pointer
     */
    private int GetDataEntryNum(Node pointer) {
        int total = 0;
        if (pointer.GetIsLeafNode()) {
            return pointer.GetSize();
        } else {
            for (Node node : pointer.GetPointerList()) {
                int dataEntryNum = GetDataEntryNum(node);
                total += dataEntryNum;
            }

            return total;
        }
    }

    /**
     * This method get the number of index entry in B+-tree
     *
     * @param pointer the pointer which starts searching
     * @return the number of index entry in the start pointer
     */
    private int GetIndexEntryNum(Node pointer) {
        int total = 0;
        if (pointer.GetIsLeafNode()) {
            return 0;
        } else {
            total += pointer.GetSize();
            for (Node node : pointer.GetPointerList()) {
                int dataEntryNum = GetIndexEntryNum(node);
                total += dataEntryNum;
            }

            return total;
        }
    }

    /**
     * This method read file from address and build a b+-tree
     *
     * @param address the file path of key value file
     */
    public void ReadFile(String address) {
        int[] array = new int[7];
        String tmp = "";
        String[] splitTmp;
        try {

            File inputFile = new File(address);
            InputStream in = new FileInputStream(inputFile);
            int size = in.available();

            for (int i = 0; i < size; i++) {

                tmp += (char) in.read() + "";        //store the input stream into a string

            }

            splitTmp = tmp.split(",");

            for (String s : splitTmp) {
                Insert(Integer.parseInt(s));        //splite the string and turn value into integer
            }


            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * This method inserts the number into existed b+-tree
     *
     * @param input the new number needs to be inserted
     */
    public void Insert(int input) {

        Integer tmp = rootPointer.Insert(input);

        if (tmp != null) {                                    //root node need to be split
            Node newRoot = new Node(false);

            newRoot.GetValueList().add(new Pair(tmp, 0));        //insert the value

            newRoot.GetPointerList().add(rootPointer);
            newRoot.GetPointerList().add(1, rootPointer.GetNextPointer());   //insert pointers

            rootPointer = newRoot;
            height++;
        }

    }

    /**
     * This method deletes the number from existed b+-tree
     *
     * @param input the number needs to be deleted
     * @return whether the deletion is successful or not
     */
    public boolean Delete(int input) {
        boolean isSuccess = rootPointer.Delete(input);

        if (rootPointer.changeLeafHeader) {             //adjust the leafHeader to a correct Node
            leafHeader = rootPointer;

            for (int i = 0; i < height; i++) {
                leafHeader.changeLeafHeader = false;
                leafHeader = leafHeader.GetPointerList().getFirst();
            }
        }

        if (rootPointer.GetSize() == 0) {                       // if the root node become empty

            rootPointer = rootPointer.GetPointerList().getFirst(); //change root pointer

            height--;

            System.gc();

        }


        return isSuccess;

    }

    /**
     * This method print the tree with given format
     */
    public void PrintTree() {
        int count = 0;
        Node levelPointer = rootPointer;
        Node pointer = levelPointer;

        for (int i = 0; i < height + 1; i++) {

            //System.out.println("height: "+i);
            //System.out.println();

            while (true) {
                System.out.print(count + ": ");
                pointer.PrintNode();
                System.out.println();

                pointer = pointer.GetNextPointer();

                count++;

                if (pointer == null) {
                    break;
                }
            }

            if (i != height) {
                levelPointer = levelPointer.GetPointerList().getFirst();
                pointer = levelPointer;
            } else {
                pointer = leafHeader;
            }
        }

    }

}
