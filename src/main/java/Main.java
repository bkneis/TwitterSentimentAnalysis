//import static spark.Spark.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        POSTagger posTagger = new POSTagger();

        //@todo replace this with function to get a tweet, inside a loop
        String tweet = "Most large cities in the US had morning and afternoon newspapers.";

        ArrayList<String> tags = posTagger.getAdjectives(tweet);

        for(int i = 0; i < tags.size(); i++){
            System.out.println(tags.get(i));
        }
    }
}