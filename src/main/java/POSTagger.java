import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class POSTagger {

    public static POSModel model = null;
    public static POSTaggerME tagger = null;
    public static String trainedPOSModelFile = "/home/arthur/en-pos-maxent.bin";

    //@todo, use something better than array list. Also use java 8 lambdas
    public static ArrayList<String> getAdjectives(String sentence) {

        String[] words = sentence.split(" ");
        ArrayList<String> adjectives = new ArrayList<String>();
        String tags[] = tagger.tag(words);
        for(int i = 0; i < tags.length; i++) {
            if(tags[i].equals("JJ")) {
                adjectives.add(words[i]);
            }
        }
        return adjectives;
    }

    public POSTagger() {

        InputStream modelIn = null;

        try {
            modelIn = new FileInputStream(trainedPOSModelFile);
            model = new POSModel(modelIn);

        } catch (IOException e) {
            // Model loading failed, handle the error
            e.printStackTrace();

        } finally {

            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        tagger = new POSTaggerME(model);
    }

}
