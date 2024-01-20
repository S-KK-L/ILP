package uk.ac.ed.inf.PathFinding;

import uk.ac.ed.inf.ilp.data.LngLat;

public class Vector {
    public double x;
    public double y;

    // Constructor to create a vector from two geographical points (LngLat objects)
    public Vector(LngLat Point1, LngLat Point2) {
        x = Point2.lng() - Point1.lng();
        y = Point2.lat() - Point1.lat();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Method to calculate the cross product of this vector with another vector
    // The cross product is a determinant useful in various geometric calculations
    double crossProduct(Vector other) { // 叉乘
        return x * other.getY() - other.getX() * y;
    }
}
