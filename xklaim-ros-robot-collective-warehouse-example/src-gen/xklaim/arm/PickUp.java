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
  
  private Double x;
  
  private Double y;
  
  public PickUp(final String rosbridgeWebsocketURI, final Locality DeliveryRobot, final Double x, final Double y) {
    super("xklaim.arm.PickUp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.DeliveryRobot = DeliveryRobot;
    this.x = x;
    this.y = y;
  }
  
  @Override
  public void executeProcess() {
    try {
      Double poseX = this.x;
      Double poseY = this.y;
      if (((this.x).doubleValue() > 0)) {
        poseX = Double.valueOf(((poseX).doubleValue() + 0.3));
      } else {
        poseX = Double.valueOf(((poseX).doubleValue() - 0.3));
      }
      if (((this.y).doubleValue() < 0)) {
        poseY = Double.valueOf(((poseY).doubleValue() + 0.5));
      } else {
        poseY = Double.valueOf(((poseY).doubleValue() - 0.5));
      }
      while (true) {
        {
          String id = null;
          Tuple _Tuple = new Tuple(new Object[] {"itemDelivered", String.class, this.x, this.y});
          in(_Tuple, this.DeliveryRobot);
          id = (String) _Tuple.getItem(1);
          InputOutput.<String>println((((((("###### Item " + id) + " delivered at (") + this.x) + ",") + this.y) + ")"));
          Thread.sleep(2000);
          final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
          final Publisher Pose_item = new Publisher("/gazebo/set_model_state", "gazebo_msgs/ModelState", bridge);
          final ModelState modelstate = new ModelState();
          modelstate.pose.position.x = (poseX).doubleValue();
          modelstate.pose.position.y = (poseY).doubleValue();
          modelstate.model_name = id;
          modelstate.reference_frame = "world";
          Pose_item.publish(modelstate);
          InputOutput.<String>println((((((("###### Item " + id) + " posed at (") + poseX) + ",") + poseY) + ")"));
          if (((this.y).doubleValue() < 0)) {
            poseY = Double.valueOf(((poseY).doubleValue() + 0.3));
          } else {
            poseY = Double.valueOf(((poseY).doubleValue() - 0.3));
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
