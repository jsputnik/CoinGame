import java.util.ArrayList;

//last level consists of duplicates
public class Tree {
    private ArrayList<Node> nodes = new ArrayList<Node>();
    private int size = 0;
    private int height = 0;

    //tree only just a root
    public Tree(Node root) {
        nodes.add(new Node(root.get_coins(), root.get_player_points(), root.get_opponent_points(), root.get_round(), 0)); //add root
        size = 1;
        height = 0;
    }

    //insert as child of parent_node and choosing either far left or far right coin
    public void insert(Node parent_node, String left_or_right) {
        //inserting ith element, i = size, nodes[i-1]
        int new_round = parent_node.get_round() + 1;
        long new_player_points = parent_node.get_player_points();
        long new_opponent_points = parent_node.get_opponent_points();
        ArrayList<Integer> new_coins = new ArrayList<Integer>(parent_node.get_coins());

        //if min move
        if (new_round % 2 == 0 && left_or_right.equals("left")) {
            new_opponent_points += parent_node.get_far_left();
            new_coins.remove(0);
        }
        else if (new_round % 2 == 0 && left_or_right.equals("right")) {
            new_opponent_points += parent_node.get_far_right();
            new_coins.remove(new_coins.size() - 1);
        }

        //if max move
        else if (new_round % 2 == 1 && left_or_right.equals("left")) {
            new_player_points += parent_node.get_far_left();
            new_coins.remove(0);
        }
        else {
            new_player_points += parent_node.get_far_right();
            new_coins.remove(new_coins.size() - 1);
        }
        nodes.add(new Node(new_coins, new_player_points, new_opponent_points, new_round, size));
        ++size;
    }

    public void print() {
        for (Node node : nodes) {
            node.print();
        }
        System.out.println("Size: " + size);
        System.out.println("Height: " + height);
    }

    public void build_tree(int deep, ArrayList<Integer> coins) {
        for (int move = 1; move <= deep && move <= coins.size(); ++move) {
            ++height;
            for (int parent = 0; parent < Math.pow(2, move - 1); ++parent) { //and what if the last move?
                int i = size + 1;
                int parent_index = (i - 1) / 2;
                Node parent_node = get_node(parent_index);
                insert(parent_node, "left");
                insert(parent_node, "right");
            }
        }
    }

    public int get_size() {
        return size;
    }

    public int get_height() {
        return height;
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
