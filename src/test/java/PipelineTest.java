import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class PipelineTest {
    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./pipeline.log"));

        try {
            SmokeTest test1 =  new SmokeTest();
            test1.testFindValidateOrders();
            test1.testPathFindingAlgo();
            writer.write("SmokeTest passed!");
            writer.newLine();
        } catch (Exception e) {
            writer.write("SmokeTest failed, please checked the core module in the system!");
            writer.newLine();
        }

        try {
            FunctionalTest test2 = new FunctionalTest();
            test2.testSystemLevel();
            test2.testPathFindingAlgo();
            test2.testOrderValidator();
            test2.testUtils();
            test2.testLagLatHandler();
            test2.testIntegrationLevel();
            writer.write("FunctionalTest passed!");
            writer.newLine();
        } catch (Exception e) {
            writer.write("FunctionalTest failed, please refer to the codes in the failed test!");
            writer.newLine();
        }

        try {
            StructuralTest test3 = new StructuralTest();
            test3.testSystemLevel();
            test3.testPathFindingAlgo();
            test3.testFindValidateOrders();
            test3.testUtils();
            test3.testLagLatHandler();
            writer.write("StructuralTest passed!");
            writer.newLine();
        } catch (Exception e) {
            writer.write("StructuralTest failed, please refer to the codes in the failed test!");
            writer.newLine();
        }

        try {
            GrayBoxTest test4 = new GrayBoxTest();
            test4.testGrayBox();
            writer.write("GrayBoxTest passed!");
            writer.newLine();
        } catch (Exception e) {
            writer.write("GrayBoxTest failed, please check your compiling environment!");
            writer.newLine();
        }

        try {
            StressTest test5 = new StressTest();
            test5.testSystemStress();
            writer.write("StressTest passed!");
            writer.newLine();
        } catch (Exception e) {
            writer.write("StressTest failed, please check your running environment and resources!");
            writer.newLine();
        }
        writer.write("All passed!");
        writer.newLine();
        writer.close();
    }
}
