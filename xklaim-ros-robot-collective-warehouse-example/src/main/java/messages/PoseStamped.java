package messages;

import ros.msgs.std_msgs.Header;

public class PoseStamped {
	public Header header = new Header();
	public Pose pose = new Pose();

	public PoseStamped() {
	}

	public PoseStamped(Header header, Pose pose) {
		this.header = header;
		this.pose = pose;
	}

	public PoseStamped(Pose pose) {
		this.pose = pose;
	}

	public PoseStamped headerFrameId(String frameId) {
		header.frame_id = frameId;
		return this;
	}

	public PoseStamped posePositionXY(double x, double y) {
		pose.position.x = x;
		pose.position.y = y;
		return this;
	}

	public PoseStamped poseOrientation(double w) {
		pose.orientation.w = w;
		return this;
	}
}
