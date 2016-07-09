import java.util.Scanner;

@SuppressWarnings("all")

enum OrdinalDirection {
    N(0, -1),
    E(1, 0),
    S(0, 1),
    W(-1, 0),
    NE(1, -1),
    NW(-1, -1),
    SE(1, 1),
    SW(-1, 1);

    int _xMovement;
    int _yMovement;

    OrdinalDirection(int xMovement, int yMovement) {
        _xMovement = xMovement;
        _yMovement = yMovement;
    }

    int getXMovement() {
        return _xMovement;
    }

    int getYMovement() {
        return _yMovement;
    }
}

class Player {
    private static int thorCurrentXPosition;
    private static int thorCurrentYPosition;
    private final int MAX_X_COORDINATE = 40; // x=0 to x=39, left to right
    private final int MAX_Y_COORDINATE = 18; // y=0 to y=17, top to bottom

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int lightXPosition = in.nextInt(); // the X position of the light of power
        int lightYPosition = in.nextInt(); // the Y position of the light of power
        int initialThorXPosition = in.nextInt(); // Thor's starting X position
        System.err.println("initialThorXPosition = " + initialThorXPosition);
        int initialThorYPosition = in.nextInt(); // Thor's starting Y position
        System.err.println("initialThorYPosition = " + initialThorYPosition);

        thorCurrentXPosition = initialThorXPosition;
        thorCurrentYPosition = initialThorYPosition;

        while (true) { // game loop
            int remainingTurns = in.nextInt(); // The remaining amount of turns Thor can move. Do not remove this line.
            moveThor(lightXPosition, lightYPosition);
        }
    }

    static int calculateXDelta(int lightXPosition, int thorXPosition) {
        return lightXPosition - thorXPosition;
    }

    static int calculateYDelta(int lightYPosition, int thorYPosition) {
        return lightYPosition - thorYPosition;
    }

    static OrdinalDirection getDirectionOfMovement(int xDelta, int yDelta) {
        if (xDelta == 0 && yDelta < 0) {
            return OrdinalDirection.N;
        } else if (xDelta == 0 && yDelta > 0) {
            return OrdinalDirection.S;
        } else if (yDelta == 0 && xDelta < 0) {
            return OrdinalDirection.W;
        } else if (yDelta == 0 && xDelta > 0) {
            return OrdinalDirection.E;
        } else if (yDelta < 0 && xDelta < 0) {
            return OrdinalDirection.NW;
        } else if (yDelta < 0 && xDelta > 0) {
            return OrdinalDirection.NE;
        } else if (yDelta > 0 && xDelta < 0) {
            return OrdinalDirection.SW;
        } else {
            return OrdinalDirection.SE;
        }
    }

    static void moveThor(int lightXPosition, int lightYPosition) {
        OrdinalDirection directionThorWillMove = getDirectionOfMovement(calculateXDelta(lightXPosition,
                                                                                        Player.thorCurrentXPosition),
                                                                        calculateYDelta(lightYPosition,
                                                                                        Player.thorCurrentYPosition));
        System.out.println(directionThorWillMove.toString());
        Player.thorCurrentXPosition += directionThorWillMove.getXMovement();
        Player.thorCurrentYPosition += directionThorWillMove.getYMovement();
    }
}