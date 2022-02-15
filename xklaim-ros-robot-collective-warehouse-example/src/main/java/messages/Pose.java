package messages;

public class Pose {
    public Point position = new Point();
    public Quaternion orientation = new Quaternion();

    public Pose(){}

    public Pose(Point position, Quaternion orientation) {
        this.position = position;
        this.orientation = orientation;
    }

}
