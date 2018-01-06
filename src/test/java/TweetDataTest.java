import junit.framework.TestCase;
import static org.mockito.Mockito.*;

import twitter4j.GeoLocation;
import twitter4j.Status;
import java.util.List;
import java.util.ArrayList;

import java.util.Date;

/**
 * Created by arthur on 20/06/16.
 */
public class TweetDataTest extends TestCase {

    private TweetData testTweetData;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // @todo expand this string to include more test cases for urls
        String testStatus = "#test #hello " +
                "http://www.google.com " +
                "https://www.google.com " +
                "www.google.com";

        /* Mock twitter4j Status and GeoLocation objects and the required
           functions for the TwitterData constructor*/
        Status status = mock(Status.class);
        GeoLocation location = mock(GeoLocation.class);
        when(status.getId()).thenReturn((long) 1);
        when(status.getCreatedAt()).thenReturn(new Date());
        when(status.getFavoriteCount()).thenReturn(0);
        when(location.getLongitude()).thenReturn((double) 1);
        when(location.getLongitude()).thenReturn((double) 1);
        when(status.getGeoLocation()).thenReturn(location);
        when(status.getQuotedStatusId()).thenReturn((long) 1);
        when(status.getRetweetCount()).thenReturn(1);
        when(status.getText()).thenReturn(testStatus);
        String testLocation = "US_CA";
        testTweetData = new TweetData(status, 1, testLocation);
    }

    @Override
    public void tearDown() throws Exception {

    }

    public void testExtractLinks() throws Exception {
        List expectedLinks = new ArrayList();
        expectedLinks.add("http://www.google.com");
        expectedLinks.add("https://www.google.com");
        expectedLinks.add("www.google.com");
        assertEquals(testTweetData.getLinks(), expectedLinks);

    }

    public void testExtractHashTags() throws Exception {
        List expectedHashTags = new ArrayList();
        expectedHashTags.add("test");
        expectedHashTags.add("hello");
        assertEquals(testTweetData.getHashtags(), expectedHashTags);
    }

}