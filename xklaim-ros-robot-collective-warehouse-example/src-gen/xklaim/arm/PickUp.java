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
  
  private double x;
  
  private double y;
  
  public PickUp(final String rosbridgeWebsocketURI, final Locality DeliveryRobot, final double x, final double y) {
    super("xklaim.arm.PickUp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.DeliveryRobot = DeliveryRobot;
    this.x = x;
    this.y = y;
  }
  
  @Override
  public void executeProcess() {
    try {
      String coordinates = (((("(" + Double.valueOf(this.x)) + ",") + Double.valueOf(this.y)) + ")");
      double poseX = this.x;
      double poseY = this.y;
      if ((this.x > 0)) {
        poseX = (poseX + 0.3);
      } else {
        poseX = (poseX - 0.3);
      }
      if ((this.y < 0)) {
        poseY = (poseY + 0.5);
      } else {
        poseY = (poseY - 0.5);
      }
      while (true) {
        {
          InputOutput.<String>println(("###### COORDINATES: " + coordinates));
          String itemId = null;
          Tuple _Tuple = new Tuple(new Object[] {"itemDelivered", String.class, coordinates});
          in(_Tuple, this.DeliveryRobot);
          itemId = (String) _Tuple.getItem(1);
          InputOutput.<String>println(((("###### AAAAHHHHHHHHHHH: " + itemId) + " ") + coordinates));
          Thread.sleep(2000);
          final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
          final Publisher Pose_item = new Publisher("/gazebo/set_model_state", "gazebo_msgs/ModelState", bridge);
          final ModelState modelstate = new ModelState();
          modelstate.pose.position.x = poseX;
          modelstate.pose.position.y = poseY;
          modelstate.model_name = itemId;
          modelstate.reference_frame = "world";
          Pose_item.publish(modelstate);
          InputOutput.<String>println((((((("###### Item " + itemId) + " posed at (") + Double.valueOf(poseX)) + ",") + Double.valueOf(poseY)) + ")"));
          if ((this.y < 0)) {
            poseY = (poseY + 0.3);
          } else {
            poseY = (poseY - 0.3);
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
