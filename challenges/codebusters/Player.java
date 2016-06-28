package codingame.challenges.codebusters;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the number of busters you control
        int ghostCount = in.nextInt(); // the number of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right

        System.err.println("ack");

        // game loop
        while (true) {
            int numberOfVisibleEntities = in.nextInt(); // the number of busters and ghosts visible to you.
      /* you can only "see" ghosts and rival busters when
                                                           they are within 2200 units of one of your own busters.*/
            Entity[] listOfVisibleEntities = new Entity[numberOfVisibleEntities];
            for (int i = 0; i < numberOfVisibleEntities; i++) {
                int entityId = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int entityType = in.nextInt();
                int busterState = in.nextInt();
                int behaviour = in.nextInt();
                Entity newEntity = new Entity(entityId, // buster id or ghost id
                                              x, // x-position of this buster / ghost
                                              y, // y-position of this buster / ghost
                                              entityType, // entity type: the team id if it is a buster, -1 if it is a ghost.
                                              busterState, // state (busters only): 0=idle, 1=carrying a ghost.
                                              behaviour, // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
                                              myTeamId);    // 0 or 1
                System.err.println(newEntity.allAboutMe());
                listOfVisibleEntities[i] = newEntity;
            }

            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");

                System.out.println("MOVE 8000 4500"); // MOVE x y | BUST id | RELEASE
                //System.out.println("BUST ");
            }
        }
    }

    private int getNearestGhost() {
        return 0;
    }


    private static class Entity {
        private int _entityId; // buster id or ghost id
        private int _xPosition; // x=0 left to x=*** bottom
        private int _yPosition; // y=0 top to y=*** bottom
        private int _entityType; // the team id if it is a buster, -1 if it is a ghost.
        private int _state; // For busters: 0=idle, 1=carrying a ghost.
        private int _value; // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
        private int _myTeamId; // 0 for me on top left base; 1 for bottom right base

        private Entity() {
        }

        public Entity(int entityId,
                      int xPosition,
                      int yPosition,
                      int entityType,
                      int state,
                      int value,
                      int myTeamId) {
            _entityId = entityId;
            _xPosition = xPosition;
            _yPosition = yPosition;
            _entityType = entityType;
            _state = state;
            _value = value;
            _myTeamId = myTeamId;
        }

        public int getEntityId() {
            return _entityId;
        }

        public int getX() {
            return _xPosition;
        }

        public int getY() {
            return _yPosition;
        }

        public EntityType getEntityType() {
            if (_entityType == -1) {
                return EntityType.GHOST;
            }
            else if (_entityType == _myTeamId) {
                return EntityType.MY_UNIT;
            }
            else {
                return EntityType.RIVAL_UNIT;
            }
        }

        public String allAboutMe() {
            return "Entity ID = " + getEntityType().toString() + "_" + getEntityId() + "\n--X: " + this.getX() + ",  Y: " + this.getY();
        }
    }

    public enum EntityType {
        MY_UNIT,
        RIVAL_UNIT,
        GHOST
    }

    public enum BusterState {
        IDLE,
        CARRYING_UNIT
    }

    public enum MyBasePosition {
        TOP_LEFT,
        BOTTOM_RIGHT
    }

}