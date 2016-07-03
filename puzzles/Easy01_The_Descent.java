import java.util.*;
import java.io.*;
import java.math.*;

/**
 * At the start of each game turn, you are given the height of the 8 mountains from left to right.
 * By the end of the game turn, you must fire the highest mountain by outputting its index (from 0 to 7).
 * Firing on a mountain will only destroy part of it, reducing its height. Your ship descends after each pass.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        while (true) { // game loop
            List<Mountain> listOfMountains = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                int mountainHeight = in.nextInt(); // represents the height of one mountain, from 9 to 0.
                listOfMountains.add(new Mountain(i, mountainHeight));
            }

            Mountain tallestMountain = getHighestMountainFromList(listOfMountains);
            System.out.println(tallestMountain.getIndex()); // The number of the mountain to fire on.
        }
    }

    static Mountain getHighestMountainFromList(List<Mountain> listOfMountains) {
        Mountain tallestMountain = new Mountain(-1, 0);
        for (Mountain mountain : listOfMountains) {
            if (mountain.getHeight() > tallestMountain.getHeight()) {
                tallestMountain = mountain;
            }
        }
        return tallestMountain;
    }
}

class Mountain {
    int _index;
    int _height;

    Mountain(int index, int height) {
        _index = index;
        _height = height;
    }

    int getIndex() {
        return _index;
    }

    int getHeight() {
        return _height;
    }
}