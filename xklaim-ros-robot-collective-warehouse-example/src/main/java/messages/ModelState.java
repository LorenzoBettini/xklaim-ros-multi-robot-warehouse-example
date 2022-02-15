package messages;

public class ModelState {
	public String model_name  ;
	public Pose pose = new Pose();
	public Twist twist = new Twist();
	public String reference_frame;


    public ModelState(){}

    public ModelState(String names, Pose pose, Twist twist, String reference_frame) {
        this.model_name = names;
        this.pose = pose;
        this.twist = twist;
        this.reference_frame=reference_frame;
    }

}
