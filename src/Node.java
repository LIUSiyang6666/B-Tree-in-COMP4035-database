import java.util.LinkedList;

public class Node {
    private boolean isLeafNode;                 //whether this is a leaf node or a inner node
    private int NODE_FANOUT = 5;                      // the max of number of pointer in a node
    private LinkedList<Pair> valueList = new LinkedList<>(); // list to contain values, key rid pair

    // leafNode setting
    private Node nextPointer;               // pointer point to the next leafNode
    private Node prePointer;              // pointer point to the previous leafNode

    //inner node setting
    private LinkedList<Node> pointerList = new LinkedList<>();   // list of pointers

    public boolean changeLeafHeader = false;


    public Node(boolean isLeaf) {
        isLeafNode = isLeaf;
        if (isLeaf) {
            pointerList = null;
        } else {
            nextPointer = null;
            prePointer = null;
        }
    }

    public boolean GetIsLeafNode() {
        return isLeafNode;
    }

    public Node GetNextPointer() {
        return nextPointer;
    }

    public Node GetPrePointer() {
        return prePointer;
    }

    public int GetSize() {
        return valueList.size();
    }

    public void SetNextPointer(Node nextPointer) {
        this.nextPointer = nextPointer;
    }

    public void SetPrePointer(Node prePointer) {
        this.prePointer = prePointer;
    }

    public LinkedList<Node> GetPointerList() {
        return pointerList;
    }

    public LinkedList<Pair> GetValueList() {
        return valueList;
    }

    /**
     * This method print the key value inside a node
     */
    public void PrintNode() {
        for (Pair i : valueList) {
            System.out.print(i.key + " ");
        }
    }

    /**
     * This method search the b+-tree and return the correct result
     *
     * @param key1 the smaller one key
     * @param key2 the bigger one key
     * @return a linked list of integer within the required range
     */
    public LinkedList<Integer> Search(int key1, int key2) {
        LinkedList<Integer> result = new LinkedList<>();
        if (isLeafNode) {

            for (Pair i : valueList) {                              // search leaf node
                if (i.key >= key1 && i.key <= key2) {
                    result.add(i.key);
                }
            }
            return result;

        } else {                                                  // search inner node
            int i;
            int size = valueList.size();
            LinkedList<Integer> tmp;

            for (i = 0; i < size; i++) {                                     // pre several pointer
                if (valueList.get(i).key >= key2) {
                    tmp = pointerList.get(i).Search(key1, key2);

                    if (tmp != null) {
                        result.addAll(tmp);
                    }

                    return result;
                } else if (valueList.get(i).key >= key1 && valueList.get(i).key <= key2) {
                    tmp = pointerList.get(i).Search(key1, valueList.get(i).key);
                    key1 = valueList.get(i).key;

                    if (tmp != null) {
                        result.addAll(tmp);
                    }
                }
            }

            tmp = pointerList.get(size).Search(key1, key2);      //the last pointer
            if (tmp != null) {
                result.addAll(tmp);
            }

            return result;
        }
    }

    /**
     * This method searches the integer and check whether the number exists or not
     *
     * @param input the number needs to be searched
     * @return if exist, return true; otherwise return false
     */
    public boolean Search(int input) {          //
        if (isLeafNode) {

            for (Pair i : valueList) {                              // search leaf node
                if (i.key == input) {
                    //result.add(i.key);
                    return true;
                }
            }
            return false;

        } else {                                                  // search inner node

            boolean tmp = false;
            int i;
            int size = valueList.size();

            for (i = 0; i < size; i++) {                                     //pre several pointer
                if (input < valueList.get(i).key) {
                    tmp = pointerList.get(i).Search(input);
                    return tmp;
                }
            }

            tmp = pointerList.get(size).Search(input);      //the last pointer

            return tmp;

        }
    }

    /**
     * This method insert a integer into this node
     *
     * @param input the value needs to be inserted in the node
     */
    private void InsertValue(int input) {

        boolean isFinished = false;

        for (int i = 0; i < valueList.size(); i++) {
            if (input < valueList.get(i).key) {             //input is smaller than some key
                valueList.add(i, new Pair(input, 0));

                if (!isLeafNode) {                      //if is not leafNode, also insert the pointer to the pointer list
                    Node tmp = pointerList.get(i).nextPointer;
                    pointerList.add(i + 1, tmp);
                }

                return;

            }
        }

        int nowSize = valueList.size();

        valueList.add(nowSize, new Pair(input, 0));  //input is larger than every key

        if (!isLeafNode) {                      //if is not leafNode, also insert the pointer to the pointer list
            Node tmp = pointerList.get(nowSize).nextPointer;
            pointerList.add(nowSize + 1, tmp);
        }


    }

