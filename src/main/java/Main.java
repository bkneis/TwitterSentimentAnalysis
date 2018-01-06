import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.apache.spark.streaming.Duration;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.TwitterException;

import com.pygmalios.reactiveinflux.jawa.*;
import com.pygmalios.reactiveinflux.spark.jawa.SparkInflux;

import org.influxdb.*;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws TwitterException, InterruptedException {

        final Duration BATCH_INTERVAL = Durations.seconds(2);
        // Make sure window interval is divisible by the batch interval
        final Duration WINDOW_INTERVAL = Durations.seconds(10);
        final Duration SLIDE_INTERVAL = Durations.seconds(10);

        SparkConf conf = new SparkConf()
                .setMaster("local[4]")
                //.setMaster("spark://marvin:7077") // @todo find out why spark master doesn't use java 1.8
                .set("spark.cassandra.connection.host", "127.0.0.1")
                .set("spark.cassandra.auth.username", "cassandra")
                .set("spark.cassandra.auth.password", "cassandra")
                .setAppName("Analyse Twitter Data v1.1");

        JavaStreamingContext jssc = new JavaStreamingContext(conf, BATCH_INTERVAL);

        SparkInflux sparkInflux = new SparkInflux("tweets", 1000);

        SentimentAnalyser sentimentAnalyser = new SentimentAnalyser();

        InfluxDB influxDB = InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root");

        Query query = new Query("SELECT * FROM names ORDER BY time DESC LIMIT 1", "search_candidates");

        QueryResult result = influxDB.query(query);

        String search_candidate = result.getResults().get(0)
                                        .getSeries().get(0)
                                        .getValues().get(0).get(1)
                                        .toString();

        String filters[] = { search_candidate };

        JavaDStream<Status> tweets = TwitterUtils.createStream(jssc, filters);

        JavaDStream<Status> english_tweets = tweets
                .filter(tweet -> tweet.getLang().equals("en"));

        english_tweets
                .map((tweet) -> {
                    GeoLocation tweetLocation = tweet.getGeoLocation();
                    String location = "";
                    if(tweetLocation != null) {
                        GeoCoder geoLocator;
                        try {
                            geoLocator = new GeoCoder();
                            location = geoLocator.calcState(tweetLocation.getLongitude(), tweetLocation.getLatitude());
                            System.out.println("Location of the tweet : " + location);
                        }
                        catch(IOException e) {
                            System.out.println(e);
                        }
                    }
                    if (sentimentAnalyser.classify(tweet.getText()))
                        return new TweetPoint(tweet, 1, location).dataPoint();
                    else
                        return new TweetPoint(tweet, 0, location).dataPoint();
                })
                // Create windowed RDD from previous 10 RDDs (WINDOW_INTERVAL / BATCH_INTERVAL) to reduce amount of disk IO
                //.window(WINDOW_INTERVAL, SLIDE_INTERVAL)
                .foreachRDD((Function2<JavaRDD<Point>, Time, Void>) (tweetDataJavaRDD, time) -> {
                    //javaFunctions(tweetDataJavaRDD).writerBuilder("sentiment_analysis", "tweets", mapToRow(TweetData.class)).saveToCassandra(); // ks, tweetdata
                    sparkInflux.saveToInflux(tweetDataJavaRDD);
                    return null;
                });

        jssc.start();
        jssc.awaitTermination();

    }
}


/*
JavaDStream<TweetData> d_tweets = english_tweets
                .map((tweet) -> {
                    //String location = geoLocator.calcState(tweet.getGeoLocation().getLatitude(), tweet.getGeoLocation().getLongitude());
                    if (sentimentAnalyser.classify(tweet.getText()))
                        return new TweetData(tweet, 1);
                    else
                        return new TweetData(tweet, 0);
                });
        d_tweets
                // Create windowed RDD from previous 10 RDDs (WINDOW_INTERVAL / BATCH_INTERVAL) to reduce amount of disk IO
                //.window(WINDOW_INTERVAL, SLIDE_INTERVAL)
                .foreachRDD((Function2<JavaRDD<TweetData>, Time, Void>) (tweetDataJavaRDD, time) -> {
                    javaFunctions(tweetDataJavaRDD).writerBuilder("ks", "tweetdata", mapToRow(TweetData.class)).saveToCassandra();
                    return null;
                });
 */