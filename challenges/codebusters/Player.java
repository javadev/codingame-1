/*
 * Comment out the package name line to sync file with Codingame:
 */
//package codingame.challenges.codebusters;

import java.util.*;
import java.io.*;
import java.math.*;

enum EntityType {
    MY_UNIT,
    ENEMY_UNIT,
    GHOST
}

enum BasePosition {
    TOP_LEFT(0, 0),
    BOTTOM_RIGHT(16000, 9000);

    private int _xPosition;
    private int _yPosition;

    BasePosition(int x, int y) {
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
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the number of busters you control
        int startNumberOfGhosts = in.nextInt(); // the number of ghosts on the map
        int myTeamID = in.nextInt(); // if this is 0, your base is on the top left of the map; one, bottom right

        BasePosition myBasePosition = getBasePositionFromTeamID(myTeamID);
        debug("myBasePosition = " + myBasePosition);
        debug("numberOfGhosts = " + startNumberOfGhosts);

        while (true) { // Game loop
            int numberOfVisibleEntities = in.nextInt(); // the number of busters and ghosts visible to you (within 2200 units of a buster)
            List<Buster> listOfMyUnits = new ArrayList<>();
            List<Entity> listOfVisibleGhosts = new ArrayList<>();
            List<Entity> listOfVisibleEnemyUnits = new ArrayList<>();
            for (int i = 0; i < numberOfVisibleEntities; i++) {
                int entityID = in.nextInt(); // buster id or ghost id
                int x = in.nextInt(); // x-position of this buster / ghost
                int y = in.nextInt(); // y-position of this buster / ghost
                int entityType = in.nextInt(); // entity type: the team id if it is a buster, -1 if it is a ghost.
                int busterState = in.nextInt(); // state (busters only): 0=idle, 1=carrying a ghost.
                int numberOfInteractions = in.nextInt(); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.

                EntityType thisEntityType = getEntityTypeFromInt(entityType, myTeamID);
                switch (thisEntityType) {
                    case MY_UNIT:
                        listOfMyUnits.add(new Buster(entityID, x, y, busterState, numberOfInteractions));
                        break;
                    case ENEMY_UNIT:
                        listOfVisibleEnemyUnits.add(new Buster(entityID, x, y, busterState, numberOfInteractions));
                        break;
                    case GHOST:
                        listOfVisibleGhosts.add(new Ghost(entityID, x, y, numberOfInteractions));
                        break;
                    default:
                        break;
                }
            }

            for (int i = 0; i < bustersPerPlayer; i++) {
                Buster thisBuster = listOfMyUnits.get(i);

                // To debug: System.err.println("Debug messages...");
                debug("i=" + i + "  " + thisBuster.aboutMe());

                // Write an action using System.out.println()
                System.out.println("MOVE 8000 4500"); // MOVE x y | BUST id | RELEASE
            }
        }
    }

    private static void debug(String debugMessage) {
        System.err.println(debugMessage);
    }

    private static EntityType getEntityTypeFromInt(int entityType,
                                                   int myTeamID) {
        if (entityType == -1) {
            return EntityType.GHOST;
        } else if (entityType == myTeamID) {
            return EntityType.MY_UNIT;
        } else {
            return EntityType.ENEMY_UNIT;
        }
    }

    private static BasePosition getBasePositionFromTeamID(int teamID) {
        if (teamID == 0) {
            return BasePosition.TOP_LEFT;
        } else {
            return BasePosition.BOTTOM_RIGHT;
        }
    }
}

class Entity {
    int _entityID; // starts from 0
    int _xPosition; // x=0 left
    int _yPosition; // y=0 top
    EntityType _entityType;

    Entity() {
    }

    private int getID() {
        return _entityID;
    }

    private int getX() {
        return _xPosition;
    }

    private int getY() {
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

    private Buster() {
    }

    Buster(int entityID,
           int xPosition,
           int yPosition,
           int busterState,
           int numberOfInteractions) {
        _entityType = EntityType.MY_UNIT;
        _entityID = entityID;
        _xPosition = xPosition;
        _yPosition = yPosition;

        if (busterState == 0 && numberOfInteractions == -1) {
            _isCarryingAGhost = false;
        } else if (busterState == 1 & numberOfInteractions > -1) {
            _isCarryingAGhost = true;
            _idOfGhostBeingCarried = numberOfInteractions;
        } else {
            throw new IllegalArgumentException("Invalid buster state!");
        }
    }

    boolean isCarryingAGhost() {
        return _isCarryingAGhost;
    }

    int idOfGhostBeingCarried() {
        return _idOfGhostBeingCarried;
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