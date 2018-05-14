package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




public class Game {
    transient TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WORLD_WIDTH = 70;
    public static final int WORLD_HEIGHT = 40;

    static String name = "";
    static String nowString = "";
    /**
     *
     * Method used for playing a fresh game.
     * The game should start from the main menu.
     */
    public void newGame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 5), "Enter a character " +
                "name and 'z' when you're done");
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char k = StdDraw.nextKeyTyped();
            if (k == 'Z' || k == 'z') {
                break;
            } /*else if (!Character.isDigit(k)) {
                continue;
            }*/ else {
                name += Character.toString(k);
                StdDraw.clear(Color.black);
                StdDraw.text(WORLD_WIDTH / 2,
                        WORLD_HEIGHT / 2, name);
                World.name = name;
            }
        }

        StdDraw.clear(Color.BLACK);
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 5),
                "Enter Seed: ");

        String seed = "";

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char k = StdDraw.nextKeyTyped();
            if (k == 'S' || k == 's') {
                break;
            } else if (!Character.isDigit(k)) {
                continue;
            } else {
                seed += Character.toString(k);
                StdDraw.clear(Color.black);
                StdDraw.text(WORLD_WIDTH / 2,
                        WORLD_HEIGHT / 2, seed);
            }
        }

        long seed1 = Long.valueOf(seed);
        World test = new World(seed1);
        TETile[][] finalWorldFrame = test.generate();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        ter.renderFrame(finalWorldFrame);

        //playing game
        String quit = "";
        while (true) {
            int mouseX = (int) (StdDraw.mouseX());
            int mouseY = (int) (StdDraw.mouseY());
            if (mouseX > 0 && mouseX < WORLD_WIDTH && mouseY > 0
                    && mouseY < WORLD_HEIGHT) {
                StdDraw.clear();
                ter.renderFrame(finalWorldFrame);
                String message = test.world[mouseX][mouseY].description();
                StdDraw.setPenColor(Color.WHITE);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                nowString =  dtf.format(now);

                StdDraw.text(10, 2, "Date and Time: " + test.date);

                if (! name.equals(null) && !name.equals("")) {
                    StdDraw.text(WORLD_WIDTH - 10, WORLD_HEIGHT - 2, name + " is playing");
                }

                if (message.equals("floor")) {
                    StdDraw.textLeft(3, WORLD_HEIGHT - 1, "Why did " +
                            "the guitarist get fired from being a carpenter");
                    StdDraw.textLeft(0, WORLD_HEIGHT - 3, "He was shredding" +
                            " the floor");
                }
                else if (message.equals("wall")) {
                    StdDraw.textLeft(3, WORLD_HEIGHT - 1, "Why did " +
                            "the blonde climb over a glass wall?");
                    StdDraw.textLeft(0, WORLD_HEIGHT - 3, "To see what" +
                            " was on the other side.");
                }
                else if (message.equals("nothing")) {
                    StdDraw.textLeft(3, WORLD_HEIGHT - 1, "What is " +
                            "Beethoven doing right now? ");
                    StdDraw.textLeft(0, WORLD_HEIGHT - 3, "Nothing -" +
                            " he's dead.");
                }

                StdDraw.show();
            }

            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char input = StdDraw.nextKeyTyped();
            quit += String.valueOf(input);
            if (quit.contains(":q") || quit.contains(":Q")) {
                saveWorld(test);
                System.exit(0);
                break;
            }
            test.movePlayer(input);
            ter.renderFrame(finalWorldFrame);
        }
    }

    public void playWithKeyboard() {
        // generating intro screen
        StdDraw.setCanvasSize(WORLD_WIDTH * 16,
                WORLD_HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WORLD_WIDTH);
        StdDraw.setYscale(0, WORLD_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 5),
                "CS61B: THE GAME");
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 20),
                "New Game (N)");
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 23),
                "Load Game (L)");
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 26),
                "Quit (Q)");
        StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 29), "Background (B)");


        // generating world
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            if (key == 'n' || key == 'N') {
                newGame();
                break;

            } else if (key == 'l' || key == 'L') {
                World test = loadWorld();
                Date info = new Date();
                TETile[][] finalWorldFrame = test.world;
                ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
                ter.renderFrame(finalWorldFrame);

                if (test == null) {
                    System.exit(0);
                } else {
                    String quit = "";
                    while (true) {
                        int mouseX = (int) (StdDraw.mouseX());
                        int mouseY = (int) (StdDraw.mouseY());
                        if (mouseX > 0 && mouseX < WORLD_WIDTH && mouseY > 0
                                && mouseY < WORLD_HEIGHT) {
                            StdDraw.clear();
                            ter.renderFrame(finalWorldFrame);
                            /*DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            nowString =  dtf.format(now);*/

                            String message = test.world[mouseX][mouseY].description();
                            StdDraw.setPenColor(Color.WHITE);
                            if (message.equals("floor")) {
                                StdDraw.textLeft(0, WORLD_HEIGHT - 1, "Why did " +
                                        "the guitarist get fired from being a carpenter");
                                StdDraw.textLeft(0, WORLD_HEIGHT - 3, "He was shredding" +
                                        " the FLOOR");
                            }
                            else if (message.equals("wall")) {
                                StdDraw.textLeft(0, WORLD_HEIGHT - 1, "Why did " +
                                        "the blonde climb over a glass WALL?");
                                StdDraw.textLeft(0, WORLD_HEIGHT - 3, "To see what" +
                                        " was on the other side.");
                            }
                            else if (message.equals("nothing")) {
                                StdDraw.textLeft(0, WORLD_HEIGHT - 1, "What is " +
                                        "Beethoven doing right now? ");
                                StdDraw.textLeft(0, WORLD_HEIGHT - 3, "NOTHING -" +
                                        " he's dead.");
                            }

                            StdDraw.text(10, 2, "Date and Time: " + test.date);

                            if (!name.equals(null) && !name.equals("")){
                                StdDraw.text(WORLD_WIDTH - 10, WORLD_HEIGHT - 2, name + " is playing");
                            }
                            StdDraw.show();



                        }


                        if (!StdDraw.hasNextKeyTyped()) {
                            continue;
                        }

                        char input = StdDraw.nextKeyTyped();
                        quit += String.valueOf(input);
                        if (quit.contains(":q") || quit.contains(":Q")) {
                            saveWorld(test);
                            System.exit(0);
                            break;
                        }
                        test.movePlayer(input);
                        Font font1 = new Font("Monaco", Font.BOLD, TERenderer.TILE_SIZE - 2);
                        StdDraw.setFont(font1);
                        ter.renderFrame(finalWorldFrame);


                    }
                }
                break;
            } else if (key == 'q' || key == 'Q') {
                System.exit(0);
                break;
            } else if (key == 'b' || key == 'B') {
                StdDraw.clear(Color.BLACK);
                Font font2 = new Font("Monaco", Font.BOLD, 16);
                StdDraw.setFont(font2);
                StdDraw.setPenColor(Color.white);
                StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 10),
                        "You're preparing for the zombie apocalypse");
                StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 12),
                        "and need to scope out this building ");
                StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 14),
                        "before bringing your friends to it ");
                StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 16),
                        "to use it as a safehouse. Check all the rooms ");
                StdDraw.text((WORLD_WIDTH / 2), (WORLD_HEIGHT - 18),
                        "and hallways and make sure there aren't any zombies! ");
            }
        }

    }


    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {


        TETile[][] finalWorldFrame;
        World test;

        if (input.charAt(0) == 'L' || input.charAt(0) == 'l') {
            String moves = input.substring(1);
            test = loadWorld();
            finalWorldFrame = test.world;

            if (test == null) {
                return null;
            }

            for (int i = 0; i < moves.length(); i++) {
                if (moves.charAt(i) == ':' && (moves.charAt(i + 1) == 'q'
                        || moves.charAt(i + 1) == 'Q')) {
                    saveWorld(test);
                    break;
                }
                System.out.print(moves.charAt(i));
                test.movePlayer(moves.charAt(i));
            }

            /* ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
            ter.renderFrame(finalWorldFrame); */
        } else {
            int end = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == 's' || input.charAt(i) == 'S') {
                    end = i;
                    break;
                }
            }
            if (end == 0) {
                return new TETile[WORLD_WIDTH][WORLD_HEIGHT];
            }
            String sub = input.substring(1, end);
            String moves = input.substring(end);
            long seed = Long.parseLong(sub);
            test = new World(seed);
            finalWorldFrame = test.generate();

            for (int i = 0; i < moves.length(); i++) {
                if (moves.charAt(i) == ':' && (moves.charAt(i + 1) == 'q'
                        || moves.charAt(i + 1) == 'Q')) {
                    saveWorld(test);
                    break;
                }
                test.movePlayer(moves.charAt(i));
            }
           /*  ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
            ter.renderFrame(finalWorldFrame); */
        }

        return finalWorldFrame;

    }

    private static void saveWorld(World w) {
        //File f = new File("world.ser");
        try {
            /*if (!f.exists()) {
                f.createNewFile();
            }*/
            FileOutputStream fs = new FileOutputStream("world.txt");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private static World loadWorld() {
        //File f = new File("world.ser");
        //if (f.exists()) {
        try {
            FileInputStream fs = new FileInputStream("world.txt");
            ObjectInputStream os = new ObjectInputStream(fs);
            World loadWorld = (World) os.readObject();
            os.close();
            return loadWorld;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
            System.exit(0);
        }
        //}
        /* In the case no World has been saved yet, we return a new one. */
        return null;
    }


}
