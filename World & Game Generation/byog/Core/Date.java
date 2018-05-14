package byog.Core;

import java.io.Serializable;

public class Date implements Serializable{
    public static String name;
    public static String date;

    public Date(){
        String name = Game.name;
        String date = Game.nowString;
    }
}
