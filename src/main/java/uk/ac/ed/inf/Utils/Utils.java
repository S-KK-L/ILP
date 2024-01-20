package uk.ac.ed.inf.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.Output.OutputFlightPath;
import uk.ac.ed.inf.Output.OutputGeojson.GeojsonResult;
import uk.ac.ed.inf.Output.OutputOrder;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Utils {
    public static final String restaurantsIdx = "./tmp_restaurants.txt";

    public static Order[] getOrdersFromRestService(String baseUrl) throws IOException {
        // Urls
        URL orderUrl = new URL(baseUrl + "orders");

        // Read order from the rest service
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Register a custom deserializer for CreditCardInformation
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CreditCardInformation.class, new CreditCardInformationDeserializer());
        objectMapper.registerModule(module);
        // Read and return orders from the REST service
        return objectMapper.readValue(
                orderUrl, Order[].class);
    }
    // Method to retrieve restaurant data from a REST service
    public static Restaurant[] getRestaurantsFromRestService(String baseUrl) throws IOException {
        // read restaurants from the rest service
        URL restaurantsUrl = new URL(baseUrl + "restaurants");
        return new ObjectMapper().readValue(
                restaurantsUrl, Restaurant[].class);
    }

    public static NoFlyZone[] getNoFlyZonesFromRestService(String baseUrl) throws IOException {
        // read restaurants from the rest service
        URL noFlyZonesUrl = new URL(baseUrl + "noFlyZones");
        return new ObjectMapper().readValue(
                noFlyZonesUrl, NoFlyZone[].class);
    }
    // Method to write order results to a JSON file
    public static void writeOrderResults(String date, List<OutputOrder> orderResults) throws IOException {
        String resultFolder = "./resultfiles";
        Path path = Paths.get(resultFolder);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Setup ObjectMapper for writing JSON with indentation
        ObjectMapper objectMapperForOrder = new ObjectMapper();
        objectMapperForOrder.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapperForOrder.writeValue(new File(resultFolder + "/deliveries-" + date + ".json"),
                orderResults);
    }
    // Method to write flight path results to a JSON file
    public static void writeOutputFlightPathResults(String date, List<OutputFlightPath> flightPathsResults) throws IOException {
        String resultFolder = "./resultfiles";
        Path path = Paths.get(resultFolder);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ObjectMapper objectMapperForFlightPath = new ObjectMapper();
        objectMapperForFlightPath.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapperForFlightPath.writeValue(new File(resultFolder + "/flightpath-" + date + ".json"),
                flightPathsResults);
    }
    // Method to write GeoJSON results to a file
    public static void writeGeojsonResults(String date, List<OutputFlightPath> flightPathsResults) throws IOException {
        String resultFolder = "./resultfiles";
        Path path = Paths.get(resultFolder);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ObjectMapper objectMapperForGeojsonResults = new ObjectMapper();
        objectMapperForGeojsonResults.enable(SerializationFeature.INDENT_OUTPUT);

        GeojsonResult results = new GeojsonResult(flightPathsResults);
        objectMapperForGeojsonResults.writeValue(new File(resultFolder + "/drone-" + date + ".geojson"), results);
    }
}
