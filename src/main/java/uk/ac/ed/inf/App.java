package uk.ac.ed.inf;

import uk.ac.ed.inf.Output.OutputOrder;
import uk.ac.ed.inf.PathFinding.PathFindingAlgo;
import uk.ac.ed.inf.Utils.NoFlyZone;
import uk.ac.ed.inf.Utils.Utils;
import uk.ac.ed.inf.ilp.data.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class App
{
    public static List<OutputOrder> findOrdersByDate(String date, Order[] orders, Restaurant[] restaurants) throws IOException {
        // parse string to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.parse(date, formatter);

        // search orders which equal to the date
        List<OutputOrder> orderResults = new ArrayList<>();
        for (Order order: orders) {
            if (order.getOrderDate().equals(today)){
                Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

                orderResults.add(new OutputOrder(validatedOrder.getOrderNo(),
                        validatedOrder.getOrderStatus(),
                        validatedOrder.getOrderValidationCode(),
                        validatedOrder.getPriceTotalInPence()));
            }
        }
        return orderResults;
    }


    public static int main( String[] args )
    {
        // Check if the correct number of arguments are provided.
        if (args.length != 2){
            System.err.println("Testclient Date Base-URL Echo-Parameter");
            System.err.println("you must supply the date and base address of the ILP REST Service\n" +
                    " e.g. http://restservice.somewhere and a string to be echoed");
            return 1;
        }

        try {
            // Retrieve the date and base URL from the command line arguments.
            String date = args[0];
            String baseUrl = args[1];

            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            // Check and delete a cached file if it exists.
            File file = new File(Utils.restaurantsIdx);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Safely delete the cached file");
                }
            }

            long startTime = System.currentTimeMillis();
            // Fetch orders, restaurants, and no-fly zones from the REST service.
            Order[] orders = Utils.getOrdersFromRestService(baseUrl);
            Restaurant[] restaurants = Utils.getRestaurantsFromRestService(baseUrl);
            // Process orders for the specified date.
            List<OutputOrder> orderResults = findOrdersByDate(date, orders, restaurants);
            // Write the processed orders to a file.
            Utils.writeOrderResults(date, orderResults);
            // Fetch no-fly zones and perform pathfinding.
            NoFlyZone[] noFlyZones = Utils.getNoFlyZonesFromRestService(baseUrl);

            // Path finding
            PathFindingAlgo algo = new PathFindingAlgo(restaurants, noFlyZones);
            algo.parseStartEnd();
            // Write the flight paths and GeoJSON results to files.
            Utils.writeOutputFlightPathResults(date, algo.getResults());

            Utils.writeGeojsonResults(date, algo.getResults());
            long endTime = System.currentTimeMillis();
            System.out.println("经过的时间（秒）：" + (endTime - startTime) / 1000);
            return 0;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 2;
    }
}
