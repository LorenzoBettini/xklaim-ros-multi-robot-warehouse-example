package messages;

public class ModelStates {
	public String[] name;
	public Pose[] pose = new Pose[1];
	public Twist[] twist = new Twist[1];
	


    public ModelStates(){}

    public ModelStates(String[] names, Pose[] pose, Twist[] twist) {
        this.name = names;
        this.pose = pose;
        this.twist = twist;
    }

}
