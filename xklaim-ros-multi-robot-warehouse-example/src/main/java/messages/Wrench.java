package messages;

public class Wrench {
	public Vector3 force = new Vector3();
	public Vector3 torque = new Vector3();

	public Wrench(){}

	public Wrench(Vector3 force, Vector3 torque) {
		this.force = force;
		this.torque = torque;
	}
}
