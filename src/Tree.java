import java.lang.reflect.Array;
import java.util.ArrayList;

public class Tree {
    ArrayList<Node> nodes = new ArrayList<Node>();
    int size = 0;
    int height = 0;

    //tree only with root having starting coin row
    public Tree(ArrayList<Integer> coins) {
        nodes.add(new Node(coins)); //add root
        size = 1;
        height = 0;
    }

    //insert as child of parent_node and choosing either far left or far right coin
    public void insert(Node parent_node, String left_or_right) {
        //inserting ith element, i = size, nodes[i-1]
        //Node parent = nodes.get(size - 1);
        //ArrayList<Integer> coins, int point_difference, int round
        //System.out.println("\nEntered insert: ");
        int new_round = parent_node.get_round() + 1;
        //System.out.println("\nnew_round: " + new_round);
        int new_point_difference = 0;
        ArrayList<Integer> new_coins = new ArrayList<Integer>(parent_node.get_coins());
        if (new_round % 2 == 0 && left_or_right == "left") {
            new_point_difference = parent_node.get_point_difference() - parent_node.get_far_left();
            new_coins.remove(0);
        }
        else if (new_round % 2 == 0 && left_or_right == "right") {
            new_point_difference = parent_node.get_point_difference() - parent_node.get_far_right();
            new_coins.remove(new_coins.size() - 1);
        }
        else if (new_round % 2 == 1 && left_or_right == "left") {
            new_point_difference = parent_node.get_point_difference() + parent_node.get_far_left();
            new_coins.remove(0);
        }
        else {
            new_point_difference = parent_node.get_point_difference() + parent_node.get_far_right();
            new_coins.remove(new_coins.size() - 1);
        }
        //System.out.println("\nnew_point_difference: " + new_point_difference);
        //System.out.println("\nnew_coins: " + new_coins);
        nodes.add(new Node(new_coins, new_point_difference, new_round, size));
        ++size;
    }

    public void insert(int value, Node parent) {
        //inserting ith element, i = size, nodes[i-1]
        //Node parent = nodes.get(size - 1);
        nodes.add(new Node(value, parent));
        ++size;
    }

    public void insert(int value, int round, int prev_point_difference, ArrayList<Integer> coins) {
        //inserting ith element, i = size, nodes[i-1]
        Node parent = nodes.get(size - 1);
        nodes.add(new Node(value, parent));
        ++size;
    }

    public void print() {
        for (int i = 0; i < nodes.size(); ++i) {
            nodes.get(i).print();
        }
        System.out.println("Size: " + size);
        System.out.println("Height: " + height);
    }

    public void build_tree(int deep, ArrayList<Integer> coins) {
        if (size == 0) {
            insert(0, 0, 0, coins);
            ++size;
        }
        for (int move = 1; move <= deep && move < coins.size(); ++move) {
            ++height;
            for (int parent = 0; parent < Math.pow(2, move - 1); ++parent) { //and what if the last move?
                int i = size + 1;
                int parent_index = (i - 1) / 2;
                Node parent_node = get_node(parent_index);
                //insert(parent_node.get_far_left(), parent_node);
                //insert(parent_node.get_far_right(), parent_node);
                insert(parent_node, "left");
                insert(parent_node, "right");
            }
        }
    }

    public Node get_node(int index) {
        return nodes.get(index);
    }

    public Node get_left_child(Node node) {
        return nodes.get(2 * node.get_index() + 1);
    }

    public Node get_right_child(Node node) {
        return nodes.get(2 * node.get_index() + 2);
    }

    public Node get_parent(Node node) {
        return nodes.get((node.get_index() - 1) / 2);
    }
}
