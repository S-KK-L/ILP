import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.Assert;
import uk.ac.ed.inf.OrderValidator;

import junit.framework.TestCase;
import uk.ac.ed.inf.Output.OutputFlightPath;
import uk.ac.ed.inf.PathFinding.PathFindingAlgo;
import uk.ac.ed.inf.Utils.CreditCardInformationDeserializer;
import uk.ac.ed.inf.Utils.NoFlyZone;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.io.File;
import java.util.List;

public class SmokeTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        System.out.println("Set up");
    }

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
            OrderValidationCode.CARD_NUMBER_INVALID, OrderValidationCode.EXPIRY_DATE_INVALID,
            OrderValidationCode.CVV_INVALID, OrderValidationCode.TOTAL_INCORRECT,
            OrderValidationCode.PIZZA_NOT_DEFINED, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED,
            OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR,
            OrderValidationCode.NO_ERROR, OrderValidationCode.NO_ERROR
        };

        try {
            ordersData = objectMapper1.readValue(new File("./smoke_test_data.json"), Order[].class);
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

    public void testPathFindingAlgo() {
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurant[] restaurantsData = null;
        ObjectMapper objectMapper1 = new ObjectMapper();
        NoFlyZone[] noFlyZonesData = null;
        try {
            restaurantsData = objectMapper.readValue(new File("./smoke_test_restaurant_data.json"), Restaurant[].class);
            noFlyZonesData = objectMapper1.readValue(new File("./smoke_test_noflyzones_data.json"), NoFlyZone[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PathFindingAlgo toTest = new PathFindingAlgo(restaurantsData, noFlyZonesData);
        LngLat StartPoint = new LngLat(-3.2025, 55.9433);
        LngLat EndPoint = new LngLat(-3.1869, 55.9445);
        toTest.pathFinding("0", StartPoint, EndPoint);
        List<OutputFlightPath> results = toTest.getResults();
        OutputFlightPath lastPoint = results.get(results.size() - 1);
        Assert.assertEquals(lastPoint.getFromLongitude(), EndPoint.lng());
        Assert.assertEquals(lastPoint.getFromLatitude(), EndPoint.lat());
        System.out.println("Test passed");
    }
}
