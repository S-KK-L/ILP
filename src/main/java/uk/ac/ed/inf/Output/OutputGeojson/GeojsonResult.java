package uk.ac.ed.inf.Output.OutputGeojson;

import uk.ac.ed.inf.Output.OutputFlightPath;

import java.util.ArrayList;
import java.util.List;

public class GeojsonResult {
    public String type;

    public List<Feature> features;

    // Constructor of the GeojsonResult class, takes a list of OutputFlightPath objects as an argument
    public GeojsonResult(List<OutputFlightPath> flightPathsResults) {
        // Set the type of the GeoJSON object to "FeatureCollection"
        type = "FeatureCollection";
        features = new ArrayList<>();
        // Add a new Feature object to the features list, using the flightPathsResults list as an argument
        features.add(new Feature(flightPathsResults));
    }
}
