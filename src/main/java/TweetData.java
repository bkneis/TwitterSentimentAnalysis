import java.io.Serializable;
import java.util.Date;
import java.util.List;

import twitter4j.Status;

/**
 * Created by arthur on 13/06/16.
 */
public class TweetData implements Serializable {

    private long id;
    private Date createdAt;
    private int favoritedCount;
    private double longitude;
    private double latitude;
    private long quotedId;
    private int retweetCount;
    private String text;
    private List links;
    private List hashtags;
    private int posSentiment;

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getFavoritedCount() {
        return favoritedCount;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getQuotedId() {
        return quotedId;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public String getText() {
        return text;
    }

    public List getLinks() {
        return links;
    }

    public List getHashtags() {
        return hashtags;
    }

    public int getPosSentiment() { return posSentiment; }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setFavoritedCount(int favoritedCount) {
        this.favoritedCount = favoritedCount;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public void setQuotedId(long quotedId) {
        this.quotedId = quotedId;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLinks(List links) {
        this.links = links;
    }

    public void setHashtags(List hashtags) {
        this.hashtags = hashtags;
    }

    public void setPosSentiment(int posSentiment) { this.posSentiment = posSentiment; }


    private List extractHastags(String status) {
        return null;
    }

    private List extractLinks(String status) {
        return null;
    }

    public TweetData() {}

    public TweetData(Status tweet, int sentiment) {
        this.id = tweet.getId();
        this.createdAt = tweet.getCreatedAt();
        this.favoritedCount = tweet.getFavoriteCount();
        this.longitude = tweet.getGeoLocation().getLongitude();
        this.latitude = tweet.getGeoLocation().getLatitude();
        this.quotedId = tweet.getQuotedStatusId();
        this.retweetCount = tweet.getRetweetCount();
        this.text = tweet.getText();
        this.links = this.extractHastags(this.text);
        this.hashtags = this.extractLinks(this.text);
        this.posSentiment = sentiment;
    }

    public TweetData(long id, Date createdAt, int favoritedCount, double longitude, double latitude, long quotedId, int retweetCount, String text, List links, List hashtags) {
        this.id = id;
        this.createdAt = createdAt;
        this.favoritedCount = favoritedCount;
        this.longitude = longitude;
        this.latitude = latitude;
        this.quotedId = quotedId;
        this.retweetCount = retweetCount;
        this.text = text;
        this.links = links;
        this.hashtags = hashtags;
    }
}
