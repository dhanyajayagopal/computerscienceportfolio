package byog.lab5;

import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(40, 50);
        TETile[][] world = new TETile[40][50];
        for (int x = 0; x < 40; x += 1) {
            for (int y = 0; y < 50; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Position p = new Position(10, 20);
        int s = 7;
        TETile t = Tileset.MOUNTAIN;
        addHexagon(world, p, s, t);
        ter.renderFrame(world);

    }

    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        if (s < 2) {
            throw new IllegalArgumentException("Hexagon must be at least size 2.");
        }

        for (int i = 0; i < s * 2; i++) {
            int offset = rowOffset(s, i);
            int x_start = p.x + offset;
            int y_start = p.y + i;
            Position row = new Position(x_start, y_start);
            addRow(world, row, rowWidth(s, i), t);
        }
    }

    public static int rowOffset(int s, int rownum) {
        int offset;
        if (rownum < s) {
            offset = rownum % s;
        }
        else {
            offset = (s*2 - 1) % rownum;
        }
        return -offset;
    }

    public static int rowWidth(int s, int rownum) {
        return (rowOffset(s, rownum) * -2) + s;
    }

    public static void addRow(TETile[][] world, Position p, int width, TETile t) {
        for (int i = 0; i < width; i ++) {
            int x_cord = p.x + i;
            int y_cord = p.y;
            Random r = new Random();
            try {
                world[x_cord][y_cord] = TETile.colorVariant(t, 32, 32, 32, r);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException("Tried to access x_cord " + x_cord + " and y_cord " + y_cord + " and i " + i);
            }

        }
    }

}
