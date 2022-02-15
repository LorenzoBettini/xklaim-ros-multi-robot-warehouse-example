package messages;

public class PoseWithCovariance {
    public Pose pose = new Pose();
    public double[] covariance;

   public PoseWithCovariance(Pose pose, double[] covariance){
        this.pose = pose;
        this.covariance = covariance;

    }
    public PoseWithCovariance(){this.covariance = new double[36];}
}
