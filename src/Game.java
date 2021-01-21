import java.io.*;
import java.util.*;

public class Game {
    private ArrayList<Integer> starting_coins = new ArrayList<Integer>(); //starting coin row
    private long player_points = 0; //player points at the end
    private long opponent_points = 0; //opponent points at the end
    private ArrayList<Node> game_strategy = new ArrayList<>(); //built only for low problem sizes
    private int max_tree_height = 10;

    public static void main(String[] args) {
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("Program runs in 4 modes:");
        System.out.println("Mode 1: java -jar CoinGame.jar -m1 [input file name] [output file name]");
        System.out.println("Mode 2: java -jar CoinGame.jar -m2 -n[problem size] [output file name]");
        System.out.println("Mode 3: java -jar CoinGame.jar -m3 -n[problem size] -k[repeats] -step[problem size increase] " +
                "-r[repeats for each problem size [output file name]");
        System.out.println("Mode 4: java -jar CoinGame.jar -m4 -n[problem size] -gen[generator type] [output file name]\n");

        System.out.println("Mode 1 - reads data from file and writes results to another file");
        System.out.println("Mode 2 - creates a random game based on the [n] size and writes results to another file");
        System.out.println("Mode 3 - creates [k] games starting from game of size [n] and then gradually increasing the size by [step] every" +
                " [r] games and writes results to another file");
        System.out.println("Mode 4 - creates a specific game of [n] size based on the [gen] value (asc or desc) and writes results to another" +
                " file");
        System.out.println("-------------------------------------------------------------------------------------------\n");
        switch (args[0]) {
            case "-m1":
                if (args.length == 3) {
                    System.out.println("Entered mode 1");
                    delete_file(args[2]);
                    ArrayList<String> data = read_from_file(args[1]);
                    for (int i = 0; i < data.size(); ++i) {
                        String datum = data.get(i);
                        System.out.println("Printing game at the start: ");
                        Game game = new Game(datum);
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
                        if (low_problem_size(game.starting_coins.size())) {
                            game.game_strategy.add(last_deciding_move);
                        }
                        //algorithm loop
                        System.out.println("Computing game " + (i + 1) + "...");
                        for (int j = 0; j < number_of_trees; ++j) {
                            Tree tree = new Tree(last_deciding_move);
                            tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                            //MinMaxAB
                            last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                    last_deciding_move.get_turn().next_turn(), tree);
                            if (low_problem_size(game.starting_coins.size())) {
                                game.update_game_strategy(last_deciding_move, tree);
                            }
                        }
                        game.set_results(last_deciding_move);

                        String result = "";
                        result = game.format_results(result);
                        write_to_file(args[2], result);

                        game.print_results();
                        System.out.println("-------------------------------------------------------------------------------------------\n");
                    }
                }
                else {
                    System.out.println("Error 2: Invalid number of arguments in -m1");
                    System.out.println("java -jar CoinGame.jar -m1 [input file name] [output file name]");
                    System.out.println("Mode 1 - reads data from file and writes results to another file\n");
                }
                break;
            case "-m2":
                if (args.length == 3) { //check if -n5 not -rn5 etc are right
                    System.out.println("Entered mode 2");
                    delete_file(args[2]);
                    int n = Integer.parseInt(args[1].substring(2));
                    System.out.println("Printing game at the start: ");
                    Game game = new Game(n);
                    game.print();

                    int number_of_trees = game.starting_coins.size() / game.max_tree_height;
                    if (game.starting_coins.size() % game.max_tree_height != 0) {
                        ++number_of_trees;
                    }

                    Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
                    Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
                    Node last_deciding_move = new Node(game.starting_coins, 0, 0, 0, 0);
                    if (low_problem_size(n)) {
                        game.game_strategy.add(last_deciding_move);
                    }
                    System.out.println("Computing game...");
                    for (int j = 0; j < number_of_trees; ++j) {
                        Tree tree = new Tree(last_deciding_move);
                        tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                        //MinMaxAB
                        last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                last_deciding_move.get_turn().next_turn(), tree);
                        if (low_problem_size(n)) {
                            game.update_game_strategy(last_deciding_move, tree);
                        }
                    }
                    game.set_results(last_deciding_move);

                    String result = "";
                    result = game.format_results(result);
                    write_to_file(args[2], result);

                    game.print_results();
                    System.out.println("-------------------------------------------------------------------------------------------\n");
                }
                else {
                    System.out.println("Error 3: Invalid number of arguments in -m2");
                    System.out.println("java -jar CoinGame.jar -m2 -n[problem size] [output file name]");
                    System.out.println("Mode 2 - creates a random game based on the [n] size and writes results to another file\n");
                }
                break;
            case "-m3":
                if (args.length == 6) {
                    System.out.println("Entered mode 3");
                    warm_up();
                    System.out.println("Finished warming up");
                    delete_file(args[5]);

                    int n = Integer.parseInt(args[1].substring(2));
                    int k = Integer.parseInt(args[2].substring(2));
                    int step = Integer.parseInt(args[3].substring(5));
                    int r = Integer.parseInt(args[4].substring(2));
                    for (int i = 1; i <= k; ++i) {
                        System.out.println("Printing game " + i + " at the start: ");
                        Game game = new Game(n);
                        game.print();

                        int number_of_trees = game.starting_coins.size() / game.max_tree_height;
                        if (game.starting_coins.size() % game.max_tree_height != 0) {
                            ++number_of_trees;
                        }

                        Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
                        Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
                        Node last_deciding_move = new Node(game.starting_coins, 0, 0, 0, 0);
                        if (low_problem_size(game.starting_coins.size())) {
                            game.game_strategy.add(last_deciding_move);
                        }
                        System.out.println("Computing game " + i + "...");
                        long start_time = System.nanoTime();
                        for (int j = 0; j < number_of_trees; ++j) {
                            Tree tree = new Tree(last_deciding_move);
                            tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                            //MinMaxAB
                            last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                    last_deciding_move.get_turn().next_turn(), tree);
                            if (low_problem_size(game.starting_coins.size())) {
                                game.update_game_strategy(last_deciding_move, tree);
                            }
                        }
                        long estimated_time = (System.nanoTime() - start_time) / 1000000;
                        game.set_results(last_deciding_move);

                        String result = "";
                        result = game.format_results(result);
                        result += "Elapsed time: " + estimated_time + "ms\n";
                        write_to_file(args[5], result);

                        game.print_results();
                        System.out.println("Elapsed time: " + estimated_time + "ms");
                        System.out.println("-------------------------------------------------------------------------------------------\n");
                        if (i % r == 0) {
                            n += step;
                        }
                    }
                }
                else {
                    System.out.println("Error 4: Invalid number of arguments in -m3");
                    System.out.println("java -jar CoinGame.jar -m3 -n[problem size] -k[repeats] -step[problem size increase]" +
                            " -r[repeats for each problem size] [output file name]");
                    System.out.println("Mode 3 - creates [k] games starting from game of size [n] and then gradually increasing the size by [step] every" +
                            " [r] games and writes results to another file\n");
                }
                break;
            case "-m4":
                if (args.length == 4) {
                    System.out.println("Entered mode 4");
                    warm_up();
                    System.out.println("Finished warming up");
                    delete_file(args[3]);

                    int n = Integer.parseInt(args[1].substring(2));
                    String generator_type = args[2].substring(4);

                    System.out.println("Printing game at the start: ");
                    Game game = new Game(n, generator_type);
                    game.print();

                    int number_of_trees = game.starting_coins.size() / game.max_tree_height;
                    if (game.starting_coins.size() % game.max_tree_height != 0) {
                        ++number_of_trees;
                    }
                    Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
                    Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
                    Node last_deciding_move = new Node(game.starting_coins, 0, 0, 0, 0);
                    if (low_problem_size(game.starting_coins.size())) {
                        game.game_strategy.add(last_deciding_move);
                    }
                    System.out.println("Computing game...");
                    long start_time = System.nanoTime();
                    for (int j = 0; j < number_of_trees; ++j) {
                        Tree tree = new Tree(last_deciding_move);
                        tree.build_tree(game.max_tree_height, last_deciding_move.get_coins());
                        //MinMaxAB
                        last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                                last_deciding_move.get_turn().next_turn(), tree);
                        if (low_problem_size(game.starting_coins.size())) {
                            game.update_game_strategy(last_deciding_move, tree);
                        }
                    }
                    long estimated_time = (System.nanoTime() - start_time) / 1000000;
                    game.set_results(last_deciding_move);

                    String result = "";
                    result = game.format_results(result);
                    result += "Elapsed time: " + estimated_time + "ms\n";
                    write_to_file(args[3], result);

                    game.print_results();
                    System.out.println("Elapsed time: " + estimated_time + "ms");
                    System.out.println("-------------------------------------------------------------------------------------------\n");
                }
                else {
                    System.out.println("Error 5: Invalid number of arguments in -m4");
                    System.out.println("java -jar CoinGame.jar -m4 -n[problem size] -gen[generator type] [output file name]");
                    System.out.println("Mode 4 - creates a specific game of [n] size based on the [gen] value (asc or desc) and writes results to another" +
                            " file\n");
                }
                break;
            default:
                System.out.println("Error 1: Use -m1, -m2, -m3 or -m4 as first argument for help for each mode");
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

    //create a specific case
    public Game(int n, String mode) {
        if (n % 2 == 0) {
            ++n;
        }
        //starting_coins in ascending order, hard case
        if (mode.equals("asc")) {
            for (int i = 1; i <= n; ++i) {
                starting_coins.add(i);
            }
        }
        //starting_coins in descending order, easy case
        else if (mode.equals("desc")) {
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
    }

    public void print_game_strategy() {
        for (Node node : game_strategy) {
            System.out.println(node.get_coins());
        }
    }

    public void print_results() {
        System.out.println("\nResults:");
        System.out.println("Length: " + starting_coins.size());
        System.out.println("Player points: " + player_points);
        System.out.println("Opponent points: " + opponent_points);
        if (low_problem_size(starting_coins.size())) {
            print_game_strategy();
        }
    }

    //reads from file into an array of strings
    public static ArrayList<String> read_from_file(String file_name) {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader buffered_reader = new BufferedReader(new FileReader(file_name))) {
            String line = buffered_reader.readLine();
            while (line != null) {
                lines.add(line);
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
            System.out.println("Successfully deleted the file: " + file.getName());
        }
        else {
            System.out.println("Couldn't delete the file: " + file.getName());
        }
    }

    //if the round is equal to the game size, it's the last move in the game
    public boolean is_terminal(Node node) {
        return node.get_round() == starting_coins.size();
    }

    //returns the better of the 2 possible moves for max
    public static Node get_better_max_move(Node left, Node right) {
        return (left.get_point_difference() > right.get_point_difference()) ? left : right;
    }

    //returns the better of the 2 possible moves for min
    public static Node get_better_min_move(Node left, Node right) {
        return (left.get_point_difference() < right.get_point_difference()) ? left : right;
    }

    //updates game strategy, called to find the optimal route in the given tree
    public void update_game_strategy(Node node, Tree tree) {
        int index_to_add = game_strategy.size();
        while (node.get_index() > 0) {
            game_strategy.add(index_to_add, node);
            node = tree.get_parent(node);
        }
    }

    //used to indicate when to build game strategy and when to save starting coin row to the file
    public static boolean low_problem_size(int n) {
        return n < 50;
    }

    //updates results at the end of the game
    public void set_results(Node node) {
        player_points = node.get_player_points();
        opponent_points = node.get_opponent_points();
    }

    //creates a string to be later saved into the file
    public String format_results(String result) {
        StringBuilder result_builder = new StringBuilder("Problem size n = " + starting_coins.size());
        if (low_problem_size(starting_coins.size())) {
            Object[] starting_coins_array = starting_coins.toArray();
            result_builder.append("\nStarting coin row:\n").append(Arrays.toString(starting_coins_array));
        }

        result_builder.append("\nResults at the end:\nPlayer points: ").append(player_points);
        result_builder.append("\nOpponent points: ").append(opponent_points).append("\n");
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

    //full MinMax, currently not used mainly because it doesn't work for problems of size bigger than 20
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

    //MiniMax with alpha-beta cut, the algorithm used
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

    static public void warm_up() {
        Node a = new Node(null, 0, Integer.MAX_VALUE, -1, -1);
        Node b = new Node(null, Integer.MAX_VALUE, 0, -1, -1);
        for (int i = 0; i < 100000; ++i) {
            Game game = new Game(9);
            Tree tree = new Tree(new Node(game.starting_coins, 0, 0, 0, 0));
            tree.build_tree(game.max_tree_height, game.starting_coins);
            Node last_deciding_move = game.MinMaxAB(tree.get_node(0), tree.get_height(), a, b,
                        Turn.MAX, tree);
            if (low_problem_size(game.starting_coins.size())) {
                game.update_game_strategy(last_deciding_move, tree);
            }
        }
    }
}
