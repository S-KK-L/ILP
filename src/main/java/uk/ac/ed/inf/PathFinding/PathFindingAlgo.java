package uk.ac.ed.inf.PathFinding;

import uk.ac.ed.inf.Utils.Direction;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.Utils.NoFlyZone;
import uk.ac.ed.inf.Output.OutputFlightPath;
import uk.ac.ed.inf.Utils.Utils;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
// Define a public class named PathFindingAlgo for pathfinding algorithms
public class PathFindingAlgo {
    private Restaurant[] restaurants;
    private NoFlyZone[] noFlyZones;

    List<OutputFlightPath> results;
    // Constructor to initialize the PathFindingAlgo with restaurants and no-fly zones
    public PathFindingAlgo(Restaurant[] outer_restaurants, NoFlyZone[] outer_noFlyZones) {
        restaurants = outer_restaurants;
        noFlyZones = outer_noFlyZones;
        results = new ArrayList<>();
    }

    // Method to get the results of the pathfinding
    public List<OutputFlightPath> getResults() {
        return results;
    }
    public List<OutputFlightPath> parseStartEnd() {
        List<OutputFlightPath> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Utils.restaurantsIdx))) {
            String line;
            LngLat startPoint = new LngLat(-3.186874, 55.944494);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String orderNo = parts[0];
                System.out.println("orderNo " + orderNo);
                int value = Integer.parseInt(parts[1]);

                LngLat endPoint = restaurants[value].location();

                pathFinding(orderNo, startPoint, endPoint);
                // hover once
                results.add(new OutputFlightPath(orderNo, endPoint.lng(), endPoint.lat(), Direction.hovering, endPoint.lng(), endPoint.lat()));

                pathFinding(orderNo, endPoint, startPoint); // return back
                // hover once
                results.add(new OutputFlightPath(orderNo, startPoint.lng(), startPoint.lat(), Direction.hovering, startPoint.lng(), startPoint.lat()));

                System.out.println("startPoint: " + startPoint.lng() + " " + startPoint.lat() +
                        ", endPoint: " + endPoint.lng() + " " + endPoint.lat());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    // judge if point is in the noFlyZones
    public boolean isHeadingToObstacles(LngLat CurPoint, LngLat NextPoint) {
        for (int i = 0; i < noFlyZones.length; ++i) {
            if (isHeadingToOneObstacle(noFlyZones[i], CurPoint, NextPoint)) {
                return true;
            }
        }
        return  false;
    }

    // https://segmentfault.com/a/1190000004457595
    // judge if two line segments cross
    public boolean isHeadingToOneObstacle(NoFlyZone poly, LngLat CurPoint, LngLat NextPoint) {
        LngLat[] vertices = poly.getVertices();
        int size = vertices.length;

        // cur_Point: A, NextPoint: B, vertices[i]: C, vertices[j]: D
        for (int i = 0; i + 1 < size; i++) {
            Vector AC = new Vector(CurPoint, vertices[i]);
            Vector AD = new Vector(CurPoint, vertices[i + 1]);
            Vector BC = new Vector(NextPoint, vertices[i]);
            Vector BD = new Vector(NextPoint, vertices[i + 1]);

            Vector CA = new Vector(vertices[i], CurPoint);
            Vector CB = new Vector(vertices[i], NextPoint);
            Vector DA = new Vector(vertices[i + 1], CurPoint);
            Vector DB = new Vector(vertices[i + 1], NextPoint);

            // two lines cross, cross products less than zero
            if ((AC.crossProduct(AD) * BC.crossProduct(BD)) <= 0 && (CA.crossProduct(CB) * DA.crossProduct(DB)) <= 0) {
                return true;
            }
        }
        return false;

        // judge if next point is in the obstacle
//        boolean flag = false;
//        for (int i = 0; i + 1 < size; i++) {
//            double x = Point.lng(), y = Point.lat();
//           double x1 = vertices[i].lng(), y1 = vertices[i].lat(); // A l2.x1
//            double x2 = vertices[i+1].lng(), y2 = vertices[i+1].lat(); // B l2.x2
//            if ((y1 < y && y2 >= y) || (y1 >= y && y2 < y)) { // two endpoints lines one the different side of the ray
//                double cross_point_x = (y - y1) * (x2 - x1) / (y2 - y1) + x1;
//
//                if (cross_point_x == x) { // point lies on the edge
//                    return true;
//                }
//
//                if (cross_point_x > x) { // the ray crosses one edge
//                    flag = !flag;
//                }
//            }
//        }
//        return false;
    }
    // Method to check if an angle is within a specified range
    public boolean isInRange(double angle, double lastAngle, double lastAngle_added) {
        double max = Math.max(lastAngle, lastAngle_added);
        double min = Math.min(lastAngle, lastAngle_added);
        return (angle >= min) && (angle <= max);
    }

    // iteration 16 directions, find the direction that is closest to the target.
    // keep directions until distance to the target is becoming larger or heading to the obstacle.
    public List<OutputFlightPath> pathFinding(String orderNo, LngLat StartPoint, LngLat EndPoint) {
//        EndPoint = new LngLat(-3.1869, 55.9445);
//        StartPoint = new LngLat(-3.2025, 55.9433);
        int maxWhileCounts = 5000;
        LngLat curPoint = StartPoint, nextPoint = StartPoint;
        LngLatHandler handler = new LngLatHandler();
        double lastAngle = -1;
        System.out.println("Drone is at start point: " + StartPoint.lng() + ", " + StartPoint.lat());
        System.out.println("Drone is heading to point: " + EndPoint.lng() + ", " + EndPoint.lat());
        // Loop until the drone is close to the endpoint
        int cnt = 0;
        while (cnt < maxWhileCounts && !handler.isCloseTo(curPoint, EndPoint)) {
            cnt += 1;
            double minDistance = Double.MAX_VALUE;
            // find in range [lastAngle, (lastAngle + 90) % 360] and range [lastAngle, (lastAngle + 270) % 360]
            for (double angle: Direction.directions) {
                if (lastAngle == -1 || isInRange(angle, lastAngle, (lastAngle + 90) % 360) || isInRange(angle, lastAngle, (lastAngle + 270) % 360)) {
                    LngLat tmpNextPoint = handler.nextPosition(curPoint, angle);
                    double tmpDistance = handler.distanceTo(tmpNextPoint, EndPoint);
                    // Check if the next point is not heading towards obstacles and is closer to the endpoint
                    if (!isHeadingToObstacles(curPoint, tmpNextPoint) && (tmpDistance <= minDistance)) {
                        lastAngle = angle;
                        nextPoint = tmpNextPoint;
                        minDistance = tmpDistance;
                    }
                }
            }
            // Add the calculated flight path to the results
            results.add(new OutputFlightPath(orderNo, curPoint.lng(), curPoint.lat(), lastAngle, nextPoint.lng(), nextPoint.lat()));
            curPoint = nextPoint;
            System.out.println("Drone is at point: " + curPoint.lng() + ", " + curPoint.lat());
        }
        // Add the final endpoint to the results
        if (cnt != maxWhileCounts) {
            results.add(new OutputFlightPath(orderNo, EndPoint.lng(), EndPoint.lat(), lastAngle, EndPoint.lng(), EndPoint.lat()));
        }
        System.out.println("Drone is at end point: " + EndPoint.lng() + ", " + EndPoint.lat());
        System.out.println("-----------------------------------------------------------------");
        return results;
    }
}
