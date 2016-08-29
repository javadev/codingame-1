import java.util.*;
import java.io.*;
import java.math.*;

class Player {
    public static int LENGTH_OF_ROAD_BEFORE_GAP = 0;
    public static int LENGTH_OF_GAP = 0;
    public static int LENGTH_OF_LANDING_PLATFORM = 0;
    public static int NECESSARY_SPEED_TO_CROSS_GAP = 0;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        // all length measurements are in units of "cells" (integer).
        LENGTH_OF_ROAD_BEFORE_GAP = in.nextInt() - 1; // the length of the road before the gap.
        LENGTH_OF_GAP = in.nextInt(); // the length of the gap.
        LENGTH_OF_LANDING_PLATFORM = in.nextInt(); // the length of the landing platform.
        NECESSARY_SPEED_TO_CROSS_GAP = LENGTH_OF_GAP + 1;

        Biker biker = new Biker();

        while (true) { // Game loop
            biker.setSpeed(in.nextInt()); // the motorbike's speed.
            biker.setXPosition(in.nextInt()); // the position on the road of the motorbike.

            biker.makeMove();
        }
    }

    public static class Biker {
        private int _speed;
        private int _xPosition;

        public int getSpeed() {
            return _speed;
        }

        public void setSpeed(int newSpeed) {
            _speed = newSpeed;
        }

        public int getXPosition() {
            return _xPosition;
        }

        public void setXPosition(int newXPosition) {
            _xPosition = newXPosition;
        }

        private void increaseSpeed() {
            System.out.println("SPEED");
        }

        private void decreaseSpeed() {
            System.out.println("SLOW");
        }

        private void keepSpeed() {
            System.out.println("WAIT");
        }

        private void jump() {
            System.out.println("JUMP");
        }

        private boolean isBeforeGap() {
            return getXPosition() <= LENGTH_OF_ROAD_BEFORE_GAP;
        }

        private boolean isAtGap() {
            return getXPosition() == LENGTH_OF_ROAD_BEFORE_GAP;
        }

        private boolean isAfterGap() {
            return getXPosition() > LENGTH_OF_ROAD_BEFORE_GAP;
        }

        public void makeMove() {
            if (isAtGap()) {
                jump();
            }

            else if (isBeforeGap()) {
                if (getSpeed() < NECESSARY_SPEED_TO_CROSS_GAP) {
                    increaseSpeed();
                } else if (getSpeed() > NECESSARY_SPEED_TO_CROSS_GAP) {
                    decreaseSpeed();
                } else {
                    keepSpeed();
                }
            }

            else if (isAfterGap()) {
                decreaseSpeed();
            }

            else {
                decreaseSpeed();
            }
        }
    }
}