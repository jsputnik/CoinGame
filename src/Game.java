import java.io.*;
import java.util.*;

public class Game {
    private ArrayList<Integer> starting_coins = new ArrayList<Integer>(); //starting coin row
    private long player_points = 0; //player points at the end
    private long opponent_points = 0; //opponent points at the end
    private ArrayList<Node> game_strategy = new ArrayList<>();
    private int max_tree_height = 5;

    public static void main(String[] args) {
        if (handle_input(args) != 0) {
            System.out.println("");
        }
    }

    //creates a random game of n size, if n is even makes it bigger by 1
    public Game(int n) {
        //if n is even, make it odd
        if (n % 2 == 0) {
            ++n;
        }
        ArrayList<Integer> value_list = new ArrayList<Integer>();
        for (int i = 1; i <= n; ++i) {
            value_list.add(i);
        }
        Random number = new Random();
        for (int i = n; i > 0; --i) {
            int index = number.nextInt(i);
            starting_coins.add(value_list.get(index));
            value_list.remove(index);
        }
    }

    public Game(int n, String mode) {
        if (n % 2 == 0) {
            ++n;
        }
        //starting_coins in ascending order, hard case
        if (mode.equals("1")) {
            for (int i = 1; i <= n; ++i) {
                starting_coins.add(i);
            }
        }
        //starting_coins in descending order, easy case
        else if (mode.equals("2")) {
            for (int i = n; i > 0; --i) {
                starting_coins.add(i);
            }
        }
    }

