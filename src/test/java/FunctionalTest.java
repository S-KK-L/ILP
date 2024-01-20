import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.OrderValidator;
import uk.ac.ed.inf.Output.OutputFlightPath;
import uk.ac.ed.inf.Output.OutputOrder;
import uk.ac.ed.inf.PathFinding.PathFindingAlgo;
import uk.ac.ed.inf.Utils.CreditCardInformationDeserializer;
import uk.ac.ed.inf.Utils.NoFlyZone;
import uk.ac.ed.inf.Utils.Utils;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.App;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FunctionalTest extends TestCase {
    public void testSystemLevel() {
        // 2023-9 ~ 2024-1-28
        String [] test_args_date = {"2023-11-20", "2023-10-15", "2023-11-25", "2024-01-10",  "2024-01-15",
                                    "2023-10-01", "2023-12-01", "2023-11-30", "2024-01-01",  "2024-01-14"};

        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < test_args_date.length; ++i) {
            String baseUrl = "https://ilp-rest.azurewebsites.net";
            String [] args = {test_args_date[i], baseUrl};
            App.main(args);
            File toRead = new File("./resultfiles/flightpath-" + test_args_date[i] + ".json");
            OutputFlightPath[] flightPaths = null;
            try {
                flightPaths = objectMapper.readValue(toRead, OutputFlightPath[].class);

                double first_x = 0, first_y = 0;
                double last_x = 0, last_y = 0;
                String curOrderNo = "";

                // to test the system is correct,
                // assert start point and end point equals for the same order
                for (int j = 0; j < flightPaths.length; ++j) {
                    String orderNo = flightPaths[j].getOderNo();
                    if (orderNo.equals(curOrderNo)) {
                        last_x = flightPaths[j].getFromLongitude();
                        last_y = flightPaths[j].getFromLatitude();
                    } else {
                        Assert.assertEquals(last_x, first_x);
                        Assert.assertEquals(last_y, first_y);
                        curOrderNo = flightPaths[j].getOderNo();
                        first_x = flightPaths[j].getFromLongitude();
                        first_y = flightPaths[j].getFromLatitude();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Test passed");
    }

    // module test
    public void testPathFindingAlgo() {
        double [] startPointsLng = {-3.192785, -3.183496, -3.202130, -3.197598,
                                    -3.192642, -3.205881, -3.206784, -3.182572};
        double [] startPointsLat = {55.942891, 55.935584, 55.946064, 55.941246,
                                    55.941042, 55.937646, 55.935953, 55.937077};
        double [] endPointsLng = {-3.201733, -3.191808, -3.203827, -3.192392,
                                    -3.186334, -3.201121, -3.204187, -3.195669};
        double [] endPointsLat = {55.945131, 55.942063, 55.940247, 55.943826,
                                    55.937428, 55.939588, 55.937671, 55.944693};

        for (int i = 0; i < startPointsLng.length; ++i) {
            ObjectMapper objectMapper = new ObjectMapper();
            Restaurant[] restaurantsData = null;
            ObjectMapper objectMapper1 = new ObjectMapper();
            NoFlyZone[] noFlyZonesData = null;
            try {
                restaurantsData = objectMapper.readValue(new File("./smoke_test_restaurant_data.json"), Restaurant[].class);
                noFlyZonesData = objectMapper1.readValue(new File("./functional_test_noflyzones.json"), NoFlyZone[].class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            PathFindingAlgo toTest = new PathFindingAlgo(restaurantsData, noFlyZonesData);
            LngLat StartPoint = new LngLat(startPointsLng[i], startPointsLat[i]);
            LngLat EndPoint = new LngLat(endPointsLng[i], endPointsLat[i]);
            toTest.pathFinding("0", StartPoint, EndPoint);
            List<OutputFlightPath> results = toTest.getResults();
            OutputFlightPath lastPoint = results.get(results.size() - 1);
            Assert.assertEquals(lastPoint.getFromLongitude(), EndPoint.lng());
            Assert.assertEquals(lastPoint.getFromLatitude(), EndPoint.lat());
        }

        System.out.println("Test passed");
    }

    // module test
    public void testOrderValidator() {
        OrderValidator toTest = new OrderValidator();

        // Read order from the rest service
        ObjectMapper objectMapper1 = new ObjectMapper();
        objectMapper1.registerModule(new JavaTimeModule());
        ObjectMapper objectMapper2 = new ObjectMapper();

        // Register a custom deserializer for CreditCardInformation
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CreditCardInformation.class, new CreditCardInformationDeserializer());
        objectMapper1.registerModule(module);

        Order[] ordersData = null;
        Restaurant[] restaurantsData = null;

        OrderValidationCode[] expected = {
                OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID, OrderValidationCode.CVV_INVALID,
                OrderValidationCode.TOTAL_INCORRECT, OrderValidationCode.PIZZA_NOT_DEFINED,
                OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderValidationCode.RESTAURANT_CLOSED,
                OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID, OrderValidationCode.CVV_INVALID,
                OrderValidationCode.TOTAL_INCORRECT, OrderValidationCode.PIZZA_NOT_DEFINED,
                OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderValidationCode.RESTAURANT_CLOSED,
                OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID, OrderValidationCode.CVV_INVALID,
                OrderValidationCode.TOTAL_INCORRECT, OrderValidationCode.PIZZA_NOT_DEFINED,
                OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS,
                OrderValidationCode.RESTAURANT_CLOSED, OrderValidationCode.CARD_NUMBER_INVALID,
                OrderValidationCode.EXPIRY_DATE_INVALID, OrderValidationCode.CVV_INVALID, OrderValidationCode.TOTAL_INCORRECT,
                OrderValidationCode.PIZZA_NOT_DEFINED, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED,
                OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderValidationCode.RESTAURANT_CLOSED,
                OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID,
                OrderValidationCode.CVV_INVALID, OrderValidationCode.TOTAL_INCORRECT, OrderValidationCode.PIZZA_NOT_DEFINED,
                OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS,
                OrderValidationCode.RESTAURANT_CLOSED, OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID,
                OrderValidationCode.CVV_INVALID, OrderValidationCode.TOTAL_INCORRECT,
                OrderValidationCode.PIZZA_NOT_DEFINED, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED,
                OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderValidationCode.RESTAURANT_CLOSED,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
                OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR
        };

        try {
            ordersData = objectMapper1.readValue(new File("./functional_test_data.json"), Order[].class);
            restaurantsData = objectMapper2.readValue(new File("./smoke_test_restaurant_data.json"), Restaurant[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ordersData != null) {
            for (int i = 0; i < ordersData.length; ++i) {
                toTest.validateOrder(ordersData[i], restaurantsData);
                Assert.assertEquals(ordersData[i].getOrderValidationCode(), expected[i]);
            }
        }
        System.out.println("Test passed");
    }

    // Unit test
    public void testUtils() throws IOException {
        String baseUrl = "https://ilp-rest.azurewebsites.net/";
        Order[] OrderResults = Utils.getOrdersFromRestService(baseUrl);
        Assert.assertEquals(OrderResults.length, 8700);
        Restaurant[] RestaurantResults = Utils.getRestaurantsFromRestService(baseUrl);
        Assert.assertEquals(RestaurantResults.length, 7);
        NoFlyZone[] noFlyZones = Utils.getNoFlyZonesFromRestService(baseUrl);
        Assert.assertEquals(noFlyZones.length, 4);
        System.out.println("Test passed");
    }

    // Unit test
    public void testLagLatHandler() {
        LngLatHandler toTest = new LngLatHandler();

        LngLat[] startPositions = {
            new LngLat(29.745907488250266, 89.49656456882596), // 0
            new LngLat(80.42158783238235, 85.00347725602376)  // 1
        };

        LngLat[] endPositions = {
            new LngLat(52.63486667376833, 51.93695619937847), // 2
            new LngLat(24.98750416474676, 11.77379427541217), // 3
            new LngLat(4.601198815857083, 38.949622724948465), // 4
            new LngLat(27.929876007192767, 11.62322760375658), // 5
            new LngLat(6.661288885829961, 80.02454861122943) // 6
        };

        double[] expected = {43.984413528687426, 77.86829537284025, 56.45573225092257, 77.89450930852209,
                             24.952328595146646, 43.19139598966597, 91.84510929554875, 88.71126708477716,
                             90.2221749314868, 73.92815046472305};
        for (int i = 0; i < startPositions.length; ++i) {
            for (int j = 0; j < endPositions.length; ++j) {
                double results = toTest.distanceTo(startPositions[i], endPositions[j]);
                Assert.assertEquals(results, expected[i * endPositions.length + j]);
            }
        }

        LngLat[] startPositions1 = {
            new LngLat(29.745907488250266, 89.49656456882596), // 0
            new LngLat(80.42158783238235, 85.00347725602376), // 1
            new LngLat(4.601198815857082, 38.949622724948465), // 4
            new LngLat(28.0, 11.62322760375658), // 5
            new LngLat(6.661288885829, 80.02454861122943) // 6
        };
        boolean[] expected1 = {
            false, false, true, false, true
        };
        for (int i = 0; i < startPositions1.length; ++i) {
            boolean results = toTest.isCloseTo(startPositions1[i], endPositions[i]);
            Assert.assertEquals(results, expected1[i]);
        }

        double [] angle = { 999, 20.0, 210.3, 303.1, 104.4};
        LngLat [] expected2 = {
            new LngLat(29.745907488250268, 89.49656456882596),
            new LngLat(80.42164904469162, 85.00361419781136),
            new LngLat(4.601051422755795, 38.94965056868551),
            new LngLat(28.00000954720287, 11.623377299617971),
            new LngLat(6.6611768541997876, 80.0244488669862)
        };
        for (int i = 0; i < startPositions1.length; ++i) {
            LngLat results = toTest.nextPosition(startPositions1[i], angle[i]);
            Assert.assertEquals(results, expected2[i]);
        }
        System.out.println("Test passed");
    }

    // integration level test
    public void testIntegrationLevel() throws IOException{
        String baseUrl = "https://ilp-rest.azurewebsites.net/";
        Order[] OrderResults = Utils.getOrdersFromRestService(baseUrl);
        Assert.assertEquals(OrderResults.length, 8700);
        Restaurant[] RestaurantResults = Utils.getRestaurantsFromRestService(baseUrl);
        Assert.assertEquals(RestaurantResults.length, 7);
        NoFlyZone[] noFlyZones = Utils.getNoFlyZonesFromRestService(baseUrl);
        Assert.assertEquals(noFlyZones.length, 4);

        List<OutputOrder> orderResults = new ArrayList<>();
        orderResults.add(new OutputOrder("1111", OrderStatus.INVALID, OrderValidationCode.NO_ERROR, 1231));
        orderResults.add(new OutputOrder("1111", OrderStatus.INVALID, OrderValidationCode.NO_ERROR, 1231));
        Utils.writeOrderResults("2022-12-31", orderResults);
        File toTest = new File("./resultfiles/deliveries-2022-12-31.json");
        Assert.assertTrue(toTest.exists());

        List<OutputFlightPath> flightPathsResults = new ArrayList<>();
        flightPathsResults.add(new OutputFlightPath());
        File toTest1 = new File("./resultfiles/flightpath-2022-12-31.json");
        Utils.writeOutputFlightPathResults("2022-12-31", flightPathsResults);
        Assert.assertTrue(toTest1.exists());

        File toTest2 = new File("./resultfiles/drone-2022-12-31.geojson");
        Utils.writeGeojsonResults("2022-12-31", flightPathsResults);
        Assert.assertTrue(toTest2.exists());

        System.out.println("Test passed");
    }
}
