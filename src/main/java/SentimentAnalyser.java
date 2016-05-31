/**
 * Created by arthur on 31/05/16.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;

public class SentimentAnalyser implements Serializable {

    private DoccatModel model = null;
    private DocumentCategorizerME categorizer;

    SentimentAnalyser() {
        System.out.println("Training the model ....");
        this.train();
        System.out.println("Finished Training");
        this.categorizer = new DocumentCategorizerME(model);
    }

    private void train() {
        InputStream dataIn;
                                                                                // Check if there is a cached version of our model (already trained)
        try {
            dataIn = new FileInputStream("resources/en-sentiment.bin");
            this.model = new DoccatModel(dataIn);
            return;
        }
        catch (IOException e) {
            System.out.println("The model was not found, so we will train it from scratch...");
        }

        try {
            dataIn = new FileInputStream("resources/en-sentiment.train");
            ObjectStream<String> lineStream =
                    new PlainTextByLineStream(dataIn, "UTF-8");                 // Find newer method to replace deprecated function
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(0));
            params.put(TrainingParameters.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);

                                                                                // TrainingParameters.ALGORITHM_PARAM ensures
            this.model = DocumentCategorizerME.train("en", sampleStream, params);
        }
        catch (IOException e) {
            // Failed to read or parse training data, training failed
            e.printStackTrace();
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
