package uk.ac.ed.inf.Output.OutputGeojson;

import uk.ac.ed.inf.Output.OutputFlightPath;

import java.util.ArrayList;
import java.util.List;

public class Geometry {
    public String type;
    public List<List<Double>> coordinates;

    // Iterate through each OutputFlightPath object in the flightPathsResults list
    Geometry(List<OutputFlightPath> flightPathsResults) {
        type = "LineString";
        coordinates = new ArrayList<>();
        for (OutputFlightPath point: flightPathsResults) {
            // Create a new list to hold the current point's longitude and latitude
            List<Double> cur_point = new ArrayList<>();
            // Add the longitude of the current point
            cur_point.add(point.getFromLongitude());
            // Add the latitude of the current point
            cur_point.add(point.getFromLatitude());
            // Add the current point to the coordinates list
            coordinates.add(cur_point);
        }
    }
}
