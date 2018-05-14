package byog.Core;

import java.io.Serializable;

public class Room implements Serializable {

    int bottomLeftX;
    int bottomLeftY;
    int width;
    int height;

    public Room(int bottomLeftX, int bottomLeftY, int w, int h) {
        this.bottomLeftX = bottomLeftX;
        this.bottomLeftY = bottomLeftY;
        width = w;
        height = h;
    }

    public boolean overlap(Room other) {
        //bottom left
        int bLX = other.bottomLeftX;
        int bLY = other.bottomLeftY;
        if (pointInRoom(this, bLX, bLY)) {
            return true;
        }

        //bottom right
        int bottomRightX = other.bottomLeftX + other.width;
        int bottomRightY = other.bottomLeftY;
        if (pointInRoom(this, bottomRightX, bottomRightY)) {
            return true;
        }

        //top right
        int topRightX = other.bottomLeftX + other.width;
        int topRightY = other.bottomLeftY + other.height;
        if (pointInRoom(this, topRightX, topRightY)) {
            return true;
        }


        //top left
        int topLeftX = other.bottomLeftX;
        int topLeftY = other.bottomLeftY + other.height;
        if (pointInRoom(this, topLeftY, topLeftY)) {
            return true;
        }

        return false;
    }

    public boolean pointInRoom(Room r, int x, int y) {
        if (x >= r.bottomLeftX - 2 && x <= (r.bottomLeftX + width + 2)) {
            if (y >= r.bottomLeftY - 2 && y <= (r.bottomLeftY + height + 2)) {
                return true;
            }
        }

        return false;

    }

}