    /**
     * This method deals with nodes need split
     *
     * @return the key value which needs to be added to upper node
     */
    private Integer Split() {

        Node newNode = new Node(isLeafNode);
        int pos = (NODE_FANOUT - 1) / 2;

        for (int i = valueList.size(); i > pos; i--) {
            newNode.valueList.add(valueList.remove(pos));       // insert the latter part of value into new node

            if (!isLeafNode) {
                newNode.pointerList.add(pointerList.remove(pos + 1));    //for inner node, the pointer list also need to be edited
            }
        }

        if (this.nextPointer != null) {
            this.nextPointer.prePointer = newNode;              //update the list of leaf node , also connect inner nodes
            newNode.SetNextPointer(this.nextPointer);
        }
        this.nextPointer = newNode;
        newNode.SetPrePointer(this);


        if (isLeafNode) {
            return newNode.valueList.get(0).key;              // return the value should be added to the upper node

        } else {
            int tmp = newNode.valueList.get(0).key;             // for the inner node case
            newNode.valueList.remove(0);
            return tmp;
        }

    }

    /**
     * insert in a leafNode, if not full,return true; if full return false
     *
     * @param input the number needs to be inserted
     * @return null if do not need split otherwise
     */
    public Integer Insert(int input) {
        //search the leaf node to insert

        // if do not need split just return null;
        if (!isLeafNode) {                                   // search inner node

            Integer tmp = -1;
            int size = valueList.size();
            boolean isFinished = false;

            for (int i = 0; i < size; i++) {                                 //pre several pointer
                if (input < valueList.get(i).key) {
                    tmp = pointerList.get(i).Insert(input);
                    isFinished = true;
                    break;
                }
            }

            if (!isFinished) {
                tmp = pointerList.get(size).Insert(input);      //the last pointer
                isFinished = false;
            }

            //get back from lower nodes, if tmp is null, which means no overflow, return directly;
            // if tmp is not null, means there is overflow to deal with

            if (tmp != null) {

                InsertValue(tmp);                       //insert the value that lower nodes pushed up and insert pointers into pointer lists

                if (valueList.size() > NODE_FANOUT - 1) {                // case :need split
                    return Split();
                }

            }

        } else {                                              // insert in the leaf node

            InsertValue(input);

            if (valueList.size() > NODE_FANOUT - 1) {                // case :need split
                return Split();
            }

        }
        return null;

    }

    private boolean DeleteValue(int input) {           //delete a value in a straight forward way

        int nowSize = valueList.size();

        for (int i = 0; i < nowSize; i++) {
            if (input == valueList.get(i).key) {
                valueList.remove(i);
                return true;
            }
        }
        return false;

    }

    /**
     * This method merges two nodes and rebuild the pointers of the node
     *
     * @param idx      one node needs to be merged
     * @param otherIdx another node needs to be merged
     */
    private void Merge(int idx, int otherIdx) {
        Node ufNode = pointerList.get(idx);    //underflowNode
        Node otherNode = pointerList.get(otherIdx);

        if (idx < otherIdx) {                 //underflow node is on the left side of the other node

            if (!ufNode.GetIsLeafNode()) {
                otherNode.valueList.offerFirst(valueList.remove(idx));   //remove the upper node value and put into a lower node
            } else {
                valueList.remove(idx);
            }

            for (int i = (NODE_FANOUT - 5) / 2; i >= 0; i--) {
                otherNode.valueList.offerFirst(ufNode.valueList.remove(0)); //move value
            }

            if (!ufNode.GetIsLeafNode()) {
                for (int i = (NODE_FANOUT - 3) / 2; i >= 0; i--) {
                    otherNode.pointerList.offerFirst(ufNode.pointerList.remove(i)); //move pointer
                }
            }


            if (ufNode.prePointer != null) {
                otherNode.prePointer = ufNode.prePointer;   //remove pointer with sibling
                ufNode.prePointer.nextPointer = otherNode;
            } else {
                otherNode.prePointer = null;            // remove previous node

                if (ufNode.GetIsLeafNode()) {
                    // leafHeader pointer point to otherNode
                    changeLeafHeader = true;
                }

            }


        } else {                              //underflow node is on the left side of the other node

            if (!ufNode.GetIsLeafNode()) {
                otherNode.valueList.offerLast(valueList.remove(otherIdx));   //remove the upper node value and put into a lower node
            } else {
                valueList.remove(otherIdx);
            }

            for (int i = 0; i < (NODE_FANOUT - 3) / 2; i++) {
                otherNode.valueList.offerLast(ufNode.valueList.remove(0)); //move value
            }

            if (!ufNode.GetIsLeafNode()) {

                for (int i = 0; i < (NODE_FANOUT - 1) / 2; i++) {

                    otherNode.pointerList.offerLast(ufNode.pointerList.remove(0)); //move pointer

                }
            }


            if (ufNode.nextPointer != null) {
                otherNode.nextPointer = ufNode.nextPointer;   //remove pointer with sibling
                ufNode.nextPointer.prePointer = otherNode;
            } else {
                otherNode.nextPointer = null;
            }

        }

        pointerList.remove(idx); //remove pointer from parent

        System.gc();

    }

