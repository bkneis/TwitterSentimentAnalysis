/**
 * Created by arthur on 31/05/16.
 */

import java.io.*;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;

public class SentimentAnalyser implements Serializable {

    private static DoccatModel model = null;
    private static DocumentCategorizerME categorizer;

    SentimentAnalyser() {
        System.out.println("Training the model ....");
        this.train();
        System.out.println("Finished Training");
        this.categorizer = new DocumentCategorizerME(model);
    }

    private void train() {
        InputStream dataIn;

        try {
            dataIn = new FileInputStream("/home/arthur/IdeaProjects/TwitterKNN/input/tweets.txt");
            ObjectStream<String> lineStream =
                    new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(0));
            params.put(TrainingParameters.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);

            this.model = DocumentCategorizerME.train("en", sampleStream, params);

        } catch (IOException e) {
            e.printStackTrace();    // Failed to read or parse training data, training failed
        }
    }

    public boolean classify(String tweet) {
        double[] outcomes = this.categorizer.categorize(tweet);

        String category = this.categorizer.getBestCategory(outcomes);

        if (category.equalsIgnoreCase("1")) {
            System.out.println("The tweet is positive :) ");
            return true;
        } else {
            System.out.println("The tweet is negative :( ");
            return false;
        }
    }

}
