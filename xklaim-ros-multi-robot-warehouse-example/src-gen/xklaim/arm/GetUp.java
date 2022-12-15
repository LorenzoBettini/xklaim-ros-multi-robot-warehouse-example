package xklaim.arm;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import messages.Duration;
import messages.JointTrajectory;
import messages.JointTrajectoryPoint;
import messages.XklaimToRosConnection;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.DoubleExtensions;
import ros.Publisher;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class GetUp extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Double x;
  
  private Double y;
  
  public GetUp(final String rosbridgeWebsocketURI, final Double x, final Double y) {
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.x = x;
    this.y = y;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    in(new Tuple(new Object[] {"gripCompleted"}), this.self);
    final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    double _divide = DoubleExtensions.operator_divide(this.y, this.x);
    double _atan = Math.atan(_divide);
    double _minus = (_atan - 3.14);
    final List<Double> jointPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf(_minus), Double.valueOf((-0.2862)), Double.valueOf((-0.5000)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))));
    final JointTrajectory getUpJointTrajectory = new JointTrajectory().positions(((double[])Conversions.unwrapArray(jointPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    JointTrajectoryPoint _get = getUpJointTrajectory.points[0];
    Duration _duration = new Duration(120, 0);
    _get.time_from_start = _duration;
    pub.publish(getUpJointTrajectory);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double delta = 0.0;
      final double tolerance = 0.008;
      for (int i = 0; (i < jointPositions.size()); i++) {
        double _delta = delta;
        double _asDouble = actual.get(i).asDouble();
        Double _get_1 = jointPositions.get(i);
        double _minus_1 = (_asDouble - (_get_1).doubleValue());
        double _pow = Math.pow(_minus_1, 2.0);
        delta = (_delta + _pow);
      }
      final double norm = Math.sqrt(delta);
      if ((norm <= tolerance)) {
        out(new Tuple(new Object[] {"getUpCompleted"}), local);
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
