import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    ArrayList<Integer> starting_coins = new ArrayList<Integer>(); //starting coin row
    int result
    ArrayList<Node> game_strategy = new ArrayList<>();

    public static void main(String[] args) {
        if (handle_input(args) != 0) {
            System.out.println("");
            return;
        }
    }

    public Game(int n) {
        if (n % 2 == 0) {
            ++n;
        }
        //coins = new ArrayList<Integer>();
        List<Integer> value_list = new ArrayList<Integer>();
        for (int i = 1; i <= n; ++i) {
            value_list.add(i);
        }
        //System.out.println(value_list);
        Random number = new Random();
        for (int i = n; i > 0; --i) {
            int index = number.nextInt(i);
            starting_coins.add(value_list.get(index));
            value_list.remove(index);
            //System.out.println("Index: " + index + ", " + "iterations left: " + i + ", " + value_list);
        }
    }

    public Game(String data) {
        int value = 0;
        for (int i = 0; i < data.length(); ++i) {
            if (Character.isDigit(data.charAt(i))) {
                value = 10 * value + Integer.parseInt(data.valueOf(data.charAt(i)));
            }
            else {
                if (value != 0) {
                    starting_coins.add(value);
                    value = 0;
                }
            }
        }
    }

    public void print() {
        System.out.println(starting_coins);
        System.out.println("Length: " + starting_coins.size());
    }

    public void print_game_strategy() {
        for (int i = 0; i < game_strategy.size(); ++i) {
            game_strategy.get(i).print();
        }
    }

    public static int handle_input(String[] args) {
        System.out.println("Program runs in 3 modes:");
        System.out.println("java -jar CoinGame.jar -m1 <<[input file name] >>[output file name]");
        System.out.println("java -jar CoinGame.jar -m2 -n[problem size] >> [output file name]");
        System.out.println("java -jar CoinGame.jar -m2 -n[problem size] -k[repeats] -step[problem size increase] -r[repeats for each problem size]\n");
        switch (args[0]) {
            case "-m1":
                if (args.length == 3) {
                    System.out.println("Entered mode 1");
                    ArrayList<String> data = read_from_file(args[1]);
                    System.out.println("\nPrinting game: ");
                    Game game = new Game(data.get(0));
                    game.print();
                    System.out.println("\nPrinting tree with just a root: ");
                    Tree tree = new Tree(game.starting_coins);
                    tree.print();
                    System.out.println("\nPrinting tree as array of deep = 4: ");
                    tree.build_tree(4, game.starting_coins);
                    tree.print();
                    game.create_game_strategy(game.MinMax(tree.get_node(0), Turn.MAX, tree), tree);
                    System.out.println("\nPrinting game strategy as nodes: ");
                    game.print_game_strategy();
                    //System.out.println("\nPrinting tree after MinMax: ");
                    //tree.print();
                    write_to_file(args[2], data);
                    break;
                }
                else {
                    System.out.println("Error 2: Invalid number of arguments in -m1");
                    System.out.println("java -jar CoinGame.jar -m1 << [input file name] >> [output file name]");
                    return 2;
                }
            case "-m2":
                System.out.println("Entered mode 2");
                break;
            case "-m3":
                System.out.println("Entered mode 3");
                break;
            default:
                System.out.println("Error 1: Use -m1, -m2 or -m3 as first argument");
                return 1;
        }
        return 0;
    }

    //reads from file into an array of strings
    public static ArrayList<String> read_from_file(String file_name) {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader buffered_reader = new BufferedReader(new FileReader(file_name))) {
            String line = buffered_reader.readLine();
            while (line != null) {
                lines.add(line);
                System.out.println(line);
                line = buffered_reader.readLine();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());;
        }
        return lines;
    }

    //writes an array of strings to file
    public static void write_to_file(String file_name, ArrayList<String> data) {
        try (BufferedWriter buffered_writer = new BufferedWriter(new FileWriter(file_name))) {
            for (int i = 0; i < data.size(); ++i) {
                buffered_writer.write(data.get(i));
                buffered_writer.newLine();
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }

    public boolean is_terminal(Node node) {
        return node.get_round() == starting_coins.size() - 1;
    }

    public static Node get_better_max_move(Node left, Node right) {
        return (left.point_difference > right.point_difference) ? left : right;
    }

    public static Node get_better_min_move(Node left, Node right) {
        return (left.get_point_difference() < right.get_point_difference()) ? left : right;
    }

    public Node MinMax(Node node, Turn turn, Tree tree) {
        System.out.println("in");
        node.print();
        if (is_terminal(node)) {
            return node;
        }
        Node left_child = MinMax(tree.get_left_child(node), turn.next_turn(), tree);
        Node right_child = MinMax(tree.get_right_child(node), turn.next_turn(), tree);
        if (turn == Turn.MAX) {
            //game_strategy.add(get_better_max_move(left_child, right_child));
            return get_better_max_move(left_child, right_child);
            //return Math.max(left_child_point_difference, right_child_point_difference);
        }
        else {
            //game_strategy.add(get_better_min_move(left_child, right_child));
            return get_better_min_move(left_child, right_child);
            //return Math.min(left_child_point_difference, right_child_point_difference);
        }
    }

    public void create_game_strategy(Node node, Tree tree) {
        while (node.get_index() > 0) {
            game_strategy.add(node);
            node = tree.get_parent(node);
        }
        game_strategy.add(node);
        Collections.reverse(game_strategy);
    }
/*
    //MiniMax z odcięciem alfa-beta
    //s – badany węzeł, d - głebokość części budowanego drzewa
    public int MinMax(Node s, int deep, int a, int b, String turn) {
        if (deep == 0) {
            return s.get_point_difference();
        }
        //T – terminal nodes
        if (is_terminal(s)) {
            return s.get_point_difference();
        }
        U = successors(s);
        If max_turn {
            For u in U {
                a = max(v, MinMax(u, d - 1, a, b, false));
                If a >=b return b;
            }
            Return b;
        }
        If min_turn {
            For u in U {
                b = min(v, MinMax(u, d - 1, a, b, true));
                If b <=a return a;
            }
            Return a;
        }
    }
    Int h(s) {
                p = parent(s);
        if s.max_turn
        s.coin_diff_value = p.coin_diff_value + s.coin_value;
        if s.min_turn
        s.coin_diff_value = p.coin_diff_value - s.coin_value;
        return s.coin_diff_value; }
    }
      */



    /*
    //MiniMax z odcięciem alfa-beta
    //s – badany węzeł, d - głebokość części budowanego drzewa
    public int MinMax(Node s, int deep, int a, int b, String maxTurn) {
        if (deep == 0) {
            return h(s);
        }
        for t in T { //T – węzły terminalne, stany po zakończeniu gry
            If s ==t then return h(s);
        }
        U = successors(s);
        If max_turn {
            For u in U {
                a = max(v, MinMax(u, d - 1, a, b, false));
                If a >=b return b;
            }
            Return b;
        }
        If min_turn {
            For u in U {
                b = min(v, MinMax(u, d - 1, a, b, true));
                If b <=a return a;
            }
            Return a;
        }
    }
    Int h(s) {
        p = parent(s);
        if s.max_turn
        s.coin_diff_value = p.coin_diff_value + s.coin_value;
        if s.min_turn
        s.coin_diff_value = p.coin_diff_value - s.coin_value;
        return s.coin_diff_value; }

 */
}
