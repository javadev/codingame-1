/*
 * Comment out the package name line to sync file with Codingame:
 */
//package codingame.challenges.codebusters;

import java.util.*;

enum Const {
    MAX_X_COORDINATE(16000),
    MAX_Y_COORDINATE(9000),
    BUST_DISTANCE_MAX(1760),
    BUST_DISTANCE_MIN(900),
    RELEASE_RANGE(1600),
    FOG_LIMIT(2200), // radius of sight for each buster
    MOVE_DISTANCE(800);

    private int _value;

    Const(int value) {
        _value = value;
    }

    int get() {
        return _value;
    }
}

enum EntityType {
    MY_UNIT,
    ENEMY_UNIT,
    GHOST
}

enum Position {
    TOP_LEFT(0, 0),
    BOTTOM_LEFT(0, Const.MAX_Y_COORDINATE.get()),
    TOP_RIGHT(Const.MAX_X_COORDINATE.get(), 0),
    BOTTOM_RIGHT(Const.MAX_X_COORDINATE.get(), Const.MAX_Y_COORDINATE.get());

    private int _xPosition;
    private int _yPosition;

    Position(int x, int y) {
        _xPosition = x;
        _yPosition = y;
    }

    int getX() {
        return _xPosition;
    }

    int getY() {
        return _yPosition;
    }
}

/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 **/
class Player {
    public static void main(String args[]) {

        // Game constants
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the number of busters you control
        int startNumberOfGhosts = in.nextInt(); // the number of ghosts on the map
        int myTeamID = in.nextInt(); // if this is 0, your base is on the top left of the map; one, bottom right

        Position myBasePosition = getMyBasePositionFromTeamID(myTeamID);
        Position enemyBasePosition = getEnemyBasePositionFromTeamID(myTeamID);
        debug("myBasePosition = " + myBasePosition);

        // First turn - get info about my units (mainly to seed the positionSelector)
        InputDataPerTurn inputData = new InputDataPerTurn(in, myTeamID, myBasePosition, enemyBasePosition);
        List<Buster> listOfMyUnits = inputData.getListOfMyUnits();
        List<Ghost> listOfVisibleGhosts = inputData.getlistOfVisibleGhosts();
        List<Buster> listOfVisibleEnemies = inputData.getListOfVisibleEnemyUnits();
        PositionSelector positionSelector = new PositionSelector(myBasePosition, listOfMyUnits);

        for (Buster buster : listOfMyUnits) { // (you have to issue commands every turn)
            Position position = positionSelector.getDirectionForBuster(buster);
            buster.moveToward(position);
        }

        while (true) { // Game loop
            inputData = new InputDataPerTurn(in, myTeamID, myBasePosition, enemyBasePosition);
            listOfMyUnits = inputData.getListOfMyUnits();
            listOfVisibleGhosts = inputData.getlistOfVisibleGhosts();
            listOfVisibleEnemies = inputData.getListOfVisibleEnemyUnits();

            for (int i = 0; i < bustersPerPlayer; i++) {
                Buster thisBuster = listOfMyUnits.get(i);

                if (thisBuster.isCarryingAGhost()) {
                    if (thisBuster.isWithinRangeOfHomeBase()) {
                        thisBuster.releaseGhost();
                    } else {
                        thisBuster.moveToward(myBasePosition);
                    }

                } else { //seek out a ghost to capture
                    if (thisBuster.canCaptureaGhost(listOfVisibleGhosts)) {
                        int ghostID = thisBuster.getIDOfACapturableGhost(listOfVisibleGhosts);
                        thisBuster.captureGhost(ghostID);
                    } else {
                        thisBuster.moveToward(positionSelector.getDirectionForBuster(thisBuster));
                    }
                }
            }
        }
    }

    private static void debug(String debugMessage) {
        System.err.println(debugMessage);
    }

    private static Position getMyBasePositionFromTeamID(int teamID) {
        if (teamID == 0) {
            return Position.TOP_LEFT;
        } else {
            return Position.BOTTOM_RIGHT;
        }
    }

    private static Position getEnemyBasePositionFromTeamID(int teamID) {
        if (teamID == 1) {
            return Position.TOP_LEFT;
        } else {
            return Position.BOTTOM_RIGHT;
        }
    }
}

class InputDataPerTurn {
    int _numberOfVisibleEntities;
    List<Buster> _listOfMyUnits;
    List<Ghost> _listOfVisibleGhosts;
    List<Buster> _listOfVisibleEnemyUnits;