    /**
     * This method is for the leaf nodes which needs to borrow left or right siblings
     *
     * @param borrowIdx The index of node which needs to borrow value from left or right nodes
     * @param lendIdx   The index of node which lend value to the borrow node
     */
    private void BorrowLeaf(int borrowIdx, int lendIdx) {
        Node borrower = pointerList.get(borrowIdx);
        Node lender = pointerList.get(lendIdx);

        if (borrowIdx < lendIdx) {                                            // borrow from right sibling

            borrower.valueList.offerLast(lender.valueList.remove(0)); //remove the first of right sibling and add to the underflow node

            valueList.remove(borrowIdx);                                    //deal with the part of the node now
            valueList.add(borrowIdx, lender.GetValueList().getFirst());

        } else {                                                               // borrow from left sibling

            int size = lender.valueList.size();
            borrower.valueList.offerFirst(lender.valueList.remove(size - 1)); //remove the last of left sibling and add to the underflow node

            valueList.remove(lendIdx);                                    //deal with the part of the node now
            valueList.add(lendIdx, borrower.GetValueList().getFirst());       //which is on the left, the valueList use which value
        }

    }

    /**
     * This method is for the inner nodes which needs to borrow left or right siblings
     *
     * @param borrowIdx The index of node which needs to borrow value from left or right nodes
     * @param lendIdx   The index of node which lend value to the borrow node
     */
    private void BorrowInner(int borrowIdx, int lendIdx) {
        Node borrower = pointerList.get(borrowIdx);
        Node lender = pointerList.get(lendIdx);

        if (borrowIdx < lendIdx) {                                            // borrow from right sibling

            borrower.valueList.offerLast(valueList.remove(borrowIdx));    //remove the value in current node and add to the underflow node

            borrower.pointerList.offerLast(lender.GetPointerList().remove(0)); // move pointer
            //deal with the part of the node now

            valueList.add(borrowIdx, lender.GetValueList().remove(0));


        } else {                                                               // borrow from left sibling

            int size = lender.valueList.size();
            borrower.valueList.offerFirst(valueList.remove(lendIdx)); //remove the last of left sibling and add to the underflow node

            borrower.pointerList.offerFirst(lender.GetPointerList().remove(size)); // move pointer

            valueList.add(lendIdx, lender.GetValueList().remove(size - 1));
        }

    }

    /**
     * This method first tries to borrow nodes from siblings, otherwise this method merges nodes of siblings
     *
     * @param orgIdx The index of the node whose leaf node is deleted
     */
    private void SolveUnderflow(int orgIdx) {              //borrow first, if cannot borrow, then merge

        int otherIdx;

        if (orgIdx - 1 >= 0) {                               // borrow from left sibling

            otherIdx = orgIdx - 1;

            if (pointerList.get(otherIdx).GetSize() > (NODE_FANOUT - 1) / 2) {

                if (pointerList.get(orgIdx).GetIsLeafNode()) {    //borrow between leaf nodes
                    BorrowLeaf(orgIdx, otherIdx);
                } else {                                            //borrow between inner nodes
                    BorrowInner(orgIdx, otherIdx);
                }
                return;
            }

        }

        if (orgIdx + 1 < pointerList.size()) {

            otherIdx = orgIdx + 1;
            if (pointerList.get(otherIdx).GetSize() > (NODE_FANOUT - 1) / 2) {

                if (pointerList.get(orgIdx).GetIsLeafNode()) {    //borrow between leaf nodes
                    BorrowLeaf(orgIdx, otherIdx);
                } else {                                            //borrow between inner nodes
                    BorrowInner(orgIdx, otherIdx);
                }

                return;
            }

        }

        if (orgIdx - 1 >= 0) {                               // merge with left sibling

            otherIdx = orgIdx - 1;

            Merge(orgIdx, otherIdx);

        } else if (orgIdx + 1 < pointerList.size()) {

            otherIdx = orgIdx + 1;

            Merge(orgIdx, otherIdx);
        }


    }

    /**
     * Delete the key value in node and rebuild the b+-tree if needed
     *
     * @param input the number needs to be deleted
     * @return the value is deleted successfully or not
     */
    public boolean Delete(int input) {

        if (!isLeafNode) {                                   // search inner node

            boolean tmp = false;

            int size = valueList.size();
            boolean isFinished = false;
            int i;

            for (i = 0; i < size; i++) {                                 //pre several pointer
                if (input < valueList.get(i).key) {
                    tmp = pointerList.get(i).Delete(input);
                    isFinished = true;
                    break;
                }
            }

            if (!isFinished) {
                tmp = pointerList.get(size).Delete(input);      //the last pointer
                isFinished = false;
            }

            //get back from lower nodes

            //check whether need to split or redistribution

            if (pointerList.get(i).changeLeafHeader) {     //pass the message up
                changeLeafHeader = true;
            }

            if (pointerList.get(i).GetSize() < (NODE_FANOUT - 1) / 2) {         //need to split or redistribution
                SolveUnderflow(i);               //solve overflow
            }

            return tmp;


        } else {

            return DeleteValue(input);
        }


    }
}
