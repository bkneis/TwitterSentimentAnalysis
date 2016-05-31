import junit.framework.TestCase;

import java.util.ArrayList;

public class POSTaggerTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {

    }

    public void testGetAdjectives() {
        POSTagger posTagger = new POSTagger();
        String sentence = "This is a good test";
        ArrayList<String> tags = posTagger.getAdjectives(sentence);
        ArrayList<String> correctReturn = new ArrayList<>();
        correctReturn.add("good");
        assertTrue(tags.containsAll(correctReturn) && correctReturn.containsAll(tags));
    }

}