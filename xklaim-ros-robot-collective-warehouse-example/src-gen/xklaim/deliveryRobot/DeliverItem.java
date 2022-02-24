package xklaim.deliveryRobot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import messages.ContactState;
import messages.ContactsState;
import messages.PoseStamped;
import messages.PoseWithCovarianceStamped;
import messages.Twist;
import messages.XklaimToRosConnection;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class DeliverItem extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private String robotId;
  
  private Locality Arm;
  
  public DeliverItem(final String rosbridgeWebsocketURI, final String robotId, final Locality Arm) {
    super("xklaim.deliveryRobot.DeliverItem");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.robotId = robotId;
    this.Arm = Arm;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    in(new Tuple(new Object[] {"readyToReceiveTheItem"}), this.self);
    String itemId = null;
    String itemType = null;
    Tuple _Tuple = new Tuple(new Object[] {"gripperOpened", String.class, String.class});
    in(_Tuple, this.Arm);
    itemId = (String) _Tuple.getItem(1);
    itemType = (String) _Tuple.getItem(2);
    final String id = itemId;
    Double x = null;
    Double y = null;
    Tuple _Tuple_1 = new Tuple(new Object[] {"type2destination", itemType, Double.class, Double.class});
    read(_Tuple_1, this.self);
    x = (Double) _Tuple_1.getItem(2);
    y = (Double) _Tuple_1.getItem(3);
    final Double destinationX = x;
    final Double destinationY = y;
    final PoseStamped deliveryDestination = new PoseStamped().headerFrameId("world").posePositionXY((x).doubleValue(), (y).doubleValue()).poseOrientation(1.0);
    InputOutput.<String>println(((((((("###############[" + this.robotId) + "] type2destination") + itemType) + ",") + x) + ",") + y));
    final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
    final Publisher pub = new Publisher((("/" + this.robotId) + "/move_base_simple/goal"), "geometry_msgs/PoseStamped", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rosMsgNode = data.get("msg");
        ContactsState state = mapper.<ContactsState>treeToValue(rosMsgNode, ContactsState.class);
        InputOutput.<String>print((("###############[" + this.robotId) + "] Sensor pressure: "));
        boolean _isEmpty = ((List<ContactState>)Conversions.doWrapArray(state.states)).isEmpty();
        if (_isEmpty) {
          InputOutput.<String>println("empty state");
        } else {
          InputOutput.<String>println(("total_wrench.force.z=" + Double.valueOf((state.states[0]).total_wrench.force.z)));
        }
        if (((!((List<ContactState>)Conversions.doWrapArray(state.states)).isEmpty()) && ((state.states[0]).total_wrench.force.z != 0.0))) {
          pub.publish(deliveryDestination);
          InputOutput.<String>println(((((("###############[" + this.robotId) + "] deliveryDestinationPublished: ") + destinationX) + ",") + destinationY));
          bridge.unsubscribe((("/" + this.robotId) + "/pressure_sensor_state"));
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate((("/" + this.robotId) + "/pressure_sensor_state")).setType("gazebo_msgs/ContactsState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
    final RosListenDelegate _function_1 = (JsonNode data, String stringRep) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rosMsgNode = data.get("msg");
        PoseWithCovarianceStamped current_position = mapper.<PoseWithCovarianceStamped>treeToValue(rosMsgNode, PoseWithCovarianceStamped.class);
        final double tolerance = 0.16;
        double deltaX = (current_position.pose.pose.position.x - deliveryDestination.pose.position.x);
        double deltaY = (current_position.pose.pose.position.y - deliveryDestination.pose.position.y);
        if (((deltaX <= tolerance) && (deltaY <= tolerance))) {
          InputOutput.<String>println((("################# The robot " + this.robotId) + " is arrived at destination"));
          final Publisher pubvel = new Publisher((("/" + this.robotId) + "/cmd_vel"), "geometry_msgs/Twist", bridge);
          final Twist twistMsg = new Twist();
          pubvel.publish(twistMsg);
          out(new Tuple(new Object[] {"itemDelivered", destinationX, destinationY}), local);
          out(new Tuple(new Object[] {"availableForDelivery"}), local);
          bridge.unsubscribe((("/" + this.robotId) + "/amcl_pose"));
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate((("/" + this.robotId) + "/amcl_pose")).setType(
        "geometry_msgs/PoseWithCovarianceStamped").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function_1);
  }
}
