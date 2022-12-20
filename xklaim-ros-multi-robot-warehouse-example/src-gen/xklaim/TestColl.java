package xklaim;

import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import messages.PoseStamped;
import messages.XklaimToRosConnection;
import org.mikado.imc.common.IMCException;
import ros.Publisher;

@SuppressWarnings("all")
public class TestColl extends LogicalNet {
  private static final LogicalLocality DeliveryRobot1 = new LogicalLocality("DeliveryRobot1");
  
  public static class DeliveryRobot1 extends ClientNode {
    private static class DeliveryRobot1Process extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        final String robotId = "robot1";
        final double x = 1.0;
        final double y = 1.0;
        final XklaimToRosConnection bridge = new XklaimToRosConnection(rosbridgeWebsocketURI);
        final Publisher pub = new Publisher((("/" + robotId) + "/move_base_simple/goal"), "geometry_msgs/PoseStamped", bridge);
        final PoseStamped destination = new PoseStamped().headerFrameId("world").posePositionXY(x, y).poseOrientation(1.0);
        pub.publish(destination);
      }
    }
    
    public DeliveryRobot1() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot1"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new TestColl.DeliveryRobot1.DeliveryRobot1Process());
    }
  }
  
  public TestColl() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    TestColl.DeliveryRobot1 deliveryRobot1 = new TestColl.DeliveryRobot1();
    deliveryRobot1.addMainProcess();
  }
}
