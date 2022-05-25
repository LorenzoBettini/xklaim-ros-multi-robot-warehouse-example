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
import xklaim.arm.Release;
import xklaim.arm.Rotate;
import xklaim.deliveryRobot.DeliverItem;
import xklaim.deliveryRobot.MoveToArm;

@SuppressWarnings("all")
public class MRS extends LogicalNet {
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
      addNodeCoordinator(new MRS.Arm.ArmProcess());
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
            MoveToArm _moveToArm = new MoveToArm(rosbridgeWebsocketURI, robotId, sector, MRS.Arm);
            eval(_moveToArm, this.self);
            DeliverItem _deliverItem = new DeliverItem(rosbridgeWebsocketURI, robotId, MRS.Arm);
            eval(_deliverItem, this.self);
          }
        }
      }
    }
    
    public DeliveryRobot1() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot1"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new MRS.DeliveryRobot1.DeliveryRobot1Process());
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
            MoveToArm _moveToArm = new MoveToArm(rosbridgeWebsocketURI, robotId, sector, MRS.Arm);
            eval(_moveToArm, this.self);
            DeliverItem _deliverItem = new DeliverItem(rosbridgeWebsocketURI, robotId, MRS.Arm);
            eval(_deliverItem, this.self);
          }
        }
      }
    }
    
    public DeliveryRobot2() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot2"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new MRS.DeliveryRobot2.DeliveryRobot2Process());
    }
  }
  
  public static class SimuationHandler extends ClientNode {
    private static class SimuationHandlerProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        out(new Tuple(new Object[] {"item", "item1", "sector1", "red", 0.583518, 0.0}), MRS.Arm);
        out(new Tuple(new Object[] {"item", "item2", "sector2", "blue", 0.554542, 0.187360}), MRS.Arm);
        out(new Tuple(new Object[] {"item", "item3", "sector2", "red", 0.504, 0.307}), MRS.Arm);
        out(new Tuple(new Object[] {"item", "item4", "sector1", "blue", 0.332977, 0.470854}), MRS.Arm);
        out(new Tuple(new Object[] {"type2destination", "red", (-9.0), (-9.0)}), MRS.DeliveryRobot1);
        out(new Tuple(new Object[] {"type2destination", "blue", 9.0, (-9.0)}), MRS.DeliveryRobot1);
        out(new Tuple(new Object[] {"type2destination", "red", 9.0, 9.0}), MRS.DeliveryRobot2);
        out(new Tuple(new Object[] {"type2destination", "blue", (-9.0), 9.0}), MRS.DeliveryRobot2);
        out(new Tuple(new Object[] {"initialPosition"}), MRS.Arm);
        out(new Tuple(new Object[] {"availableForDelivery"}), MRS.DeliveryRobot1);
        out(new Tuple(new Object[] {"availableForDelivery"}), MRS.DeliveryRobot2);
        PickUp _pickUp = new PickUp(rosbridgeWebsocketURI, MRS.DeliveryRobot1, (-9.0), (-9.0));
        eval(_pickUp, this.self);
        PickUp _pickUp_1 = new PickUp(rosbridgeWebsocketURI, MRS.DeliveryRobot1, 9.0, (-9.0));
        eval(_pickUp_1, this.self);
        PickUp _pickUp_2 = new PickUp(rosbridgeWebsocketURI, MRS.DeliveryRobot2, (-9.0), 9.0);
        eval(_pickUp_2, this.self);
        PickUp _pickUp_3 = new PickUp(rosbridgeWebsocketURI, MRS.DeliveryRobot2, 9.0, 9.0);
        eval(_pickUp_3, this.self);
      }
    }
    
    public SimuationHandler() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("SimuationHandler"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new MRS.SimuationHandler.SimuationHandlerProcess());
    }
  }
  
  public MRS() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    MRS.Arm arm = new MRS.Arm();
    MRS.DeliveryRobot1 deliveryRobot1 = new MRS.DeliveryRobot1();
    MRS.DeliveryRobot2 deliveryRobot2 = new MRS.DeliveryRobot2();
    MRS.SimuationHandler simuationHandler = new MRS.SimuationHandler();
    arm.addMainProcess();
    deliveryRobot1.addMainProcess();
    deliveryRobot2.addMainProcess();
    simuationHandler.addMainProcess();
  }
}
