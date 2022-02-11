package xklaim.arm;

import com.fasterxml.jackson.databind.JsonNode;
import java.JointTrajectory;
import java.util.Collections;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class GetDown extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public GetDown(final String rosbridgeWebsocketURI) {
    super("xklaim.arm.GetDown");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final List<Double> trajectoryPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412))));
    final JointTrajectory firstMovement = new JointTrajectory().positions(((double[])Conversions.unwrapArray(trajectoryPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    final List<Double> secondTrajectoryPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))));
    final JointTrajectory secondMovement = new JointTrajectory().positions(((double[])Conversions.unwrapArray(secondTrajectoryPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    pub.publish(firstMovement);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double delta1 = 0.0;
      double delta2 = 0.0;
      final double tolerance1 = 0.000001;
      final double tolerance2 = 0.00001;
      for (int i = 0; (i < trajectoryPositions.size()); i++) {
        {
          double _delta1 = delta1;
          double _asDouble = actual.get(i).asDouble();
          Double _get = trajectoryPositions.get(i);
          double _minus = (_asDouble - (_get).doubleValue());
          double _abs = Math.abs(_minus);
          delta1 = (_delta1 + _abs);
          double _delta2 = delta2;
          double _asDouble_1 = actual.get(i).asDouble();
          Double _get_1 = secondTrajectoryPositions.get(i);
          double _minus_1 = (_asDouble_1 - (_get_1).doubleValue());
          double _pow = Math.pow(_minus_1, 2.0);
          delta2 = (_delta2 + _pow);
        }
      }
      final double norm = Math.sqrt(delta2);
      if ((delta1 <= tolerance1)) {
        pub.publish(secondMovement);
      }
      if ((norm <= tolerance2)) {
        out(new Tuple(new Object[] {"getDownMovementsCompleted"}), local);
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
