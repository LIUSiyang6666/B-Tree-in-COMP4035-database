import java.util.LinkedList;

public class Node {
    private boolean isLeafNode;                 //whether this is a leaf node or a inner node
    private int fanout = 5;                      // the max of number of pointer in a node
    private LinkedList<Pair> valueList = new LinkedList<>(); // list to contain values, key rid pair

    // leafNode setting
    private Node nextPointer;               // pointer point to the next leafNode
    private Node prePointer;              // pointer point to the previous leafNode

    //inner node setting
    private LinkedList<Node> pointerList = new LinkedList<>();   // list of pointers

    public boolean changeLeafHeader = false;


    public Node(boolean isLeaf) {
        isLeafNode = isLeaf;
        if (isLeaf == true) {
            pointerList = null;
        } else {
            nextPointer = null;
            prePointer = null;
        }
    }

    public boolean getIsLeafNode() {
        return isLeafNode;
    }

    public Node getNextPointer() {
        return nextPointer;
    }

    public Node getPrePointer() {
        return prePointer;
    }

    public int getSize() {
        return valueList.size();
    }

    public void setNextPointer(Node nextPointer) {
        this.nextPointer = nextPointer;
    }

    public void setPrePointer(Node prePointer) {
        this.prePointer = prePointer;
    }

    public LinkedList<Node> getPointerList() {
        return pointerList;
    }

    public LinkedList<Pair> getValueList() {
        return valueList;
    }

    public void printNode() {
        for (Pair i : valueList) {
            System.out.print(i.key + " ");
        }
    }

