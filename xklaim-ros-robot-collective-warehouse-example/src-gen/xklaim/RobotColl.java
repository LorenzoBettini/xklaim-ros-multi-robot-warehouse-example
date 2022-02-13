package xklaim;

import java.util.Collections;
import java.util.List;
import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.Tuple;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.mikado.imc.common.IMCException;
import xklaim.arm.Disappear;
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
            String itemType = null;
            List<Double> firstTrajectoryPositions = null;
            List<Double> secondTrajectoryPositions = null;
            Tuple _Tuple = new Tuple(new Object[] {"item", String.class, List.class, List.class});
            in(_Tuple, this.self);
            itemType = (String) _Tuple.getItem(1);
            firstTrajectoryPositions = (List) _Tuple.getItem(2);
            secondTrajectoryPositions = (List) _Tuple.getItem(3);
            GetDown _getDown = new GetDown(rosbridgeWebsocketURI, firstTrajectoryPositions, secondTrajectoryPositions);
            eval(_getDown, this.self);
            Grip _grip = new Grip(rosbridgeWebsocketURI);
            eval(_grip, this.self);
            GetUp _getUp = new GetUp(rosbridgeWebsocketURI);
            eval(_getUp, this.self);
            Rotate _rotate = new Rotate(rosbridgeWebsocketURI);
            eval(_rotate, this.self);
            Lay _lay = new Lay(rosbridgeWebsocketURI);
            eval(_lay, this.self);
            Release _release = new Release(rosbridgeWebsocketURI, itemType);
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
        while (true) {
          {
            in(new Tuple(new Object[] {"availableForDelivery"}), this.self);
            MoveToArm _moveToArm = new MoveToArm(rosbridgeWebsocketURI, robotId, RobotColl.Arm);
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
        while (true) {
          {
            in(new Tuple(new Object[] {"availableForDelivery"}), this.self);
            MoveToArm _moveToArm = new MoveToArm(rosbridgeWebsocketURI, robotId, RobotColl.Arm);
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
        out(new Tuple(new Object[] {"item", "typeA", Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412)))), Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))))}), RobotColl.Arm);
        out(new Tuple(new Object[] {"item", "typeB", Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412)))), Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))))}), RobotColl.Arm);
        out(new Tuple(new Object[] {"item", "typeA", Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412)))), Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))))}), RobotColl.Arm);
        out(new Tuple(new Object[] {"item", "typeB", Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412)))), Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))))}), RobotColl.Arm);
        out(new Tuple(new Object[] {"type2destination", "typeA", 12.0, 34.0}), RobotColl.DeliveryRobot1);
        out(new Tuple(new Object[] {"type2destination", "typeB", 67.0, 14.0}), RobotColl.DeliveryRobot1);
        out(new Tuple(new Object[] {"type2destination", "typeA", 12.0, 34.0}), RobotColl.DeliveryRobot2);
        out(new Tuple(new Object[] {"type2destination", "typeB", 134.0, 49.0}), RobotColl.DeliveryRobot2);
        out(new Tuple(new Object[] {"initialPosition"}), RobotColl.Arm);
        out(new Tuple(new Object[] {"availableForDelivery"}), RobotColl.DeliveryRobot1);
        out(new Tuple(new Object[] {"availableForDelivery"}), RobotColl.DeliveryRobot2);
        Disappear _disappear = new Disappear(rosbridgeWebsocketURI, RobotColl.DeliveryRobot1);
        eval(_disappear, this.self);
        Disappear _disappear_1 = new Disappear(rosbridgeWebsocketURI, RobotColl.DeliveryRobot2);
        eval(_disappear_1, this.self);
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
