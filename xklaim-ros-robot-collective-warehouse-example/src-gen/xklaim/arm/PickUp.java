package xklaim.arm;

import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import messages.ModelState;
import messages.XklaimToRosConnection;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;

/**
 * posare l'item in posizioni predefinite per evitare problemi con il calcolo dell'offset
 */
@SuppressWarnings("all")
public class PickUp extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality DeliveryRobot;
  
  private String sector;
  
  public PickUp(final String rosbridgeWebsocketURI, final Locality DeliveryRobot, final String sector) {
    super("xklaim.arm.PickUp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.DeliveryRobot = DeliveryRobot;
    this.sector = sector;
  }
  
  @Override
  public void executeProcess() {
    try {
      double offset = 0.0;
      double initialXforRed = (-9.5);
      double initialYforRed = (-9.0);
      double initialXforBlue = 9.5;
      double initialYforBlue = (-9.0);
      boolean _equals = this.sector.equals("sector2");
      if (_equals) {
        initialXforRed = 9.5;
        initialYforRed = 9.0;
        initialXforBlue = (-9.5);
        initialYforBlue = 9.0;
      }
      while (true) {
        {
          String id = null;
          String type = null;
          Double x = null;
          Double y = null;
          Tuple _Tuple = new Tuple(new Object[] {"itemDelivered", String.class, String.class, Double.class, Double.class});
          in(_Tuple, this.DeliveryRobot);
          id = (String) _Tuple.getItem(1);
          type = (String) _Tuple.getItem(2);
          x = (Double) _Tuple.getItem(3);
          y = (Double) _Tuple.getItem(4);
          Thread.sleep(2000);
          final XklaimToRosConnection bridge = new XklaimToRosConnection(this.rosbridgeWebsocketURI);
          final Publisher Pose_item = new Publisher("/gazebo/set_model_state", "gazebo_msgs/ModelState", bridge);
          final ModelState modelstate = new ModelState();
          boolean _equals_1 = type.equals("red");
          if (_equals_1) {
            modelstate.pose.position.x = initialXforRed;
            modelstate.pose.position.y = (initialYforRed + offset);
          } else {
            modelstate.pose.position.x = initialXforBlue;
            modelstate.pose.position.y = (initialYforBlue + offset);
          }
          modelstate.model_name = id;
          modelstate.reference_frame = "world";
          Pose_item.publish(modelstate);
          InputOutput.<String>println((((((((("### Item " + id) + "(") + type) + ") delivered at (") + x) + ",") + y) + ")"));
          boolean _equals_2 = this.sector.equals("sector1");
          if (_equals_2) {
            double _offset = offset;
            offset = (_offset + 0.2);
          } else {
            double _offset_1 = offset;
            offset = (_offset_1 - 0.2);
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
