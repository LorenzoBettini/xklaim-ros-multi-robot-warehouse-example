package xklaim.arm;

import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import messages.ModelState;
import messages.XklaimToRosConnection;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;

@SuppressWarnings("all")
public class PickUp extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality DeliveryRobot;
  
  public PickUp(final String rosbridgeWebsocketURI, final Locality DeliveryRobot) {
    super("xklaim.arm.PickUp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.DeliveryRobot = DeliveryRobot;
  }
  
  @Override
  public void executeProcess() {
    try {
      double offset = 0.0;
      while (true) {
        {
          String id = null;
          Double x = null;
          Double y = null;
          Tuple _Tuple = new Tuple(new Object[] {"itemDelivered", String.class, Double.class, Double.class});
          in(_Tuple, this.DeliveryRobot);
          id = (String) _Tuple.getItem(1);
          x = (Double) _Tuple.getItem(2);
          y = (Double) _Tuple.getItem(3);
          Thread.sleep(2000);
          final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
          final Publisher Pose_item = new Publisher("/gazebo/set_model_state", "gazebo_msgs/ModelState", bridge);
          final ModelState modelstate = new ModelState();
          modelstate.pose.position.x = (((x).doubleValue() + 0.3) + offset);
          modelstate.pose.position.y = (((y).doubleValue() + 0.3) + offset);
          modelstate.model_name = id;
          modelstate.reference_frame = "world";
          Pose_item.publish(modelstate);
          InputOutput.<String>println((((((("### Item " + id) + " delivered at (") + Double.valueOf((((x).doubleValue() + 0.3) + offset))) + ",") + Double.valueOf((((y).doubleValue() + 0.3) + offset))) + ")"));
          double _offset = offset;
          offset = (_offset + 0.3);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
