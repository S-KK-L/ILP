import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ed.inf.App;

public class GrayBoxTest extends TestCase {
    public void testGrayBox() {
        // get operating system type
        String os = System.getProperty("os.name").toLowerCase();

        String baseUrl = "https://ilp-rest.azurewebsites.net";
        String [] args = {"2023-09-28", baseUrl};
        int result = 0;

        // get operating system arch
        String arch = System.getProperty("os.arch");

        System.out.println("Operating System: " + os);
        System.out.println("Architecture: " + arch);

        if (os.contains("win")) {
            System.out.println("Windows");
            result = App.main(args);
            Assert.assertEquals(result, 0);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            System.out.println("Unix/Linux/Mac");
            result = App.main(args);
            Assert.assertEquals(result, 0);
        } else {
            System.out.println("Other");
            result = App.main(args);
            Assert.assertEquals(result, 0);
        }

        // get cpu bit width
        if (arch.contains("64")) {
            System.out.println("64-bit");
            result = App.main(args);
            Assert.assertEquals(result, 0);
        } else {
            System.out.println("32-bit");
            result = App.main(args);
            Assert.assertEquals(result, 0);
        }
        System.out.println("Test passed");
    }
}
