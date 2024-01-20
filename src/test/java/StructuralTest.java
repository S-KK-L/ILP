import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ed.inf.App;
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
import uk.ac.ed.inf.ilp.data.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StructuralTest extends TestCase {
    public void testSystemLevel() {
        // 2023-9 ~ 2024-1-28
        String [] test_args_date = {"2023-11-31", "2023-10-18", "2023-09-21"};

        String [] args0 = new String[0];
        int result = App.main(args0);
        Assert.assertEquals(result, 1);

        String [] args1 = {test_args_date[0]};
        result = App.main(args1);
        Assert.assertEquals(result, 1);

        String [] args2 = {test_args_date[1], test_args_date[2]};
        result = App.main(args2);
        Assert.assertEquals(result, 2);

        String baseUrl = "https://ilp-rest.azurewebsites.net";
        String [] args3 = {test_args_date[1], baseUrl, test_args_date[2]};
        result = App.main(args3);
        Assert.assertEquals(result, 1);

        baseUrl = "https://ilp-rest.azurewebsites.net/";
        String [] args4 = {test_args_date[1], baseUrl};
        result = App.main(args4);
        Assert.assertEquals(result, 0);

        String [] args5 = {"2023-04-15", baseUrl};
        result = App.main(args5);
        Assert.assertEquals(result, 0);

        System.out.println("Test passed");
    }

    // testPathFinding
    // startPoint or Endpoint in the obstacles
    // cause endless while
    public void testPathFindingAlgo() {
        LngLat ErrorStartPoint = new LngLat(-3.1894867087890906, 55.94388768855305);
        LngLat endPoint = new LngLat(-3.1869793929712187, 55.94295726972755);

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
        LngLat StartPoint = ErrorStartPoint;
        LngLat EndPoint = endPoint;
        toTest.pathFinding("0", StartPoint, EndPoint);
        List<OutputFlightPath> results = toTest.getResults();
        OutputFlightPath lastPoint = results.get(results.size() - 1);
        Assert.assertNotSame(lastPoint.getFromLongitude(), EndPoint.lng());
        Assert.assertNotSame(lastPoint.getFromLatitude(), EndPoint.lat());

        LngLat startPoint = new LngLat(-3.184842576097587, 55.943298129252895);
        LngLat ErrorEndPoint = new LngLat(-3.188022527388639, 55.94382183772736);
        StartPoint = startPoint;
        EndPoint = ErrorEndPoint;
        toTest.pathFinding("0", StartPoint, EndPoint);
        results = toTest.getResults();
        lastPoint = results.get(results.size() - 1);
        Assert.assertNotSame(lastPoint.getFromLongitude(), EndPoint.lng());
        Assert.assertNotSame(lastPoint.getFromLatitude(), EndPoint.lat());

        System.out.println("Test passed");
    }

    // order date not match the pattern
    public void testFindValidateOrders() {
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
                OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID
        };

        try {
            ordersData = objectMapper1.readValue(new File("./structural_test_data.json"), Order[].class);
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


    public static void deleteFolder(File folder) {
        File [] files = folder.listFiles();
        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    // test delete to increase test coverage
    public void testUtils() throws IOException {
        String resultFolder = "./resultfiles";
        File toTest = new File(resultFolder);
        deleteFolder(toTest);
        List<OutputOrder> orderResults = new ArrayList<>();
        orderResults.add(new OutputOrder("1111", OrderStatus.INVALID, OrderValidationCode.NO_ERROR, 1231));
        orderResults.add(new OutputOrder("1111", OrderStatus.INVALID, OrderValidationCode.NO_ERROR, 1231));
        Utils.writeOrderResults("2022-12-31", orderResults);
        File toTest1 = new File("./resultfiles/deliveries-2022-12-31.json");
        Assert.assertTrue(toTest1.exists());

        deleteFolder(toTest);
        List<OutputFlightPath> flightPathsResults = new ArrayList<>();
        flightPathsResults.add(new OutputFlightPath());
        File toTest2 = new File("./resultfiles/flightpath-2022-12-31.json");
        Utils.writeOutputFlightPathResults("2022-12-31", flightPathsResults);
        Assert.assertTrue(toTest2.exists());

        deleteFolder(toTest);
        File toTest3 = new File("./resultfiles/drone-2022-12-31.geojson");
        Utils.writeGeojsonResults("2022-12-31", flightPathsResults);
        Assert.assertTrue(toTest3.exists());
    }

    public void testLagLatHandler() {
        LngLatHandler toTest = new LngLatHandler();

        LngLat[] startPositions = {
                new LngLat(29.745907488250266, 89.49656456882596), // 0
                new LngLat(-3.1884309389133922, 55.94378009065508)  // 1
        };

        LngLat[] vertices = {
                new LngLat(-3.190578818321228, 55.94402412577528),
                new LngLat(-3.1899887323379517, 55.94284650540911),
                new LngLat(-3.187097311019897, 55.94328811724263),
                new LngLat(-3.187682032585144, 55.944477740393744),
                new LngLat(-3.190578818321228, 55.94402412577528)
        };
        NamedRegion testRegion = new NamedRegion("George Square Area", vertices);
        boolean result = toTest.isInRegion(startPositions[0], testRegion);
        Assert.assertFalse(result);
        System.out.println("Test passed");
    }
}
