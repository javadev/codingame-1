import java.util.Scanner;

@SuppressWarnings("all")

class Player {
    static Pod myPod = new Pod(0, 0);
    static Pod enemyPod = new Pod(0, 0);
    static Checkpoint nextCheckpoint = new Checkpoint(0, 0);

    static Scanner inStream;
    static int nextCheckpointDistance;
    static int nextCheckpointAngle;

    public static void main(String args[]) {
        inStream = new Scanner(System.in);

        while (true) { // game loop
            updateGameObjects();

            if (myPod.isAbleToBoost()
                    && myPod.isFacingNextCheckpoint(nextCheckpointAngle)
                    && nextCheckpointDistance > 3000) {
                myPod.useBoostAbility(nextCheckpoint.getX(), nextCheckpoint.getY());
            } else {
                int speed = calculateOptimalSpeed(nextCheckpointAngle);
                myPod.moveAtSpeed(speed, nextCheckpoint.getX(), nextCheckpoint.getY());
            }
        }
    }

    static void updateGameObjects() {
        myPod.updateCoordinates(inStream.nextInt(), inStream.nextInt());
        nextCheckpoint.updateCoordinates(inStream.nextInt(), inStream.nextInt());
        nextCheckpointDistance = inStream.nextInt();
        nextCheckpointAngle = inStream.nextInt();
        enemyPod.updateCoordinates(inStream.nextInt(), inStream.nextInt());
    }

    static int calculateOptimalSpeed(int angleBetweenPodAndNextCheckpoint) {
        if (angleBetweenPodAndNextCheckpoint > 90 || angleBetweenPodAndNextCheckpoint < -90) {
            return 0;
        } else if (angleBetweenPodAndNextCheckpoint > 45 || angleBetweenPodAndNextCheckpoint < -45) {
            return 50;
        } else {
            return 100;
        }
    }
}

abstract class GameObject {
    private static int _nextCheckpointNumber = 0;
    protected int _xCoordinate;
    protected int _yCoordinate;

    public static int getNextCheckpointNumber() {
        int thisCheckpointNumber = _nextCheckpointNumber;
        _nextCheckpointNumber++;
        return thisCheckpointNumber;
    }

    public int getX() {
        return _xCoordinate;
    }

    public int getY() {
        return _yCoordinate;
    }

    public void updateCoordinates(int xCoordinate, int yCoordinate) {
        _xCoordinate = xCoordinate;
        _yCoordinate = yCoordinate;
    }
}

class Pod extends GameObject {
    boolean _isAbleToBoost = true;

    public Pod(int initialXCoordinate, int initialYCoordinate) {
        updateCoordinates(initialXCoordinate, initialYCoordinate);
    }

    public void moveAtSpeed(int speed, int xCoordOfDestination, int yCoordOfDestination) {
        System.out.println(xCoordOfDestination + " " + yCoordOfDestination + " " + speed);
    }

    public void useBoostAbility(int xCoordOfDestination, int yCoordOfDestination) {
        System.out.println(xCoordOfDestination + " " + yCoordOfDestination + " BOOST");
    }

    public boolean isAbleToBoost() {
        return _isAbleToBoost;
    }

    public boolean isFacingNextCheckpoint(int angleBetweenPodAndNextCheckpoint) {
        return angleBetweenPodAndNextCheckpoint < 30 && angleBetweenPodAndNextCheckpoint > -30;
    }
}

class Checkpoint extends GameObject {
    private int _checkpointNumber;

    public Checkpoint(int xCoordinate, int yCoordinate) {
        updateCoordinates(xCoordinate, yCoordinate);
        _checkpointNumber = GameObject.getNextCheckpointNumber();
    }

    public int getCheckpointNumber() {
        return _checkpointNumber;
    }
}