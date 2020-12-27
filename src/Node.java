import java.util.ArrayList;
import java.util.List;

public class Node {
    ArrayList<Integer> coins = new ArrayList<Integer>();
    int point_difference = 0;
    int player_points = 0; //player MAX
    int opponent_points = 0; //player MIN
    int round = 0; //state after this round, equals level (deepness) of the node
    int index = 0;


    public Node(Node node) {
        coins = new ArrayList<Integer>(node.coins);
        point_difference = node.point_difference;
        round = node.round;
        index = node.index;
    }

    public Node(ArrayList<Integer> coins, int point_difference, int round, int index) {
        this.coins = coins;
        this.point_difference = point_difference;
        this.round = round;
        this.index = index;
    }
    /*
    public Node(int value, int current_round, int prev_point_difference, ArrayList<Integer> current_coins) {
        round = current_round;
        if (round % 2 == 0) {
            point_difference = prev_point_difference + value;
        }
        else {
            point_difference = prev_point_difference - value;
        }
        coins = current_coins;
    }
*/
    //add root
    public Node(ArrayList<Integer> coins) {
        this.coins = coins;
        point_difference = 0;
        round = 0;
        index = 0;
    }
    //add node as child to parent
    public Node(int value, Node parent) {
        round = parent.round + 1;
        if (round % 2 == 0) {
            point_difference = parent.point_difference + value;
        }
        else {
            point_difference = parent.point_difference - value;
        }
        coins = parent.coins;
        //remove 1 of the coins [0] or [last]
    }

    public void print() {
        System.out.println("Coins: " + coins);
        System.out.println("Point difference: " + point_difference);
        System.out.println("Round: " + round);
        System.out.println("Index: " + index + "\n");
    }

    public int get_point_difference() {
        return point_difference;
    }

    public int get_round() {
        return round;
    }

    public int get_index() {
        return index;
    }

    public ArrayList<Integer> get_coins() {
        return coins;
    }

    public int get_far_left() {
        return coins.get(0);
    }

    public int get_far_right() {
        return coins.get(coins.size() - 1);
    }
}
