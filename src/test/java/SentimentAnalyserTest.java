import junit.framework.TestCase;

/**
 * Created by arthur on 20/06/16.
 */
public class SentimentAnalyserTest extends TestCase {

    private SentimentAnalyser testAnalyser;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testAnalyser = new SentimentAnalyser();
    }

    @Override
    public void tearDown() throws Exception {

    }

    public void testClassify() throws Exception {
        String testPositive = "I love my life, its great";
        String testNegative = "I hate everything, its stupid, boring and nasty";
        assertTrue("Positive sentiment classification failed", testAnalyser.classify(testPositive));
        assertFalse("Negative sentiment classification failed", testAnalyser.classify(testNegative));
    }

}