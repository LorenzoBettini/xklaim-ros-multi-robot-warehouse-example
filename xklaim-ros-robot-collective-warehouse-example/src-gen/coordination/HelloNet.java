package coordination;

import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.mikado.imc.common.IMCException;

/**
 * This is an example Xklaim application.
 * 
 * Right click on the file and select "Run As" -> "Xklaim Application".
 */
@SuppressWarnings("all")
public class HelloNet extends LogicalNet {
  private static final LogicalLocality Hello = new LogicalLocality("Hello");
  
  public static class Hello extends ClientNode {
    private static class HelloProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        InputOutput.<String>println("Hello World!");
        System.exit(0);
      }
    }
    
    public Hello() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("Hello"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new HelloNet.Hello.HelloProcess());
    }
  }
  
  public HelloNet() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    HelloNet.Hello hello = new HelloNet.Hello();
    hello.addMainProcess();
  }
}
