import javax.swing.plaf.basic.BasicTextAreaUI;
import java.util.LinkedList;
public class Test {
    public static void main(String [] args){

/*
        // PLEASE change the address when testing!!!
        Tree BTree = new Tree("C:\\Users\\XiaoYao Li\\Desktop\\data base\\project\\src\\DataFile.txt");
        BTree.printTree();

 */

        // insert and delete test
        Tree BTree = new Tree();
        BTree.insert(13);
        BTree.insert(17);
        BTree.insert(24);
        BTree.insert(30);

        BTree.insert(2);
        BTree.insert(3);
        BTree.insert(5);
        BTree.insert(7);

        BTree.insert(32);
        BTree.insert(33);

        BTree.insert(25);
        BTree.insert(27);

        BTree.insert(28);

        BTree.insert(34);
        BTree.insert(35);
        BTree.insert(31);

        BTree.insert(44);
        BTree.insert(41);

        BTree.delete(28);   //normal delete

        BTree.delete(34);   //borrow from left leaf nodes

        BTree.delete(32);   //borrow from right leaf nodes

        // BTree.delete(33);   //merge with left leaf nodes

        //BTree.delete(27);   //merge with right leaf nodes


        //borrow from right inner node
        //BTree.delete(13);
        //BTree.delete(3);

        //borrow from right inner node
        BTree.insert(14);
        BTree.insert(15);
        BTree.delete(33);
        BTree.delete(27);
        BTree.delete(30);

        BTree.delete(35);

        //merge inner node with left

        //BTree.delete(17);

        //merge inner node with right

        BTree.delete(2);

        //duplicate key
        //BTree.insert(31);
        //BTree.delete(31);


        BTree.printTree();





    }

}
