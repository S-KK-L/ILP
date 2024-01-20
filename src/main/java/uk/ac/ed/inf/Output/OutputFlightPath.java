package uk.ac.ed.inf.Output;

public class OutputFlightPath {
    private String orderNo;
    private double fromLongitude;
    private double fromLatitude;
    private double angle;
    private double toLongitude;
    private double toLatitude;

    public OutputFlightPath() {

    }
    // Parameterized constructor for initializing an OutputFlightPath object
    public OutputFlightPath(String orderNo, double fromLongitude, double fromLatitude, double angle,
                            double toLongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
    }

    public void setOderNo(String oderNo) {
        this.orderNo = oderNo;
    }

    public String getOderNo() {
        return orderNo;
    }

    public void setFromLatitude(double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLongitude(double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public double getFromLongitude() {
        return fromLongitude;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLatitude() {
        return toLatitude;
    }
}
