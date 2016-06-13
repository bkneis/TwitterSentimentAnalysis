import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import twitter4j.Status;
import twitter4j.TwitterException;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

public class Main {

    public static void main(String[] args) throws TwitterException, InterruptedException {

        SparkConf conf = new SparkConf()
                .setMaster("local[4]")
                //.setMaster("spark://marvin:7077") // @todo find out why spark master doesnt support java 1.8
                .set("spark.cassandra.connection.host", "127.0.0.1")
                .set("spark.cassandra.auth.username", "cassandra")
                .set("spark.cassandra.auth.password", "cassandra")
                .setAppName("Analyse Twitter Data v1.1");

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));

        SentimentAnalyser sentimentAnalyser = new SentimentAnalyser();

        String filters[] = {"donald trump"};

        JavaDStream<Status> tweets = TwitterUtils.createStream(jssc, filters);

        // @todo find a way to not have to save the dstream but directly use it with the transformations below
        JavaDStream<Status> english_tweets = tweets
                .filter(tweet -> tweet.getLang().equals("en"));

        english_tweets
                .filter(tweet -> tweet.getLang().equals("en"))
                .filter(tweet -> sentimentAnalyser.classify(tweet.getText()))
                .map((tweet) -> {
                    if(sentimentAnalyser.classify(tweet.getText()))
                        return new TweetData(tweet, 1);
                    else
                        return new TweetData(tweet, 0);
                })
                .foreachRDD((Function2<JavaRDD<TweetData>, Time, Void>) (tweetDataJavaRDD, time) -> {
                    javaFunctions(tweetDataJavaRDD).writerBuilder("ks", "tweets", mapToRow(TweetData.class)).saveToCassandra();
                    return null;
                });

        /*english_tweets
                .filter(tweet -> sentimentAnalyser.classify(tweet.getText()))
                .map(tweet -> tweet.getText())
                .foreachRDD((Function2<JavaRDD<String>, Time, Void>) (stringJavaRDD, time) -> {
                    stringJavaRDD.saveAsTextFile("/tmp/dump/positive_tweets.txt");
                    return null;
                });

        english_tweets
                .filter(tweet -> !sentimentAnalyser.classify(tweet.getText()))
                .map(tweet -> tweet.getText())
                .foreachRDD((Function2<JavaRDD<String>, Time, Void>) (stringJavaRDD, time) -> {
                    stringJavaRDD.saveAsTextFile("/tmp/dump/negative_tweets.txt");
                    return null;
                }); */

        jssc.start();
        jssc.awaitTermination();

    }
}