import java.util.Scanner;

@SuppressWarnings("all")

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        while (true) { // game loop
            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();

            if (nextCheckpointDist > 7000 && nextCheckpointDist < 8000) {
                System.out.println(nextCheckpointX + " " + nextCheckpointY + " BOOST");
            } else {
                int speed = calculateOptimalSpeed(nextCheckpointAngle);
                System.out.println(nextCheckpointX + " " + nextCheckpointY + " " + speed);
            }
        }
    }

    static int calculateOptimalSpeed(int angleBetweenPodAndNextCheckpoint) {
        if (angleBetweenPodAndNextCheckpoint > 90 || angleBetweenPodAndNextCheckpoint < -90) {
            return 0;
        } else {
            return 100;
        }
    }
}