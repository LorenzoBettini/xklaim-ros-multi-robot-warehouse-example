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
import org.eclipse.xtext.xbase.lib.Exceptions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class GoToInitialPosition extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public GoToInitialPosition(final String rosbridgeWebsocketURI) {
    super("xklaim.arm.GoToInitialPosition");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    try {
      final Locality local = this.self;
      final RosBridge bridge = new RosBridge();
      bridge.connect(this.rosbridgeWebsocketURI, true);
      in(new Tuple(new Object[] {"releaseCompleted"}), this.self);
      Thread.sleep(1000);
      final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
      final List<Double> jointPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000)));
      final JointTrajectory initialPositionsTrajectory = new JointTrajectory().positions(((double[])Conversions.unwrapArray(jointPositions, double.class))).jointNames(
        new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
      pub.publish(initialPositionsTrajectory);
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
          out(new Tuple(new Object[] {"initialPosition"}), local);
          bridge.unsubscribe("/arm_controller/state");
        }
      };
      bridge.subscribe(
        SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