    InputDataPerTurn(Scanner in,
                     int myTeamID,
                     Position myBasePosition,
                     Position enemyBasePosition) {
        _numberOfVisibleEntities = in.nextInt(); // the number of busters and ghosts visible to you (within 2200 units of a buster)
        _listOfMyUnits = new ArrayList<>();
        _listOfVisibleGhosts = new ArrayList<>();
        _listOfVisibleEnemyUnits = new ArrayList<>();
        for (int i = 0; i < _numberOfVisibleEntities; i++) {
            int entityID = in.nextInt(); // buster id or ghost id
            int x = in.nextInt(); // x-position of this buster / ghost
            int y = in.nextInt(); // y-position of this buster / ghost
            int entityType = in.nextInt(); // entity type: the team id if it is a buster, -1 if it is a ghost.
            int busterState = in.nextInt(); // state (busters only): 0=idle, 1=carrying a ghost.
            int numberOfInteractions = in.nextInt(); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.

            EntityType thisEntityType = getEntityTypeFromInt(entityType, myTeamID);
            switch (thisEntityType) {
                case MY_UNIT:
                    _listOfMyUnits.add(new Buster(entityID, x, y, busterState, numberOfInteractions, myBasePosition));
                    break;
                case ENEMY_UNIT:
                    _listOfVisibleEnemyUnits.add(new Buster(entityID, x, y, busterState, numberOfInteractions, enemyBasePosition));
                    break;
                case GHOST:
                    _listOfVisibleGhosts.add(new Ghost(entityID, x, y, numberOfInteractions));
                    break;
                default:
                    break;
            }
        }
    }

    List<Buster> getListOfMyUnits() {
        return _listOfMyUnits;
    }

    List<Buster> getListOfVisibleEnemyUnits() {
        return _listOfVisibleEnemyUnits;
    }

    List<Ghost> getlistOfVisibleGhosts() {
        return _listOfVisibleGhosts;
    }

    private EntityType getEntityTypeFromInt(int entityType, int myTeamID) {
        if (entityType == -1) {
            return EntityType.GHOST;
        } else if (entityType == myTeamID) {
            return EntityType.MY_UNIT;
        } else {
            return EntityType.ENEMY_UNIT;
        }
    }
}

class PositionSelector {
    private List<Position> _listOfPositions;
    private Map<Integer, Integer> _busterIDToIndexOfRotation;
    private int _maxIndexOfRotation;

    PositionSelector(Position myBasePosition,
                     List<Buster> listOfBusters) {
        _listOfPositions = new ArrayList<>();
        _busterIDToIndexOfRotation = new HashMap<>();
        if (myBasePosition == Position.TOP_LEFT) {
            _listOfPositions.add(Position.TOP_RIGHT);
            _listOfPositions.add(Position.BOTTOM_RIGHT);
            _listOfPositions.add(Position.BOTTOM_LEFT);
        } else { // myBase Position == Position.BOTTOM_RIGHT
            _listOfPositions.add(Position.BOTTOM_LEFT);
            _listOfPositions.add(Position.TOP_LEFT);
            _listOfPositions.add(Position.TOP_RIGHT);
        }
        int currentIndexOfRotation = 0;
        _maxIndexOfRotation = _listOfPositions.size() - 1;
        for (Buster buster : listOfBusters) {
            _busterIDToIndexOfRotation.put(buster.getID(), currentIndexOfRotation);
            currentIndexOfRotation = getNextIndex(currentIndexOfRotation);
        }
    }

    Map<Integer, Integer> getMap() {
        return _busterIDToIndexOfRotation;
    }

    int getNextIndex(int currentIndex) {
        if (currentIndex == _maxIndexOfRotation) {
            return 0;
        } else {
            return currentIndex + 1;
        }
    }

    Position getDirectionForBuster(Buster buster) {
        int busterID = buster.getID();
        int currentIndexOfRotation = _busterIDToIndexOfRotation.get(busterID);
        Position currentDirection = _listOfPositions.get(currentIndexOfRotation);
        if (Distance.isWithinRangeOf(buster.getX(), buster.getY(), currentDirection, 5)) {
            int newIndexOfRotation = getNextIndex(currentIndexOfRotation);
            _busterIDToIndexOfRotation.remove(busterID);
            _busterIDToIndexOfRotation.put(busterID, newIndexOfRotation);
            return _listOfPositions.get(newIndexOfRotation);
        } else {
            return currentDirection;
        }
    }
}

class Entity {
    int _entityID; // starts from 0
    int _xPosition; // x=0 left
    int _yPosition; // y=0 top
    EntityType _entityType;

    int getID() {
        return _entityID;
    }

