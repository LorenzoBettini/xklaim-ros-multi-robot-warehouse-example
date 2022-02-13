package xklaim.arm;

import java.ModelState;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Exceptions;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class Disappear extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality DeliveryRobot;
  
  public Disappear(final String rosbridgeWebsocketURI, final Locality DeliveryRobot) {
    super("xklaim.arm.Disappear");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.DeliveryRobot = DeliveryRobot;
  }
  
  @Override
  public void executeProcess() {
    try {
      final RosBridge bridge = new RosBridge();
      bridge.connect(this.rosbridgeWebsocketURI, true);
      while (true) {
        {
          Double x = null;
          Double y = null;
          Tuple _Tuple = new Tuple(new Object[] {"itemDelivered", Double.class, Double.class});
          in(_Tuple, this.DeliveryRobot);
          x = (Double) _Tuple.getItem(1);
          y = (Double) _Tuple.getItem(2);
          Thread.sleep(2000);
          final Publisher gazebo = new Publisher("/gazebo/set_model_state", "gazebo_msgs/ModelState", bridge);
          final ModelState modelstate = new ModelState();
          modelstate.twist.linear.x = 3.0;
          modelstate.twist.angular.z = 1.0;
          modelstate.pose.position.x = (-46.0);
          modelstate.pose.position.y = 46.0;
          modelstate.pose.position.z = 0.0;
          modelstate.model_name = "unit_box_2";
          modelstate.reference_frame = "world";
          gazebo.publish(modelstate);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
