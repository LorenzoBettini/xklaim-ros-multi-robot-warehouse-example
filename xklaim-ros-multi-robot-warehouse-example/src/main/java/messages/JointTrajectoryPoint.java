package messages;

public class JointTrajectoryPoint {

	public double[] positions;
	public double[] velocities;
	public double[] accelerations;
	public double[] effort;
	public Duration time_from_start = new Duration(20, 0);

	public JointTrajectoryPoint() {
		this.velocities = new double[0];
		this.accelerations = new double[0];
		this.effort = new double[0];
	}

	public JointTrajectoryPoint positions(double[] positions) {
		this.positions = positions;
		return this;
	}
	
	public JointTrajectoryPoint velocities(double[] velocities) {
		this.velocities = velocities;
		return this;
	}

	public JointTrajectoryPoint timeFromStart(Duration time_from_start) {
		this.time_from_start = time_from_start;
		return this;
	}
}