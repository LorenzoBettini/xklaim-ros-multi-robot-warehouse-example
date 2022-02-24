package xklaim.arm;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import messages.JointTrajectory;
import messages.XklaimToRosConnection;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import ros.Publisher;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Rotate extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private String sector;
  
  public Rotate(final String rosbridgeWebsocketURI, final String sector) {
    super("xklaim.arm.Rotate");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.sector = sector;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final List<Double> jointPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-0.9546)), Double.valueOf((-0.20)), Double.valueOf((-0.7241)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))));
    final JointTrajectory rotateTrajectory = new JointTrajectory().positions(((double[])Conversions.unwrapArray(jointPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    in(new Tuple(new Object[] {"getUpCompleted"}), this.self);
    out(new Tuple(new Object[] {"itemReadyForTheDelivery", this.sector}), this.self);
    pub.publish(rotateTrajectory);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double delta = 0.0;
      final double tolerance = 0.008;
      for (int i = 0; (i < jointPositions.size()); i++) {
        double _delta = delta;
        double _asDouble = actual.get(i).asDouble();
        Double _get = jointPositions.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        delta = (_delta + _pow);
      }
      final double norm = Math.sqrt(delta);
      if ((norm <= tolerance)) {
        out(new Tuple(new Object[] {"rotationCompleted"}), local);
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
