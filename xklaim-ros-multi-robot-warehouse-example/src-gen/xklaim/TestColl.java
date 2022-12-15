package xklaim;

import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.Tuple;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.mikado.imc.common.IMCException;
import xklaim.arm.GetDown;
import xklaim.arm.Grip;

@SuppressWarnings("all")
public class TestColl extends LogicalNet {
  private static final LogicalLocality Arm = new LogicalLocality("Arm");
  
  public static class Arm extends ClientNode {
    private static class ArmProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        GetDown _getDown = new GetDown(rosbridgeWebsocketURI, Double.valueOf(0.583518), Double.valueOf(0.0));
        eval(_getDown, this.self);
        Grip _grip = new Grip(rosbridgeWebsocketURI);
        eval(_grip, this.self);
        in(new Tuple(new Object[] {"gripCompleted"}), this.self);
        InputOutput.<String>println("Evviva!");
      }
    }
    
    public Arm() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("Arm"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new TestColl.Arm.ArmProcess());
    }
  }
  
  public TestColl() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    TestColl.Arm arm = new TestColl.Arm();
    arm.addMainProcess();
  }
}
