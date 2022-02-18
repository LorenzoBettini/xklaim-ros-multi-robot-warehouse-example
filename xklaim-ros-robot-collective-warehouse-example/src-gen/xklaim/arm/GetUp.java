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
import org.eclipse.xtext.xbase.lib.DoubleExtensions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class GetUp extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Double x_coordination;
  
  private Double y_coordination;
  
  public GetUp(final String rosbridgeWebsocketURI, final Double x_coordination, final Double y_coordination) {
    super("xklaim.arm.GetUp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.x_coordination = x_coordination;
    this.y_coordination = y_coordination;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    double _divide = DoubleExtensions.operator_divide(this.y_coordination, this.x_coordination);
    double _atan = Math.atan(_divide);
    double _minus = (_atan - 3.14);
    final List<Double> jointPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf(_minus), Double.valueOf((-0.2862)), Double.valueOf((-0.5000)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))));
    final JointTrajectory getUpJointTrajectory = new JointTrajectory().positions(((double[])Conversions.unwrapArray(jointPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    in(new Tuple(new Object[] {"gripCompleted"}), this.self);
    pub.publish(getUpJointTrajectory);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double delta = 0.0;
      final double tolerance = 0.008;
      for (int i = 0; (i < jointPositions.size()); i++) {
        double _delta = delta;
        double _asDouble = actual.get(i).asDouble();
        Double _get = jointPositions.get(i);
        double _minus_1 = (_asDouble - (_get).doubleValue());
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
