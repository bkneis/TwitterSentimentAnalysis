import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class POSTagger implements Serializable {

    private static POSTaggerME tagger = null;

    //@todo, use something better than array list. Also use java 8 lambdas
    public ArrayList<String> getAdjectives(String sentence) {

        String[] words = sentence.split(" ");
        // @todo set the array list to a fixed size then .set each element with the index
        ArrayList<String> adjectives = new ArrayList<>();
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

        POSModel model = null;
        try {
            String trainedPOSModelFile = "/home/arthur/en-pos-maxent.bin";
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

        assert model != null;
        tagger = new POSTaggerME(model);
    }

}
