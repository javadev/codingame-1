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
    STUN_DISTANCE_MAX(1760),
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

enum BusterState {
    IDLE_OR_MOVING,
    CARRYING_GHOST,
    STUNNED
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
    private static Map<Integer, Buster> busterCache = new HashMap<>();

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
        debug("inputData= " + inputData);
        List<Buster> listOfMyUnits = Player.getListOfMyUnits();
        debug("listOfMyUnits= " + listOfMyUnits);
        List<Ghost> listOfVisibleGhosts = inputData.getListOfVisibleGhosts();
        debug("listOfVisibleGhosts= " + listOfVisibleGhosts);
        List<Buster> listOfVisibleEnemies = inputData.getListOfVisibleEnemyUnits();
        debug("listOfVisibleEnemies= " + listOfVisibleEnemies);
        PositionSelector positionSelector = new PositionSelector(myBasePosition, listOfMyUnits);

        for (Buster buster : listOfMyUnits) { // (you have to issue commands every turn)
            debug("buster= " + buster.getID());
            Position position = positionSelector.getDirectionForBuster(buster);
            buster.moveToward(position);
        }

        while (true) { // Game loop
            inputData = new InputDataPerTurn(in, myTeamID, myBasePosition, enemyBasePosition);
            listOfMyUnits = Player.getListOfMyUnits();
            listOfVisibleGhosts = inputData.getListOfVisibleGhosts();
            listOfVisibleEnemies = inputData.getListOfVisibleEnemyUnits();

            for (int i = 0; i < bustersPerPlayer; i++) {
                Buster thisBuster = listOfMyUnits.get(i);

                if (thisBuster.isStunned()) {
                    break;
                }

                if (thisBuster.isCarryingAGhost()) {
                    if (thisBuster.isWithinRangeOfHomeBase()) {
                        thisBuster.releaseGhost();
                    } else {
                        thisBuster.moveToward(myBasePosition);
                    }

                } else { //seek out a ghost to capture
                    if (thisBuster.isAbleToUseStunAbility()
                            && thisBuster.canStunEnemyBuster(listOfVisibleEnemies)) {
                        thisBuster.stunEnemyBuster(thisBuster.getIDOfSomeEnemyBuster(listOfVisibleEnemies));
                    } else if (thisBuster.canCaptureGhost(listOfVisibleGhosts)) {
                        int ghostID = thisBuster.getIDOfACapturableGhost(listOfVisibleGhosts);
                        thisBuster.captureGhost(ghostID);
                    } else {
                        thisBuster.moveToward(positionSelector.getDirectionForBuster(thisBuster));
                    }
                }
            }
        }
    }

    static Map<Integer, Buster> getBusterCache() {
        return busterCache;
    }

    static List<Buster> getListOfMyUnits() {
        List<Buster> listOfBusters = new ArrayList<>();
        for (Map.Entry<Integer, Buster> busterEntry : getBusterCache().entrySet()) {
            listOfBusters.add(busterEntry.getValue());
        }
        return listOfBusters;
    }

    static void addOrUpdateBustersInCache(List<Buster> listOfBusters) {
        for (Buster buster : listOfBusters) {
            addOrUpdateBusterInCache(buster.getID(),
                                     buster.getX(),
                                     buster.getY(),
                                     buster.getBusterState(),
                                     buster.getID(),
                                     Position.TOP_LEFT);
        }
    }

    static void addOrUpdateBusterInCache(int entityID,
                                         int x,
                                         int y,
                                         BusterState busterState,
                                         int numberOfInteractions,
                                         Position myBasePosition) {
        if (busterCache.containsKey(entityID)) {
            busterCache.get(entityID).update(x, y, busterState, numberOfInteractions);
        } else {
            Buster newBuster = new Buster(entityID, x, y, busterState, numberOfInteractions, myBasePosition);
            busterCache.put(entityID, newBuster);
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
    private int _numberOfVisibleEntities;
    private List<Ghost> _listOfVisibleGhosts;
    private List<Buster> _listOfVisibleEnemyUnits;

    InputDataPerTurn(Scanner in,
                     int myTeamID,
                     Position myBasePosition,
                     Position enemyBasePosition) {
        _numberOfVisibleEntities = in.nextInt(); // the number of busters and ghosts visible to you (within 2200 units of a buster)
        _listOfVisibleGhosts = new ArrayList<>();
        _listOfVisibleEnemyUnits = new ArrayList<>();
        for (int i = 0; i < _numberOfVisibleEntities; i++) {
            int entityID = in.nextInt(); // buster id or ghost id
            int x = in.nextInt(); // x-position of this buster / ghost
            int y = in.nextInt(); // y-position of this buster / ghost
            int entityType = in.nextInt(); // entity type: the team id if it is a buster, -1 if it is a ghost.
            int busterState = in.nextInt(); // state (busters only): 0=idle, 1=carrying a ghost.
            int numberOfInteractions = in.nextInt(); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.

            BusterState thisBusterState = getBusterStateFromInt(busterState);
            EntityType thisEntityType = getEntityTypeFromInt(entityType, myTeamID);

            switch (thisEntityType) {
                case MY_UNIT:
                    Player.addOrUpdateBusterInCache(entityID, x, y, thisBusterState, numberOfInteractions, myBasePosition);
                    break;
                case ENEMY_UNIT:
                    _listOfVisibleEnemyUnits.add(new Buster(entityID, x, y, thisBusterState, numberOfInteractions, enemyBasePosition));
                    break;
                case GHOST:
                    _listOfVisibleGhosts.add(new Ghost(entityID, x, y, numberOfInteractions));
                    break;
                default:
                    break;
            }
        }
    }

    List<Buster> getListOfVisibleEnemyUnits() {
        return _listOfVisibleEnemyUnits;
    }

    List<Ghost> getListOfVisibleGhosts() {
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

    private BusterState getBusterStateFromInt(int busterState) {
        switch (busterState) {
            case 1:
                return BusterState.CARRYING_GHOST;
            case 2:
                return BusterState.STUNNED;
            default: // 0
                return BusterState.IDLE_OR_MOVING;
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
    private Position _positionOfHomeBase;
    private BusterState _busterState;
    private int _idOfGhostBeingCarried = -1;
    private int _numberOfTurnsUntilStunAbilityIsReady = 0;

    Buster(int entityID,
           int xPosition,
           int yPosition,
           BusterState busterState,
           int numberOfInteractions,
           Position positionOfHomeBase) {
        _entityID = entityID;
        _xPosition = xPosition;
        _yPosition = yPosition;
        _busterState = busterState;
        _positionOfHomeBase = positionOfHomeBase;
        _entityType = EntityType.MY_UNIT;

        if (isCarryingAGhost()) {
            _idOfGhostBeingCarried = numberOfInteractions;
        }
    }

    // Update (persistent) buster each turn:
    void update(int x,
                int y,
                BusterState newBusterState,
                int numberOfInteractions) {
        _xPosition = x;
        _yPosition = y;
        _busterState = newBusterState;

        if (isCarryingAGhost()) {
            _idOfGhostBeingCarried = numberOfInteractions;
        }

        if (_numberOfTurnsUntilStunAbilityIsReady > 0) {
            _numberOfTurnsUntilStunAbilityIsReady--;
        }
    }

    BusterState getBusterState() {
        return _busterState;
    }

    boolean isCarryingAGhost() {
        return (_busterState == BusterState.CARRYING_GHOST);
    }

    boolean isStunned() {
        return (_busterState == BusterState.STUNNED);
    }

    boolean isAbleToUseStunAbility() {
        return _numberOfTurnsUntilStunAbilityIsReady == 0;
    }

    boolean isWithinRangeOfHomeBase() {
        return Distance.isWithinRangeOf(_xPosition, _yPosition, _positionOfHomeBase, Const.RELEASE_RANGE.get());
    }

    boolean canCaptureGhost(List<Ghost> listOfVisibleGhosts) {
        for (Ghost ghost : listOfVisibleGhosts) {
            int distanceBetween = Distance.between(_xPosition, _yPosition, ghost.getX(), ghost.getY());
            if (distanceBetween < Const.BUST_DISTANCE_MAX.get()
                    && distanceBetween > Const.BUST_DISTANCE_MIN.get()) {
                return true;
            }
        }
        return false;
    }

    boolean canStunEnemyBuster(List<Buster> listOfEnemyBusters) {
        if (this.isAbleToUseStunAbility()) {
            for (Buster enemyBuster : listOfEnemyBusters) {
                int distanceBetween = Distance.between(getX(), getY(), enemyBuster.getX(), enemyBuster.getY());
                if (distanceBetween < Const.STUN_DISTANCE_MAX.get()
                        && !enemyBuster.isStunned()) {
                    return true;
                }
            }
        }
        return false;
    }

    int idOfGhostBeingCarried() {
        return _idOfGhostBeingCarried;
    }

    int getIDOfACapturableGhost(List<Ghost> listOfVisibleGhosts) {
        for (Ghost ghost : listOfVisibleGhosts) {
            int distanceBetween = Distance.between(_xPosition, _yPosition, ghost.getX(), ghost.getY());
            if (distanceBetween < Const.BUST_DISTANCE_MAX.get()
                    && distanceBetween > Const.BUST_DISTANCE_MIN.get()) {
                return ghost.getID();
            }
        }
        return -1;
    }

    int getIDOfSomeEnemyBuster(List<Buster> listOfEnemyBusters) {
        for (Buster enemyBuster : listOfEnemyBusters) {
            int distanceBetween = Distance.between(getX(), getY(), enemyBuster.getX(), enemyBuster.getY());
            if (distanceBetween < Const.STUN_DISTANCE_MAX.get()) {
                return enemyBuster.getID();
            }
        }
        return -1;
    }

    private void move(int x, int y) {
        System.out.println("MOVE " + x + " " + y);
    }

    void moveToward(Position position) {
        move(position.getX(), position.getY());
    }

    void captureGhost(int ghostID) {
        System.out.println("BUST " + ghostID);
    }

    void releaseGhost() {
        System.out.println("RELEASE");
    }

    void stunEnemyBuster(int enemyBusterID) {
        _numberOfTurnsUntilStunAbilityIsReady = 20;
        System.out.println("STUN " + enemyBusterID);
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