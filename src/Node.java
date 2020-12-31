import java.util.ArrayList;

public class Node {
    private ArrayList<Integer> coins = new ArrayList<Integer>();
    private long point_difference = 0;
    private long player_points = 0; //player MAX
    private long opponent_points = 0; //player MIN
    private int round = 0; //state after this round, equals level (deepness) of the node
    private int index = 0;

    public Node(ArrayList<Integer> coins, long player_points, long opponent_points, int round, int index) {
        this.coins = coins;
        this.player_points = player_points;
        this.opponent_points = opponent_points;
        this.point_difference = player_points - opponent_points;
        this.round = round;
        this.index = index;
    }

    public void print() {
        System.out.println("Coins: " + coins);
        System.out.println("Player points: " + player_points);
        System.out.println("Opponent points: " + opponent_points);
        System.out.println("Point difference: " + point_difference);
        System.out.println("Round: " + round);
        System.out.println("Index: " + index + "\n");
    }

    public long get_point_difference() {
        return point_difference;
    }

    public int get_round() {
        return round;
    }

    public int get_index() {
        return index;
    }

    public long get_player_points() {
        return player_points;
    }

    public long get_opponent_points() {
        return opponent_points;
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

    public Turn get_turn() {
        return (round % 2 == 1) ? Turn.MAX : Turn.MIN;
    }
}
