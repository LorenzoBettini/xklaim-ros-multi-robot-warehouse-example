package xklaim;

import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.Tuple;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.mikado.imc.common.IMCException;
import xklaim.arm.GetDown;
import xklaim.arm.GetUp;
import xklaim.arm.GoToInitialPosition;
import xklaim.arm.Grip;
import xklaim.arm.Lay;
import xklaim.arm.PickUp;
import xklaim.arm.Release;
import xklaim.arm.Rotate;
import xklaim.deliveryRobot.DeliverItem;
import xklaim.deliveryRobot.MoveToArm;

/**
 * LIST OF TODOS:
 * - rename item identifiers (e.g. idem1 in item1) in xklaim and gazebo code
 * - check the position and orientation of item2 in the gazebo scenario in order to avoid problems with the gripper
 */
@SuppressWarnings("all")
public class RobotColl extends LogicalNet {
  private static final LogicalLocality Arm = new LogicalLocality("Arm");
  
  private static final LogicalLocality DeliveryRobot1 = new LogicalLocality("DeliveryRobot1");
  
  private static final LogicalLocality DeliveryRobot2 = new LogicalLocality("DeliveryRobot2");
  
  private static final LogicalLocality SimuationHandler = new LogicalLocality("SimuationHandler");
  
  public static class Arm extends ClientNode {
    private static class ArmProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        while (true) {
          {
            in(new Tuple(new Object[] {"initialPosition"}), this.self);
            String itemId = null;
            String sector = null;
            String itemType = null;
            Double x = null;
            Double y = null;
            Tuple _Tuple = new Tuple(new Object[] {"item", String.class, String.class, String.class, Double.class, Double.class});
            in(_Tuple, this.self);
            itemId = (String) _Tuple.getItem(1);
            sector = (String) _Tuple.getItem(2);
            itemType = (String) _Tuple.getItem(3);
            x = (Double) _Tuple.getItem(4);
            y = (Double) _Tuple.getItem(5);
            GetDown _getDown = new GetDown(rosbridgeWebsocketURI, x, y);
            eval(_getDown, this.self);
            Grip _grip = new Grip(rosbridgeWebsocketURI);
            eval(_grip, this.self);
            GetUp _getUp = new GetUp(rosbridgeWebsocketURI, x, y);
            eval(_getUp, this.self);
            Rotate _rotate = new Rotate(rosbridgeWebsocketURI, sector);
            eval(_rotate, this.self);
            Lay _lay = new Lay(rosbridgeWebsocketURI);
            eval(_lay, this.self);
            Release _release = new Release(rosbridgeWebsocketURI, itemId, itemType);
            eval(_release, this.self);
            GoToInitialPosition _goToInitialPosition = new GoToInitialPosition(rosbridgeWebsocketURI);
            eval(_goToInitialPosition, this.self);
          }
        }
      }
    }
    
    public Arm() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("Arm"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.Arm.ArmProcess());
    }
  }
  
  public static class DeliveryRobot1 extends ClientNode {
    private static class DeliveryRobot1Process extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        final String robotId = "robot1";
        final String sector = "sector1";
        while (true) {
          {
            in(new Tuple(new Object[] {"availableForDelivery"}), this.self);
            MoveToArm _moveToArm = new MoveToArm(rosbridgeWebsocketURI, robotId, sector, RobotColl.Arm);
            eval(_moveToArm, this.self);
            DeliverItem _deliverItem = new DeliverItem(rosbridgeWebsocketURI, robotId, RobotColl.Arm);
            eval(_deliverItem, this.self);
          }
        }
      }
    }
    
    public DeliveryRobot1() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot1"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.DeliveryRobot1.DeliveryRobot1Process());
    }
  }
  
  public static class DeliveryRobot2 extends ClientNode {
    private static class DeliveryRobot2Process extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        final String robotId = "robot2";
        final String sector = "sector2";
        while (true) {
          {
            in(new Tuple(new Object[] {"availableForDelivery"}), this.self);
            MoveToArm _moveToArm = new MoveToArm(rosbridgeWebsocketURI, robotId, sector, RobotColl.Arm);
            eval(_moveToArm, this.self);
            DeliverItem _deliverItem = new DeliverItem(rosbridgeWebsocketURI, robotId, RobotColl.Arm);
            eval(_deliverItem, this.self);
          }
        }
      }
    }
    
    public DeliveryRobot2() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot2"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.DeliveryRobot2.DeliveryRobot2Process());
    }
  }
  
  public static class SimuationHandler extends ClientNode {
    private static class SimuationHandlerProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        out(new Tuple(new Object[] {"item", "idem1", "sector1", "red", 0.583518, 0.0}), RobotColl.Arm);
        out(new Tuple(new Object[] {"item", "idem3", "sector2", "red", 0.504, 0.307}), RobotColl.Arm);
        out(new Tuple(new Object[] {"item", "idem4", "sector2", "blue", 0.332977, 0.470854}), RobotColl.Arm);
        out(new Tuple(new Object[] {"type2destination", "red", (-9.0), (-9.0)}), RobotColl.DeliveryRobot1);
        out(new Tuple(new Object[] {"type2destination", "blue", 9.0, (-9.0)}), RobotColl.DeliveryRobot1);
        out(new Tuple(new Object[] {"type2destination", "red", 9.0, 9.0}), RobotColl.DeliveryRobot2);
        out(new Tuple(new Object[] {"type2destination", "blue", (-9.0), 9.0}), RobotColl.DeliveryRobot2);
        out(new Tuple(new Object[] {"initialPosition"}), RobotColl.Arm);
        out(new Tuple(new Object[] {"availableForDelivery"}), RobotColl.DeliveryRobot1);
        out(new Tuple(new Object[] {"availableForDelivery"}), RobotColl.DeliveryRobot2);
        PickUp _pickUp = new PickUp(rosbridgeWebsocketURI, RobotColl.DeliveryRobot1);
        eval(_pickUp, this.self);
        PickUp _pickUp_1 = new PickUp(rosbridgeWebsocketURI, RobotColl.DeliveryRobot2);
        eval(_pickUp_1, this.self);
      }
    }
    
    public SimuationHandler() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("SimuationHandler"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.SimuationHandler.SimuationHandlerProcess());
    }
  }
  
  public RobotColl() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    RobotColl.Arm arm = new RobotColl.Arm();
    RobotColl.DeliveryRobot1 deliveryRobot1 = new RobotColl.DeliveryRobot1();
    RobotColl.DeliveryRobot2 deliveryRobot2 = new RobotColl.DeliveryRobot2();
    RobotColl.SimuationHandler simuationHandler = new RobotColl.SimuationHandler();
    arm.addMainProcess();
    deliveryRobot1.addMainProcess();
    deliveryRobot2.addMainProcess();
    simuationHandler.addMainProcess();
  }
}