    //creates a game based on string data
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
        System.out.println("Player points: " + player_points);
        System.out.println("Opponent points: " + opponent_points);
        //print_game_strategy();
    }

    public void print_game_strategy() {
        for (int i = 0; i < game_strategy.size(); ++i) {
            game_strategy.get(i).print();
        }
    }

    public static int handle_input(String[] args) {
        System.out.println("Program runs in 3 modes:");
        System.out.println("java -jar CoinGame.jar -m1 [input file name] [output file name]");
        System.out.println("java -jar CoinGame.jar -m2 -n[problem size] [output file name]");
        System.out.println("java -jar CoinGame.jar -m2 -n[problem size] -k[repeats] -step[problem size increase] " +
                "-r[repeats for each problem size [output file name]\n");
        switch (args[0]) {
            case "-m1":
                if (args.length == 3) {
                    System.out.println("Entered mode 1");
                    delete_file(args[2]);
                    ArrayList<String> data = read_from_file(args[1]);
                    //ArrayList<String> output = new ArrayList<String>(); //change
                    for (int i = 0; i < data.size(); ++i) {
                        System.out.println("\nPrinting game at the start: ");
                        Game game = new Game(data.get(i));
                        game.print();
                        //calculating number of the trees to be created
                        int number_of_trees = game.starting_coins.size() / game.max_tree_height;
                        if (game.starting_coins.size() % game.max_tree_height != 0) {
                            ++number_of_trees;
                        }
                        //initialising
                        Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
                        Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
                        Node last_deciding_move = new Node(game.starting_coins, 0, 0, 0, 0);
                        if (should_create_game_strategy(game.starting_coins.size())) {
                            game.game_strategy.add(last_deciding_move);
                        }
                        //algorithm loop
                        for (int j = 0; j < number_of_trees; ++j) {
                            Tree tree = new Tree(last_deciding_move);
                            tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                            //MinMaxAB
                            last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                    last_deciding_move.get_turn().next_turn(), tree);
                            if (should_create_game_strategy(game.starting_coins.size())) {
                                game.update_game_strategy(last_deciding_move, tree);
                            }
                        }
                        game.set_results(last_deciding_move);

                        String result = "";
                        result = game.format_results(result);
                        write_to_file(args[2], result);
                        //output.add(result);
                    }
                    //write_to_file(args[2], output);
                    break;
                }
                else {
                    System.out.println("Error 2: Invalid number of arguments in -m1");
                    System.out.println("java -jar CoinGame.jar -m1 [input file name] [output file name]");
                    return 2;
                }
            case "-m2":
                if (args.length == 3) { //check if -n5 not -rn5 etc are right
                    System.out.println("Entered mode 2");
                    delete_file(args[2]);
                    int n = Integer.parseInt(args[1].substring(2));
                    System.out.println("\nPrinting game at the start: ");
                    Game game = new Game(n);
                    game.print();

                    int number_of_trees = game.starting_coins.size() / game.max_tree_height;
                    if (game.starting_coins.size() % game.max_tree_height != 0) {
                        ++number_of_trees;
                    }

                    Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
                    Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
                    Node last_deciding_move = new Node(game.starting_coins, 0, 0, 0, 0);
                    if (should_create_game_strategy(n)) {
                        game.game_strategy.add(last_deciding_move);
                    }
                    for (int j = 0; j < number_of_trees; ++j) {
                        Tree tree = new Tree(last_deciding_move);
                        tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                        //MinMaxAB
                        last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                last_deciding_move.get_turn().next_turn(), tree);
                        if (should_create_game_strategy(n)) {
                            game.update_game_strategy(last_deciding_move, tree);
                        }
                    }
                    game.set_results(last_deciding_move);

                    String result = "";
                    //result = game.format_results_as_points(result);
                    result = game.format_results(result);
                    System.out.println(result);
                    //ArrayList<String> output = new ArrayList<String>();
                    //output.add(result);
                    System.out.println("\nPrint game:");
                    game.print();
                    write_to_file(args[2], result);
                }
                else {
                    System.out.println("Error 3: Invalid number of arguments in -m2");
                    System.out.println("java -jar CoinGame.jar -m2 -n[problem size] [output file name]");
                    return 3;
                }
                break;
            case "-m3":
                if (args.length == 6) {
                    System.out.println("Entered mode 3");
                    delete_file(args[5]);

                    int n = Integer.parseInt(args[1].substring(2));
                    int k = Integer.parseInt(args[2].substring(2));
                    int step = Integer.parseInt(args[3].substring(5));
                    int r = Integer.parseInt(args[4].substring(2));
                    for (int i = 1; i <= k; ++i) {
                        System.out.println("\nPrinting game at the start: ");
                        Game game = new Game(n);
                        game.print();

                        int number_of_trees = game.starting_coins.size() / game.max_tree_height;
                        if (game.starting_coins.size() % game.max_tree_height != 0) {
                            ++number_of_trees;
                        }

                        Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
                        Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
                        Node last_deciding_move = new Node(game.starting_coins, 0, 0, 0, 0);
                        if (should_create_game_strategy(game.starting_coins.size())) {
                            game.game_strategy.add(last_deciding_move);
                        }
                        for (int j = 0; j < number_of_trees; ++j) {
                            Tree tree = new Tree(last_deciding_move);
                            tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                            //MinMaxAB
                            last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                    last_deciding_move.get_turn().next_turn(), tree);
                            if (should_create_game_strategy(game.starting_coins.size())) {
                                game.update_game_strategy(last_deciding_move, tree);
                            }
                        }
                        game.set_results(last_deciding_move);
                        game.print();

                        String result = "";
                        result = game.format_results(result);
                        write_to_file(args[5], result);
                        //output.add(result);
                        if (i % r == 0) {
                            n += step;
                        }
                    }
                    //write_to_file(args[5], output);
                }
                else {
                    System.out.println("Error 4: Invalid number of arguments in -m3");
                    System.out.println("java -jar CoinGame.jar -m3 -n[problem size] -k[repeats] -step[problem size increase]" +
                            " -r[repeats for each problem size] [output file name]\n");
                    return 4;
                }
                break;
            default:
                System.out.println("Error 1: Use -m1, -m2 or -m3 as first argument");
                Game game1 = new Game(5, "1");
                Game game2 = new Game(5, "2");
                game1.print();
                game2.print();
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

    //writes a string to file
    public static void write_to_file(String file_name, String data) {
        try (BufferedWriter buffered_writer = new BufferedWriter(new FileWriter(file_name, true))) {
            buffered_writer.write(data);
            buffered_writer.newLine();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }

    public static void delete_file(String file_name) {
        File file = new File(file_name);
        if (file.delete()) {
            System.out.print("Successfully deleted the file: " + file.getName());
        }
        else {
            System.out.print("Couldn't delete the file: " + file.getName());
        }
    }

    public boolean is_terminal(Node node) {
        return node.get_round() == starting_coins.size();
    }

    public static Node get_better_max_move(Node left, Node right) {
        return (left.get_point_difference() > right.get_point_difference()) ? left : right;
    }

    public static Node get_better_min_move(Node left, Node right) {
        return (left.get_point_difference() < right.get_point_difference()) ? left : right;
    }

    public void update_game_strategy(Node node, Tree tree) {
        int index_to_add = game_strategy.size();
        while (node.get_index() > 0) {
            game_strategy.add(index_to_add, node);
            node = tree.get_parent(node);
        }
    }

    public static boolean should_create_game_strategy(int n) {
        return n < 50;
    }

    public void set_results(Node node) {
        player_points = node.get_player_points();
        opponent_points = node.get_opponent_points();
    }

    public String format_results(String result) {
        Object[] starting_coins_array = starting_coins.toArray();
        StringBuilder result_builder = new StringBuilder("Starting coin row:\n" + Arrays.toString(starting_coins_array) +
                "\nResults at the end:\nPlayer points: " + player_points + "\nOpponent points: " + opponent_points + "\n");
        if (game_strategy.size() > 0) {
            result_builder.append("Game strategy:\n");
        }
        for (Node node : game_strategy) {
            Object[] coins_array = node.get_coins().toArray();
            result_builder.append(Arrays.toString(coins_array)).append("\n");
        }
        result = result_builder.toString();
        return result;
    }

    //full MinMax
    public Node MinMax(Node node, Turn turn, Tree tree) {
        if (is_terminal(node)) {
            return node;
        }
        Node left_child = MinMax(tree.get_left_child(node), turn.next_turn(), tree);
        Node right_child = MinMax(tree.get_right_child(node), turn.next_turn(), tree);
        if (turn == Turn.MAX) {
            return get_better_max_move(left_child, right_child);
        }
        else {
            return get_better_min_move(left_child, right_child);
        }
    }

    //MiniMax with alpha-beta cut
    public Node MinMaxAB(Node node, int deep, Node a, Node b, Turn turn, Tree tree) {
        if (deep == 0) {
            return node;
        }
        if (is_terminal(node)) {
            return node;
        }
        ArrayList<Node> successors = new ArrayList<Node>();
        successors.add(tree.get_left_child(node));
        successors.add(tree.get_right_child(node));
        if (turn == Turn.MAX) {
            for (Node successor : successors) {
                a = get_better_max_move(a, MinMaxAB(successor, deep - 1, a, b, turn.next_turn(), tree));
                if (a.get_point_difference() >= b.get_point_difference()) {
                    return b;
                }
            }
            return a;
        }
        else {
            for (Node successor : successors) {
                b = get_better_min_move(b, MinMaxAB(successor, deep - 1, a, b, turn.next_turn(), tree));
                if (a.get_point_difference() >= b.get_point_difference()) {
                    return a;
                }
            }
            return b;
        }
    }
}
