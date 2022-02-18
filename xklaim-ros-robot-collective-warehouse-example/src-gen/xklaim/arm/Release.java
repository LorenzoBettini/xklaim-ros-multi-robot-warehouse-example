package xklaim.arm;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import messages.JointTrajectory;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Release extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private String itemType;
  
  public Release(final String rosbridgeWebsocketURI, final String itemType) {
    super("xklaim.arm.Release");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.itemType = itemType;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    in(new Tuple(new Object[] {"layCompleted"}), this.self);
    final Publisher pub = new Publisher("/gripper_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final List<Double> jointPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf(0.0), Double.valueOf(0.0)));
    final JointTrajectory openGripperTrajectory = new JointTrajectory().positions(((double[])Conversions.unwrapArray(jointPositions, double.class))).jointNames(
      new String[] { "f_joint1", "f_joint2" });
    pub.publish(openGripperTrajectory);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double delta = 0.0;
      final double tolerance = 0.0008;
      for (int i = 0; (i < jointPositions.size()); i = (i + 1)) {
        double _delta = delta;
        double _asDouble = actual.get(i).asDouble();
        Double _get = jointPositions.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        delta = (_delta + _pow);
      }
      final double norm = Math.sqrt(delta);
      if ((norm <= tolerance)) {
        out(new Tuple(new Object[] {"releaseCompleted"}), local);
        out(new Tuple(new Object[] {"gripperOpened", this.itemType}), local);
        bridge.unsubscribe("/gripper_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType(
        "control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
