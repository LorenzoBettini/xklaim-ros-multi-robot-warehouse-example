package messages;

public class ContactState {
	public String info;
	public String collision1_name;
	public String collision2_name;
    public Wrench[] wrenches = new Wrench[1];
    public Wrench total_wrench = new Wrench();
    public Vector3[] contact_positions = new Vector3[1];
    public Vector3[] contact_normals = new Vector3[1];
    public double[] depths;
    
   public ContactState(String info, String collision1_name, String collision2_name, Wrench[] wrenches, Wrench total_wrench, Vector3[] contact_positions, Vector3[] contact_normals, double[] depths){
        this.info = info;
        this.collision1_name = collision1_name;
        this.collision2_name = collision2_name;
        this.wrenches = wrenches;
        this.total_wrench = total_wrench;
        this.contact_positions = contact_positions;
        this.contact_normals = contact_normals;
        this.depths=depths;
        

    }
    public ContactState(){}
}
