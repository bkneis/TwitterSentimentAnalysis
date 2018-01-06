import junit.framework.TestCase;

/**
 * Created by arthur on 28/07/16.
 */
public class GeoCoderTest extends TestCase {

    private GeoCoder testGeoCoder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testGeoCoder = new GeoCoder();
    }

    @Override
    public void tearDown() throws Exception {

    }

    public void testCalcState() throws Exception {
        double testX = -122.4319137;
        double testY = 37.769345;
        String result = testGeoCoder.calcState(testX, testY);
        String expectedResult = "US-CA";
        System.out.println("Actual Result : " + result);
        System.out.println("Expected Result : " + expectedResult);
        assertTrue("Calculating the location from co ordinates failed", result.equals(expectedResult));

    }

}