    public boolean search(int input) {          // search in a node, if exist, return true; otherwise return false
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
                    tmp = pointerList.get(i).search(input);
                    return tmp;
                }
            }

            tmp = pointerList.get(size).search(input);      //the last pointer

            return tmp;

        }
    }

    private void insertValue(int input) {                            // use to insert value into a node

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

    private Integer split() {                            // use to deal with nodes need split

        Node newNode = new Node(isLeafNode);
        int pos = (fanout - 1) / 2;

        for (int i = valueList.size(); i > pos; i--) {
            newNode.valueList.add(valueList.remove(pos));       // insert the latter part of value into new node

            if (!isLeafNode) {
                newNode.pointerList.add(pointerList.remove(pos + 1));    //for inner node, the pointer list also need to be edited
            }
        }

        if (this.nextPointer != null) {
            this.nextPointer.prePointer = newNode;              //update the list of leaf node , also connect inner nodes
            newNode.setNextPointer(this.nextPointer);
        }
        this.nextPointer = newNode;
        newNode.setPrePointer(this);


        if (isLeafNode) {
            return newNode.valueList.get(0).key;              // return the value should be added to the upper node

        } else {
            int tmp = newNode.valueList.get(0).key;             // for the inner node case
            newNode.valueList.remove(0);
            return tmp;
        }

    }

    public Integer insert(int input) {            //insert in a leafNode, if not full,return true; if full return false;
        //search the leaf node to insert

        // if do not need split just return null;
        if (!isLeafNode) {                                   // search inner node

            Integer tmp = -1;
            int size = valueList.size();
            boolean isFinished = false;

            for (int i = 0; i < size; i++) {                                 //pre several pointer
                if (input < valueList.get(i).key) {
                    tmp = pointerList.get(i).insert(input);
                    isFinished = true;
                    break;
                }
            }

            if (!isFinished) {
                tmp = pointerList.get(size).insert(input);      //the last pointer
                isFinished = false;
            }

            //get back from lower nodes, if tmp is null, which means no overflow, return directly;
            // if tmp is not null, means there is overflow to deal with

            if (tmp != null) {

                insertValue(tmp);                       //insert the value that lower nodes pushed up and insert pointers into pointer lists

                if (valueList.size() > fanout - 1) {                // case :need split
                    return split();
                }

            }

        } else {                                              // insert in the leaf node

            insertValue(input);

            if (valueList.size() > fanout - 1) {                // case :need split
                return split();
            }

        }
        return null;

    }

    private boolean deleteValue(int input) {           //delete a value in a straight forward way

        int nowSize = valueList.size();

        for (int i = 0; i < nowSize; i++) {
            if (input == valueList.get(i).key) {
                valueList.remove(i);
                return true;
            }
        }
        return false;

    }

    private void merge(int idx,int otherIdx) {
        Node ufNode = pointerList.get(idx);    //underflowNode
        Node otherNode = pointerList.get(otherIdx);

        if(idx < otherIdx){                 //underflow node is on the left side of the other node

            if(!ufNode.getIsLeafNode()){
                otherNode.valueList.offerFirst(valueList.remove(idx));   //remove the upper node value and put into a lower node
            }else{
                valueList.remove(idx);
            }

            for(int i=(fanout-5)/2;i>=0;i--) {
                otherNode.valueList.offerFirst(ufNode.valueList.remove(0)); //move value
            }

            if(!ufNode.getIsLeafNode()){
                for(int i=(fanout-3)/2;i>=0;i--){
                    otherNode.pointerList.offerFirst(ufNode.pointerList.remove(i)); //move pointer
                }
            }


            if(ufNode.prePointer != null){
                otherNode.prePointer = ufNode.prePointer;   //remove pointer with sibling
                ufNode.prePointer.nextPointer = otherNode;
            }else{
                otherNode.prePointer = null;            // remove previous node

                if(ufNode.getIsLeafNode()){
                    // leafHeader pointer point to otherNode
                    changeLeafHeader = true;
                }

            }


        }else{                              //underflow node is on the left side of the other node

            if(!ufNode.getIsLeafNode()){
                otherNode.valueList.offerLast(valueList.remove(otherIdx));   //remove the upper node value and put into a lower node
            }else{
                valueList.remove(otherIdx);
            }

            for(int i=0;i<(fanout-3)/2;i++) {
                otherNode.valueList.offerLast(ufNode.valueList.remove(0)); //move value
            }

            if(!ufNode.getIsLeafNode()){

                for(int i=0;i<(fanout-1)/2;i++){

                    otherNode.pointerList.offerLast(ufNode.pointerList.remove(0)); //move pointer

                }
            }


            if(ufNode.nextPointer != null){
                otherNode.nextPointer= ufNode.nextPointer;   //remove pointer with sibling
                ufNode.nextPointer.prePointer = otherNode;
            }else {
                otherNode.nextPointer = null;
            }

        }

        pointerList.remove(idx); //remove pointer from parent

        System.gc();

    }

    private void borrowLeaf(int borrowIdx,int lendIdx){
        Node borrower = pointerList.get(borrowIdx);
        Node lender = pointerList.get(lendIdx);

        if(borrowIdx < lendIdx){                                            // borrow from right sibling

            borrower.valueList.offerLast(lender.valueList.remove(0)); //remove the first of right sibling and add to the underflow node

            valueList.remove(borrowIdx);                                    //deal with the part of the node now
            valueList.add(borrowIdx,lender.getValueList().getFirst());

        }else{                                                               // borrow from left sibling

            int size =lender.valueList.size();
            borrower.valueList.offerFirst(lender.valueList.remove(size - 1)); //remove the last of left sibling and add to the underflow node

            valueList.remove(lendIdx);                                    //deal with the part of the node now
            valueList.add(lendIdx,borrower.getValueList().getFirst());       //which is on the left, the valueList use which value
        }

    }

    private void borrowInner(int borrowIdx,int lendIdx){
        Node borrower = pointerList.get(borrowIdx);
        Node lender = pointerList.get(lendIdx);

        if(borrowIdx < lendIdx){                                            // borrow from right sibling

            borrower.valueList.offerLast(valueList.remove(borrowIdx));    //remove the value in current node and add to the underflow node

            borrower.pointerList.offerLast(lender.getPointerList().remove(0)); // move pointer
            //deal with the part of the node now

            valueList.add(borrowIdx,lender.getValueList().remove(0));


        }else{                                                               // borrow from left sibling

            int size =lender.valueList.size();
            borrower.valueList.offerFirst(valueList.remove(lendIdx)); //remove the last of left sibling and add to the underflow node

            borrower.pointerList.offerFirst(lender.getPointerList().remove(size)); // move pointer

            valueList.add(lendIdx,lender.getValueList().remove(size-1));
        }

    }


    private void solveUnderflow(int orgIdx){              //borrow first, if cannot borrow, then merge

        int otherIdx;

        if(orgIdx-1 >= 0){                               // borrow from left sibling

            otherIdx = orgIdx-1;

            if(pointerList.get(otherIdx).getSize() > (fanout-1)/2) {

                if (pointerList.get(orgIdx).getIsLeafNode()) {    //borrow between leaf nodes
                    borrowLeaf(orgIdx, otherIdx);
                } else {                                            //borrow between inner nodes
                    borrowInner(orgIdx, otherIdx);
                }
                return;
            }

        }

        if(orgIdx+1< pointerList.size()){

            otherIdx = orgIdx+1;
            if(pointerList.get(otherIdx).getSize() > (fanout-1)/2) {

                if (pointerList.get(orgIdx).getIsLeafNode()) {    //borrow between leaf nodes
                    borrowLeaf(orgIdx, otherIdx);
                } else {                                            //borrow between inner nodes
                    borrowInner(orgIdx, otherIdx);
                }

                return;
            }

        }

        if(orgIdx-1 >= 0){                               // merge with left sibling

            otherIdx = orgIdx-1;

            merge(orgIdx,otherIdx);

        }else if(orgIdx+1< pointerList.size()){

            otherIdx = orgIdx+1;

            merge(orgIdx,otherIdx);
        }


    }

    public boolean delete(int input) {

        if (!isLeafNode) {                                   // search inner node

            boolean tmp = false;

            int size = valueList.size();
            boolean isFinished = false;
            int i;

            for (i = 0; i < size; i++) {                                 //pre several pointer
                if (input < valueList.get(i).key) {
                    tmp = pointerList.get(i).delete(input);
                    isFinished = true;
                    break;
                }
            }

            if (!isFinished) {
                tmp = pointerList.get(size).delete(input);      //the last pointer
                isFinished = false;
            }

            //get back from lower nodes

            //check whether need to split or redistribution

            if(pointerList.get(i).changeLeafHeader){     //pass the message up
                changeLeafHeader = true;
            }

            if(pointerList.get(i).getSize()< (fanout-1)/2){         //need to split or redistribution
                    solveUnderflow(i);               //solve overflow
            }

            return tmp;


        }else {

            return deleteValue(input);
        }


    }
}
