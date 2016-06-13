import org.apache.spark.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.*;
//import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.twitter.*;
import twitter4j.*;

public class TwitterHelper {

    public static void collectData() {
                                                                                // Create a local StreamingContext with two working thread and batch interval of 1 second
        SparkConf conf = new SparkConf()
                .setMaster("spark://marvin:7077")
                .setAppName("Collect Twitter Data");

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));

        POSTagger posTagger = new POSTagger();                                  // Create the POS tagger to pass to the workers

        String filters[] = {"donald trump"};                                    // Set the filters for the target

        JavaDStream<Status> tweets = TwitterUtils.createStream(jssc, filters);  // Create the twitter stream

        JavaDStream<String> adjectives = tweets
                .filter(tweet -> tweet.getLang().equals("en"))                  // Get only english tweets @todo create separate RDD and save those tweets in hdfs for later analysis
                .map(tweet -> tweet.getText());
                //.flatMap(tweet -> posTagger.getAdjectives(tweet.getText()));  // Filter out only the adjectives in the tweet

        adjectives.foreachRDD((Function2<JavaRDD<String>, Time, Void>) (stringJavaRDD, time) -> {
            stringJavaRDD.saveAsTextFile("/tmp/dump/test.txt");
            return null;
        });

        jssc.start();                                                           // Start the computation
        jssc.awaitTermination();                                                // Wait for the computation to terminate

    }

}