    int getX() {
        return _xPosition;
    }

    int getY() {
        return _yPosition;
    }

    private EntityType getEntityType() {
        return _entityType;
    }

    String aboutMe() {
        return "Entity:  " + getEntityType().toString() + "_" + getID() + "\n    X: " + this.getX() + ",  Y: " + this.getY();
    }
}

class Buster extends Entity {
    private boolean _isCarryingAGhost;
    private int _idOfGhostBeingCarried;
    private Position _positionOfHomeBase;

    Buster(int entityID,
           int xPosition,
           int yPosition,
           int busterState,
           int numberOfInteractions,
           Position positionOfHomeBase) {
        _entityType = EntityType.MY_UNIT;
        _entityID = entityID;
        _xPosition = xPosition;
        _yPosition = yPosition;
        _positionOfHomeBase = positionOfHomeBase;

        if (busterState == 0) {
            _isCarryingAGhost = false;
        } else if (busterState == 1 & numberOfInteractions > -1) {
            _isCarryingAGhost = true;
            _idOfGhostBeingCarried = numberOfInteractions;
        }
    }

    boolean isCarryingAGhost() {
        return _isCarryingAGhost;
    }

    int idOfGhostBeingCarried() {
        return _idOfGhostBeingCarried;
    }

    boolean isWithinRangeOfHomeBase() {
        return Distance.isWithinRangeOf(_xPosition, _yPosition, _positionOfHomeBase, Const.RELEASE_RANGE.get());
    }

    boolean canCaptureaGhost(List<Ghost> listOfVisibleGhosts) {
        for (Ghost ghost : listOfVisibleGhosts) {
            int distanceBetween = Distance.between(_xPosition, _yPosition, ghost.getX(), ghost.getY());
            if (distanceBetween < Const.BUST_DISTANCE_MAX.get()
                    && distanceBetween > Const.BUST_DISTANCE_MIN.get()) {
                return true;
            }
        }
        return false;
    }

    int getIDOfACapturableGhost(List<Ghost> listOfVisibleGhosts) {
        int idOfClosestGhost = -1;
        for (Ghost ghost : listOfVisibleGhosts) {
            int distanceBetween = Distance.between(_xPosition, _yPosition, ghost.getX(), ghost.getY());
            if (distanceBetween < Const.BUST_DISTANCE_MAX.get()
                    && distanceBetween > Const.BUST_DISTANCE_MIN.get()) {
                return ghost.getID();
            }
        }
        return idOfClosestGhost;
    }

    private void move(int x, int y) {
        System.out.println("MOVE " + x + " " + y);
    }

    void captureGhost(int ghostID) {
        System.out.println("BUST " + ghostID);
    }

    void releaseGhost() {
        System.out.println("RELEASE");
    }

    void moveToward(Position position) {
        move(position.getX(), position.getY());
    }
}

class Ghost extends Entity {
    private int _numberOfBusterThreats;

    private Ghost() {
    }

    Ghost(int entityID,
          int xPosition,
          int yPosition,
          int numberOfInteractions) {
        _entityType = EntityType.GHOST;
        _entityID = entityID;
        _xPosition = xPosition;
        _yPosition = yPosition;
        _numberOfBusterThreats = numberOfInteractions;
    }

    int getNumberOfBusterThreats() {
        return _numberOfBusterThreats;
    }
}

class Distance {
    static int between(int xPositionA,
                       int yPositionA,
                       Position positionB) {
        return between(xPositionA, yPositionA, positionB.getX(), positionB.getY());
    }

    static int between(int xPositionA,
                       int yPositionA,
                       int xPositionB,
                       int yPositionB) {
        double xSquared = Math.pow((double) xPositionA - (double) xPositionB,
                                   2.0);
        double ySquared = Math.pow((double) yPositionA - (double) yPositionB,
                                   2.0);
        double resultAsDouble = Math.sqrt(xSquared + ySquared);
        return (int) Math.floor(resultAsDouble);
    }

    static boolean isWithinRangeOf(int xPosition,
                                   int yPosition,
                                   Position positionToCompareTo,
                                   int range) {
        return isWithinRangeOf(xPosition, yPosition, positionToCompareTo.getX(), positionToCompareTo.getY(), range);
    }

    static boolean isWithinRangeOf(int xPositionOfSource,
                                   int yPositionOfSource,
                                   int xPositionOfTarget,
                                   int yPositionOfTarget,
                                   int range) {
        int distance = Distance.between(xPositionOfSource, yPositionOfSource, xPositionOfTarget, yPositionOfTarget);
        return distance < range;
    }
}