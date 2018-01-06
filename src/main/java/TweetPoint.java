import java.io.Serializable;
import java.util.*;
import java.util.regex.*;

import twitter4j.GeoLocation;
import twitter4j.Status;

import com.pygmalios.reactiveinflux.jawa.*;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class TweetPoint implements Serializable {

    private long id;
    private Date tweeted_at;
    private int favorited_count;
    private double longitude;
    private double latitude;
    private long quote_id;
    private int retweet_count;
    private String text;
    private List<String> links;
    private List<String> hashtags;
    private int sentiment;
    private String location;

    public long getId() {
        return id;
    }

    public Date getTweeted_at() {
        return tweeted_at;
    }

    public int getFavorited_count() {
        return favorited_count;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getQuote_id() { return quote_id; }

    public int getRetweet_count() {
        return retweet_count;
    }

    public String getText() {
        return text;
    }

    public List<String> getLinks() {
        return links;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public String getLocation() { return location; }

    public int getsentiment() {
        return sentiment;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTweeted_at(Date tweeted_at) {
        this.tweeted_at = tweeted_at;
    }

    public void setFavorited_count(int favorited_count) {
        this.favorited_count = favorited_count;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setQuote_id(long quote_id) {
        this.quote_id = quote_id;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void setPos_sentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    private List<String> extractHastags(String status) {

        List<String> hashtags = new ArrayList<>();
        Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(status);
        while (matcher.find())
            hashtags.add(matcher.group(1));
        return hashtags;
    }

    private List<String> extractLinks(String status) {

        List<String> links = new ArrayList<>();
        // Pattern for recognizing a URL, based off RFC 3986
        final Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(status);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            links.add(status.substring(matchStart, matchEnd));
        }
        return links;
    }

    public TweetPoint(Status tweet, int sentiment, String location) {
        this.id = tweet.getId();
        this.tweeted_at = tweet.getCreatedAt();
        this.favorited_count = tweet.getFavoriteCount();
        GeoLocation tweetLocation = tweet.getGeoLocation();
        if(tweetLocation != null) {
            this.longitude = tweet.getGeoLocation().getLongitude();
            this.latitude = tweet.getGeoLocation().getLatitude();
        }
        this.location = location;
        this.quote_id = tweet.getQuotedStatusId();
        this.retweet_count = tweet.getRetweetCount();
        this.text = tweet.getText();
        this.hashtags = this.extractHastags(this.text);
        this.links = this.extractLinks(this.text);
        this.sentiment = sentiment;
    }

    public Point dataPoint() {
        Map<String, String> tags = new HashMap<>();
        if(this.location != null) {
            if (!this.location.equals("")) {
                tags.put("location", this.location);
            }
        }
        if(this.sentiment == 1) {
            tags.put("sentiment", "positive");
        }
        else {
            tags.put("sentiment", "negative");
        }
        //tags.put("sentiment", String(this.sentiment);
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", this.id);
        //fields.put("tweeted_at", this.toString());
        fields.put("favorited_count", this.favorited_count);
        fields.put("longitude", this.longitude);
        fields.put("latitude", this.latitude);
        fields.put("quote_id", this.quote_id);
        fields.put("retweet_count", this.retweet_count);
        fields.put("text", this.text);
        for(int i = 0; i < this.hashtags.size(); i++) {
            fields.put(String.format("hashtag-%d", i), this.hashtags.get(i));
        }
        for(int i = 0; i < this.links.size(); i++) {
            fields.put(String.format("links-%d", i), this.links.get(i));
        }
        //fields.put("hashtags", this.hashtags);
        //fields.put("links", this.links);
        fields.put("sentiment", this.sentiment);
        return new JavaPoint(
                this.getTweeted_at(),
                "tweets",
                tags,
                fields
        );
    }

}
