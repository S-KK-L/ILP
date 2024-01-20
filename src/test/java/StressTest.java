import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.TestCase;
import uk.ac.ed.inf.Output.OutputOrder;
import uk.ac.ed.inf.PathFinding.PathFindingAlgo;
import uk.ac.ed.inf.Utils.CreditCardInformationDeserializer;
import uk.ac.ed.inf.Utils.NoFlyZone;
import uk.ac.ed.inf.Utils.Utils;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;

import static uk.ac.ed.inf.App.findOrdersByDate;

public class StressTest extends TestCase {

    public void testSystemStress() throws IOException {
        int [] stressLevel = {10, 100, 1000, 10000, 100000};
        double [] results = {0, 0, 0, 0, 0};
        for (int i = 0; i < stressLevel.length; ++i) {
            // disable println
//            System.setOut(new PrintStream(new OutputStream() {
//                @Override public void write(int b) throws IOException {}
//            }));

            long startTime = System.currentTimeMillis();
            String baseUrl =  "https://ilp-rest.azurewebsites.net/";

            // mock data
            ObjectMapper objectMapper1 = new ObjectMapper();
            objectMapper1.registerModule(new JavaTimeModule());
            ObjectMapper objectMapper2 = new ObjectMapper();
            // Register a custom deserializer for CreditCardInformation
            SimpleModule module = new SimpleModule();
            module.addDeserializer(CreditCardInformation.class, new CreditCardInformationDeserializer());
            objectMapper1.registerModule(module);

            Order[] ordersData = null;
            Restaurant[] restaurantsData = null;

            String jsonString = "{\n" +
                    "    \"orderNo\": \"6218488F\",\n" +
                    "    \"orderDate\": \"2023-09-01\",\n" +
                    "    \"orderStatus\": \"UNDEFINED\",\n" +
                    "    \"orderValidationCode\": \"UNDEFINED\",\n" +
                    "    \"priceTotalInPence\": 2600,\n" +
                    "    \"pizzasInOrder\": [\n" +
                    "      {\n" +
                    "        \"name\": \"R2: Meat Lover\",\n" +
                    "        \"priceInPence\": 1400\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"R2: Vegan Delight\",\n" +
                    "        \"priceInPence\": 1100\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"creditCardInformation\": {\n" +
                    "      \"creditCardNumber\": \"4286860294655612\",\n" +
                    "      \"creditCardExpiry\": \"02/28\",\n" +
                    "      \"cvv\": \"937\"\n" +
                    "    }\n" +
                    "  }";
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("./stress_test_data.json"));
                writer.write('[');
                // write the json string several times
                for (int j = 0; j < stressLevel[i]; j++) {
                    writer.write(jsonString);
                    if (j != stressLevel[i] - 1) writer.write(',');
                    writer.newLine(); // 换行
                }
                writer.write(']');
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ordersData = objectMapper1.readValue(new File("./stress_test_data.json"), Order[].class);
                restaurantsData = objectMapper2.readValue(new File("./smoke_test_restaurant_data.json"), Restaurant[].class);


                findOrdersByDate("2023-09-01" , ordersData, restaurantsData);

                NoFlyZone[] noFlyZones = Utils.getNoFlyZonesFromRestService(baseUrl);
                PathFindingAlgo algo = new PathFindingAlgo(restaurantsData, noFlyZones);
                algo.parseStartEnd();
                long endTime = System.currentTimeMillis();

//                System.setOut(System.out);
                results[i] = (endTime - startTime) / 1000;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < stressLevel.length; ++i) {
            System.out.println("StressLevel: " + stressLevel[i] + ", time elapsed（s）：" + results[i]);
        }
    }
}
