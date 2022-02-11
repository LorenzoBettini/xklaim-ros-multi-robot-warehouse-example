package xklaim.deliveryRobot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.PoseStamped;
import java.PoseWithCovarianceStamped;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Exceptions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.geometry_msgs.Twist;

@SuppressWarnings("all")
public class MovetoArm extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality arm;
  
  public MovetoArm(final String rosbridgeWebsocketURI, final Locality arm) {
    super("xklaim.deliveryRobot.MovetoArm");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.arm = arm;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/robot1/move_base_simple/goal", "geometry_msgs/PoseStamped", bridge);
    Double x = null;
    Double y = null;
    Double w = null;
    Tuple _Tuple = new Tuple(new Object[] {"comeHere", Double.class, Double.class, Double.class});
    in(_Tuple, this.self);
    x = (Double) _Tuple.getItem(1);
    y = (Double) _Tuple.getItem(2);
    w = (Double) _Tuple.getItem(3);
    final PoseStamped destination = new PoseStamped().headerFrameId("world").posePositionXY((x).doubleValue(), (y).doubleValue()).poseOrientation((w).doubleValue());
    pub.publish(destination);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rosMsgNode = data.get("msg");
        PoseWithCovarianceStamped current_position = mapper.<PoseWithCovarianceStamped>treeToValue(rosMsgNode, PoseWithCovarianceStamped.class);
        final double tolerance = 0.16;
        double deltaX = (current_position.pose.pose.position.x - destination.pose.position.x);
        double deltaY = (current_position.pose.pose.position.y - destination.pose.position.y);
        if (((deltaX <= tolerance) && (deltaY <= tolerance))) {
          final Publisher pubvel = new Publisher("/robot1/cmd_vel", "geometry_msgs/Twist", bridge);
          final Twist twistMsg = new Twist();
          pubvel.publish(twistMsg);
          out(new Tuple(new Object[] {"ready"}), this.arm);
          bridge.unsubscribe("/robot1/amcl_pose");
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/robot1/amcl_pose").setType("geometry_msgs/PoseWithCovarianceStamped").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
