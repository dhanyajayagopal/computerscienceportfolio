package byog.Core;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class World implements Serializable {

    private static final int WORLD_WIDTH = 70;
    private static final int WORLD_HEIGHT = 40;
    // public TERenderer ter;
    TETile[][] world;
    private int numRooms;
    private Room[] rooms;
    private Random rand;
    private Player player;
    public String date;
    public static String name;

    public World(long seed) {
        //ter = new TERenderer();
       // ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        for (int x = 0; x < WORLD_WIDTH; x += 1) {
            for (int y = 0; y < WORLD_HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        rand = new Random(seed);
        numRooms = RandomUtils.uniform(rand, 6) + 10;
        rooms = new Room[numRooms];
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        date =  dtf.format(now);
        name = Game.name;
    }

    public TETile[][] generate() {
        addAllRooms();
        connectAll();
        cleanup();
        Room pos = rooms[0];
        player = new Player(pos.bottomLeftX + pos.width / 2, pos.bottomLeftY + pos.height / 2);
        world[player.xPos][player.yPos] = Tileset.PLAYER;

        return world;
    }

    private boolean isWall(int x, int y) {
        if (world[x][y].description().equals("wall")) {
            return true;
        }
        return false;
    }

    public void movePlayer(char c) {
        if (c == 'a' || c == 'A') {
            if (!isWall(player.xPos - 1, player.yPos)) {
                world[player.xPos][player.yPos] = Tileset.FLOOR;
                world[player.xPos - 1][player.yPos] = Tileset.PLAYER;
                player.xPos -= 1;
            }
        } else if (c == 's' || c == 'S') {
            if (!isWall(player.xPos, player.yPos - 1)) {
                world[player.xPos][player.yPos] = Tileset.FLOOR;
                world[player.xPos][player.yPos - 1] = Tileset.PLAYER;
                player.yPos -= 1;
            }

        } else if (c == 'd' || c == 'D') {
            if (!isWall(player.xPos + 1, player.yPos)) {
                world[player.xPos][player.yPos] = Tileset.FLOOR;
                world[player.xPos + 1][player.yPos] = Tileset.PLAYER;
                player.xPos += 1;
            }

        } else if (c == 'w' || c == 'W') {
            if (!isWall(player.xPos, player.yPos + 1)) {
                world[player.xPos][player.yPos] = Tileset.FLOOR;
                world[player.xPos][player.yPos + 1] = Tileset.PLAYER;
                player.yPos += 1;
            }
        }
    }


    public int numFloor(int x, int y) {
        int count = 0;
        if (world[x - 1][y + 1] == Tileset.FLOOR) {
            count++;
        }
        if (world[x - 1][y] == Tileset.FLOOR) {
            count++;
        }
        if (world[x - 1][y - 1] == Tileset.FLOOR) {
            count++;
        }
        if (world[x][y + 1] == Tileset.FLOOR) {
            count++;
        }
        if (world[x][y - 1] == Tileset.FLOOR) {
            count++;
        }
        if (world[x + 1][y + 1] == Tileset.FLOOR) {
            count++;
        }
        if (world[x + 1][y] == Tileset.FLOOR) {
            count++;
        }
        if (world[x + 1][y - 1] == Tileset.FLOOR) {
            count++;
        }
        return count;
    }

    public boolean close(int x, int y) {
        if (world[x - 1][y] == Tileset.WALL && world[x + 1][y] == Tileset.WALL) {
            if (world[x][y + 1] == Tileset.NOTHING || world[x][y - 1] == Tileset.NOTHING) {
                return true;
            }
        }
        return false;
    }

    public void cleanup() {
        for (int i = 2; i < WORLD_WIDTH; i++) {
            for (int j = 2; j < WORLD_HEIGHT; j++) {
                if (world[i][j] == Tileset.WALL && (numFloor(i, j) == 8 || numFloor(i, j) == 7)) {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i][j] == Tileset.FLOOR && close(i, j)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public void addSingleRoom(Room r) {
        // adds a single, acceptable room to the rooms list
        for (int i = r.bottomLeftX; i < r.bottomLeftX + r.width; i++) {
            for (int j = r.bottomLeftY; j < r.bottomLeftY + r.height; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }

        for (int i = r.bottomLeftX; i < r.bottomLeftX + r.width; i++) {
            world[i][r.bottomLeftY] = Tileset.WALL;
        }

        for (int i = r.bottomLeftX; i < r.bottomLeftX + r.width; i++) {
            world[i][r.bottomLeftY + r.height - 1] = Tileset.WALL;
        }

        for (int i = r.bottomLeftY; i < r.bottomLeftY + r.height; i++) {
            world[r.bottomLeftX][i] = Tileset.WALL;
            world[r.bottomLeftX + r.width - 1][i] = Tileset.WALL;
        }
    }

    public boolean roomWorks(Room r) {
        // checks if room overlaps with any other existing room
        for (int i = 0; i <= numRooms - 1; i++) {
            if (rooms[i] == null) {
                return true;
            }
            if (rooms[i].overlap(r)) {
                return false;
            }
        }
        return true;
    }

    public void addAllRooms() {
        // add all rooms to the list
        int roomCount = 0;
        while (roomCount <= numRooms - 1) {
            int randomStartX = RandomUtils.uniform(rand, WORLD_WIDTH - 12) + 2;
            int randomStartY = RandomUtils.uniform(rand, WORLD_HEIGHT - 10) + 2;
            int randomWidth = RandomUtils.uniform(rand, 3) + 5;
            int randomHeight = RandomUtils.uniform(rand, 3) + 5;
            Room potential = new Room(randomStartX, randomStartY, randomWidth, randomHeight);
           // if (roomWorks(potential)) {
            addSingleRoom(potential);
            rooms[roomCount] = potential;
            roomCount += 1;
           // }
        }
    }

    public void connectAll() {
        for (int r = 1; r < numRooms; r++) {
            int x = r - 1;
            connectWithHallway(rooms[r - 1], rooms[r]);
        }
        connectWithHallway(rooms[numRooms - 1], rooms[0]);

    }

    public void connectWithHallway(Room a, Room b) {
        int[] positions = getHallwayStart(a, b);
        // go right
        for (int i = positions[0] - 1; i <= positions[2]; i++) {
            int j = positions[1];

            if (world[i][j + 1] == Tileset.NOTHING) {
                world[i][j + 1] = Tileset.WALL;
            }
            world[i][j] = Tileset.FLOOR;

            if (world[i][j - 1] == Tileset.NOTHING) {
                world[i][j - 1] = Tileset.WALL;
            }

        }

        if (positions[3] < positions[1]) {
            for (int j = positions[1] + 1; j >= positions[3] - 1; j--) {
                int i = positions[2];
                if (world[i + 1][j] == Tileset.NOTHING) {
                    world[i + 1][j] = Tileset.WALL;
                }
                if (j == positions[1] + 1 && world[i][j] != Tileset.FLOOR) {
                    world[i][j] = Tileset.WALL;
                } else if (world[i][j] != Tileset.FLOOR) {
                    world[i][j] = Tileset.FLOOR;
                } else {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i - 1][j] == Tileset.NOTHING) {
                    world[i - 1][j] = Tileset.WALL;
                }
            }
        } else {
            for (int j = positions[1] - 1; j <= positions[3]; j++) {
                int i = positions[2];
                if (world[i + 1][j] == Tileset.NOTHING) {
                    world[i + 1][j] = Tileset.WALL;
                }
                if (j == positions[1] - 1) {
                    world[i][j] = Tileset.WALL;
                } else {
                    world[i][j] = Tileset.FLOOR;
                }
                if (world[i - 1][j] == Tileset.NOTHING) {
                    world[i - 1][j] = Tileset.WALL;
                }
            }
        }

    }

    public int[] getHallwayStart(Room a, Room b) {
        int[] positions = new int[4];
        // should return the x,y coordinate of where to start & the x,y coordinate of where to end
        if (a.bottomLeftX < b.bottomLeftX) {
            positions[0] = a.bottomLeftX + a.width;
            positions[1] = a.bottomLeftY + a.height / 2;
            if (a.bottomLeftY > b.bottomLeftY) {
                positions[2] = b.bottomLeftX + b.width / 2;
                positions[3] = b.bottomLeftY + b.height;
            } else {
                positions[2] = b.bottomLeftX + b.width / 2;
                positions[3] = b.bottomLeftY;
            }
        } else {
            positions[0] = b.bottomLeftX + b.width;
            positions[1] = b.bottomLeftY + b.height / 2;
            if (b.bottomLeftY > a.bottomLeftY) {
                positions[2] = a.bottomLeftX + a.width / 2;
                positions[3] = a.bottomLeftY + a.height;
            } else {
                positions[2] = a.bottomLeftX + a.width / 2;
                positions[3] = a.bottomLeftY;
            }
        }
        return positions;
    }
}

