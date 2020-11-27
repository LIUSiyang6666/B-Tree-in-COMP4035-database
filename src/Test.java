import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws Exception {
        Test test = new Test();
        test.UserInterface(); // start the user interface

/*
        // PLEASE change the address when testing!!!
        Tree BTree = new Tree("C:\\Users\\XiaoYao Li\\Desktop\\data base\\project\\src\\DataFile.txt");
        BTree.printTree();

 */
/*
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
*/

    }

    /**
     * This method is the interactive user command line interface
     */
    public void UserInterface() {
        Scanner in = new Scanner(System.in);

        System.out.println("Welcome to B+Tree Test Program");
        Tree BTree = null;

        while (true) {
            System.out.print("> ");
            String input = in.next();
            switch (input) {
                case "btree":       // build the tree from file path or ask for help
                    String command = in.next();
                    if (command.compareTo("-help") == 0) {
                        System.out.println("Usage: btree [fname]");
                        System.out.println("fname: the name of data file storing the search key values");
                        System.out.println("Usage: insert [num1] [num2] [num3] ... | insert new key values");
                        System.out.println("Usage: delete [num1] [num2] [num3] ... | delete old key values");
                        System.out.println("Usage: search [num1] [num2] | search result between num1 and num2");
                        System.out.println("Usage: stats | show statistics of b+-tree");
                        System.out.println("Usage: quit | leave this program");
                    } else {
                        File file = new File(command);
                        if (file.exists()) {
                            System.out.println("Building an initial B+-Tree..");
                            System.out.println("Launching B+-Tree test program…");
                            BTree = new Tree();
                            BTree.ReadFile(command);
                        } else {
                            System.out.println("File: " + command + " does not exist! Please check the file path.");
                        }
                    }
                    in.nextLine();
                    break;
                case "insert":      // insert [num1] [num2] [num3] ... insert new key values into b+-tree
                    if (BTree == null) {
                        System.out.println("B+-tree is not built yet. You can type btree -help for help");
                    } else {
                        String[] content = in.nextLine().trim().split(" ");
                        for (String s : content) {
                            try {
                                BTree.Insert(Integer.parseInt(s));
                                System.out.println(s + " is inserted successfully");
                            } catch (NumberFormatException e) {
                                System.out.println("The input is invalid!");
                            }

                        }
                    }
                    break;
                case "delete":      // delete [num1] [num2] [num3] ... delete old key values into b+-tree
                    if (BTree == null) {
                        System.out.println("B+-tree is not built yet. You can type btree -help for help");
                    } else {
                        String[] content = in.nextLine().trim().split(" ");
                        for (String s : content) {
                            try {
                                boolean isDeleted = BTree.Delete(Integer.parseInt(s));
                                if (isDeleted) {
                                    System.out.println(s + " is deleted successfully");
                                } else {
                                    System.out.println(s + " is not found");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("The input is invalid!");
                            }


                        }
                    }
                    break;
                case "search":      // search [num1] [num2] search keys values between
                    if (BTree == null) {
                        System.out.println("B+-tree is not built yet. You can type btree -help for help");
                    } else {
                        String content = in.nextLine();
                        String[] temp = content.trim().split(" ");

                        try {
                            int key1 = Integer.parseInt(temp[0]);
                            int key2 = Integer.parseInt(temp[1]);

                            if (key1 > key2) {
                                int tmp = key1;
                                key1 = key2;
                                key2 = tmp;
                            }

                            LinkedList<Integer> result = BTree.Search(key1, key2);
                            System.out.print("Result: ");
                            for (Integer integer : result) {
                                System.out.print(integer + " ");
                            }
                            System.out.println();
                        } catch (NumberFormatException e) {
                            System.out.println("The input is invalid!");
                        }
                    }
                    break;
                case "print":       // print the node of B+-tree
                    if (BTree == null) {
                        System.out.println("B+-tree is not built yet. You can type btree -help for help");
                    } else {
                        BTree.PrintTree();
                    }
                    break;
                case "stats":       // display the statistics of B+-tree
                    if (BTree == null) {
                        System.out.println("B+-tree is not built yet. You can type btree -help for help");
                    } else {
                        BTree.DumpStatistics();
                    }
                    break;
                case "quit":
                    System.out.println("Thanks! Bye bye.");
                    return;
                default:
                    System.out.println("Wrong command! You can type btree -help for help");

            }
        }
    }
}
