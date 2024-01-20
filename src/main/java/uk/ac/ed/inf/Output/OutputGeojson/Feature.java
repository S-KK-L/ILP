package uk.ac.ed.inf.Output.OutputGeojson;

import uk.ac.ed.inf.Output.OutputFlightPath;

import java.util.List;

public class Feature {
    public String type;
    public Geometry geometry;
    public Property properties;
    // Constructor of the Feature class, takes a list of OutputFlightPath objects as an argument
    Feature(List<OutputFlightPath> flightPathsResults) {
        type = "Feature";
        // Create a new Geometry object using the flightPathsResults list as an argument
        geometry = new Geometry(flightPathsResults);
        properties = new Property();
    }
}
