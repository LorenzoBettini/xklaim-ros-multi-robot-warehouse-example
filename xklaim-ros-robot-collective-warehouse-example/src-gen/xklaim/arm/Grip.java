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
public class Grip extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public Grip(final String rosbridgeWebsocketURI) {
    super("xklaim.arm.Grip");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/gripper_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final List<Double> gripperPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf(0.0138), Double.valueOf((-0.0138))));
    final JointTrajectory grip = new JointTrajectory().positions(((double[])Conversions.unwrapArray(gripperPositions, double.class))).jointNames(
      new String[] { "f_joint1", "f_joint2" });
    in(new Tuple(new Object[] {"getDownMovementsCompleted"}), this.self);
    pub.publish(grip);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double delta = 0.0;
      final double tolerance = 0.007;
      for (int i = 0; (i < gripperPositions.size()); i++) {
        double _delta = delta;
        double _asDouble = actual.get(i).asDouble();
        Double _get = gripperPositions.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        delta = (_delta + _pow);
      }
      final double norm = Math.sqrt(delta);
      if ((norm <= tolerance)) {
        out(new Tuple(new Object[] {"gripCompleted"}), local);
        bridge.unsubscribe("/gripper_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType(
        "control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
