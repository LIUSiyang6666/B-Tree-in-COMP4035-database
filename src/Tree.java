import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
public class Tree {
    private Node rootPointer;
    public Node leafHeader;      //Change to private!!!
    private int height;
    private int fanout = 5;

    public Tree(){
        rootPointer = new Node(true);
        leafHeader = rootPointer;
        height = 0;
    }

    public Tree(String address){                //initialize a tree object with a file address
        rootPointer = new Node(true);
        leafHeader = rootPointer;
        height = 0;
        readFile(address);
    }

    public LinkedList search(int input){
        LinkedList<Integer> result = new LinkedList<>();       // can be used to collect range search result
        boolean isIn;
        isIn = rootPointer.search(input);
        System.out.println(input+" :"+isIn);
        return result;

    }
    private void readFile(String address){
        int []array = new int[7];
        String tmp ="";
        String [] splitTmp;
        try{

            File inputFile = new File(address);
            InputStream in = new FileInputStream(inputFile);
            int size = in.available();

            for(int i=0;i<size;i++){

                tmp += (char)in.read()+"";        //store the input stream into a string

            }

            splitTmp = tmp.split(",");

            for(int i=0;i< splitTmp.length;i++){
                insert(Integer.parseInt(splitTmp[i]));        //splite the string and turn value into integer
            }



            in.close();

        }catch (Exception e){
            e.printStackTrace();
        }




    }

    public void insert(int input){

        Integer tmp = rootPointer.insert(input);

        if(tmp != null){                                    //root node need to be split
            Node newRoot = new Node(false);

            newRoot.getValueList().add(new Pair(tmp,0));        //insert the value

            newRoot.getPointerList().add(rootPointer);
            newRoot.getPointerList().add(1,rootPointer.getNextPointer());   //insert pointers

            rootPointer = newRoot;
            height++;
        }

    }
    public boolean delete(int input){
        boolean isSuccess = rootPointer.delete(input);

        if(rootPointer.changeLeafHeader){             //adjust the leafHeader to a correct Node
            leafHeader = rootPointer;

            for(int i=0;i<height;i++){
                leafHeader.changeLeafHeader = false;
                leafHeader = leafHeader.getPointerList().getFirst();
            }
        }

        if(rootPointer.getSize()==0){                       // if the root node become empty

            rootPointer = rootPointer.getPointerList().getFirst(); //change root pointer

            height--;

            System.gc();

        }



        return isSuccess;

    }

    public void printTree(){
        int count = 0;
        Node levelPointer= rootPointer;
        Node pointer = levelPointer;

        for(int i=0;i<height+1;i++){

            //System.out.println("height: "+i);
            //System.out.println();

            while(true){
                System.out.print(count+": ");
                pointer.printNode();
                System.out.println();

                pointer = pointer.getNextPointer();

                count++;

                if(pointer ==null){
                    break;
                }
            }

            if(i!=height){
                levelPointer = levelPointer.getPointerList().getFirst();
                pointer = levelPointer;
            }else{
                pointer = leafHeader;
            }
        }

    }

}
