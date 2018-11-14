package kama.patterndb;

import android.content.Context;

import android.database.CursorIndexOutOfBoundsException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private DBHelper mydb;

    Context context;

    //Data to refer to later
    String brand1 = "test1Brand";
    String brand2 = "test2Brand";
    String brand3 = "test3Brand";
    String brand4 = "test4Brand";
    String cat1 = "test1Category";
    String cat2 = "test2Category";
    String cat3 = "test3Category";
    String cat4 = "test4Category";

    Pattern pattern1;
    Pattern pattern2;
    Pattern pattern3;
    Pattern pattern4;
    Pattern pattern5;
    Pattern pattern6;
    Pattern pattern7;


    @Before
    public void initDB(){
        context = InstrumentationRegistry.getTargetContext();

        //delete existing database to allow for clean test
        context.deleteDatabase(DBHelper.DATABASE_NAME);

        //now bind database
        mydb = new DBHelper(context);

        //insert some starting data
        mydb.insertBrand(brand1);
        mydb.insertBrand(brand2);
        mydb.insertBrand(brand3);
        mydb.insertBrand(brand4);
        mydb.insertCategory(cat1);
        mydb.insertCategory(cat2);
        mydb.insertCategory(cat3);
        mydb.insertCategory(cat4);

        pattern1 = new Pattern("testPattern", 1, "Size", new int[] {1}, "description", "directory for cover image", "directory for back image");

        mydb.insertPattern(pattern1);
    }

    @After
    public void closeDb() throws IOException{
        mydb.close();
    }

    @Test
    public void addBrand(){
        int count = mydb.numBrands() + 1;
        String bra = "testBrand";
        mydb.insertBrand(bra);
        List<String> results = mydb.getAllBrands();
        assertEquals(count, results.size());
        assertEquals(bra, results.get(results.size()-1));
    }

    @Test
    public void retrieveBrandNameFromID(){
        int thisOne = mydb.insertBrand("youSUCKI!");
        String result = mydb.getBrand(thisOne);

        assertEquals("youSUCKI!", result);
    }

    @Test
    public void getAllBrands(){
        List<String> results = mydb.getAllBrands();

        List<String> expected = new ArrayList<String>();
        expected.add(brand1);
        expected.add(brand2);
        expected.add(brand3);
        expected.add(brand4);

        assertEquals(expected, results);
    }

    @Test
    public void updateBrand(){
        mydb.insertBrand("testTOCHANGEBrand");
        int id = mydb.getBrand("testTOCHANGEBrand");
        int result = mydb.updateBrand(id, "testCHANGEDBrand");

        assertEquals(1, result);
    }

    @Test
    public void deleteBrand(){
        int id = mydb.getBrand(brand1);
        mydb.deleteBrand(id);
        List<String> results = mydb.getAllBrands();

        List<String> expected = new ArrayList<String>();
        expected.add(brand2);
        expected.add(brand3);
        expected.add(brand4);

        assertEquals(expected, results);
    }

    @Test
    public void addCategory() {
        String cat = "testCategory";
        mydb.insertCategory(cat);
        List<String> results = mydb.getAllCategories();
        assertEquals(5, results.size());
        assertEquals(cat, results.get(results.size()-1));
    }

    @Test
    public void findCategory(){
        String expectedStr = cat3;

        String resultStr = mydb.getCategory(3);

        assertEquals(expectedStr, resultStr);

        int expectedInt = 4;

        int resultInt = (int) mydb.getCategory(cat4);

        assertEquals(expectedInt, resultInt);
    }

    @Test
    public void getAllCategories(){
        List<String> results = mydb.getAllCategories();

        List<String> expected = new ArrayList<String>();
        expected.add(cat1);
        expected.add(cat2);
        expected.add(cat3);
        expected.add(cat4);

        assertEquals(expected, results);
    }

    @Test
    public void updateCategory(){
        String expected = cat1 + "updated";
        int id = mydb.getCategory(cat1);
        mydb.updateCategory(id, expected);
        String results = mydb.getCategory(id);

        assertEquals(expected, results);
    }

    @Test
    public void deleteCategory(){
        int id = mydb.getCategory("test1Category");
        mydb.deleteCategory(id);
        List<String> results = mydb.getAllCategories();

        List<String> expected = new ArrayList<String>();
        expected.add(cat2);
        expected.add(cat3);
        expected.add(cat4);

        assertEquals(expected, results);
    }

    @Test
    public void addEntry() {
        int[] category = {1};
        Pattern pattern = new Pattern("testPattern2", 1, "Size", category, "description", "directory for cover image", "directory for back image");

        long pattNum = mydb.insertPattern(pattern);
        assertEquals(2, pattNum);
    }

    @Test
    public void updateEntry() {
        Pattern p = new Pattern("toBeChanged", 1, "big", new int[] {1, 2}, "This is the original description", "here", "there");
        int pNum = mydb.insertPattern(p);
        p.setUID(pNum);

        //find a pattern
        Pattern found = mydb.getPatternById(pNum);

        //set new pattern dets
        found.setDescription("This is the new description");
        found.setCategory(new int[] {1,3});

        //send it to the db
        mydb.updatePattern(found);

        //debug log
        Log.d("myTest", "original: ");
        printToScreen(p);
        Log.d("myTest", "changed: ");
        printToScreen(found);

        //confirm the entry has been updated
        Pattern fromDB = mydb.getPatternById(pNum);
        assertEquals(fromDB.getUID(), found.getUID());
        assertEquals(fromDB.getNum(), found.getNum());
        assertEquals(fromDB.getBrand(), found.getBrand());
        assertEquals(fromDB.getDescription(), found.getDescription());
        assertEquals(fromDB.getSizeRange(), found.getSizeRange());
        assertArrayEquals(fromDB.getCategory(), found.getCategory());
        assertEquals(fromDB.getCoverImageLocn(), found.getCoverImageLocn());
        assertEquals(fromDB.getBackImageLocn(), found.getBackImageLocn());
    }

    @Test
    public void deleteEntry(){
        //find a pattern, let's just get the first one

        //delete a pattern
        mydb.deletePattern(1);

        //confirm the entry no longer exists)
        Log.d("myTest", "after pattern 1 deleted get " + mydb.getPatternById(1).getUID());
        printToScreen(mydb.getPatternById(1));
        assertEquals(-1, mydb.getPatternById(1).getUID());
    }

    @Test
    public void getAllEntries(){
        //first add a bunch of entries for testing purpoises
        List<Pattern> expected = makeTestDb();
        Log.d("myTest", "GET ALL ENTRIES ___EXPECTED___ Pattern list: ");
        for(int i=0; i<expected.size(); i++){
            Log.d("myTest", i+" : " + expected.get(i).getUID() + " : "+ expected.get(i).getNum());
        }

        //get list of all patterns
        List<Pattern> results = mydb.getAllPatterns();
        Log.d("myTest", "GET ALL ENTRIES Pattern list: ");
        for(int i=0; i<results.size(); i++){
            Log.d("myTest", i+" : "+ results.get(i).getUID() + " : " + results.get(i).getNum());
        }

        for(int i = 0; i<results.size(); i++){
            Log.d("myTest", expected.get(i).getUID()+":"+expected.get(i).getNum() + " vs " + results.get(i).getUID()+":"+results.get(i).getNum());
            assertEquals(expected.get(i).getUID(), results.get(i).getUID());
        }
    }

    //the next search tests assumes that getAllEntries test has run prior and populated the db
    @Test
    public void searchEntriesByNumOneResult() {
        if(!checkTestDBExists()){
            makeTestDb();
        }

        Pattern expectedPattern = pattern2;
        List<Pattern> expected = new ArrayList<>();
        expected.add(expectedPattern);

        //find pattern by num
        List<Pattern> results = mydb.getPatternByPatternNum("7391");

        //assertEquals(expected, results);
        for(int i = 0; i<results.size(); i++){
            Log.d("myTest", expected.get(i).getUID()+":"+expected.get(i).getNum() + " vs " + results.get(i).getUID()+":"+results.get(i).getNum());
            assertEquals(expected.get(i).getUID(), results.get(i).getUID());
        }
    }

    @Test
    public void searchEntriesByNumMultipleResults() {
        if(!checkTestDBExists()){
            makeTestDb();
        }

        Pattern e1 = pattern5;
        Pattern e2 = pattern6;

        List<Pattern> expected = new ArrayList<>();
        expected.add(e1);
        expected.add(e2);

        //find patterns by num
        List<Pattern> results = mydb.getPatternByPatternNum("9206");

        for(int i = 0; i<results.size(); i++){
            Log.d("myTest", expected.get(i).getUID()+":"+expected.get(i).getNum() + " vs " + results.get(i).getUID()+":"+results.get(i).getNum());
            assertEquals(expected.get(i).getUID(), results.get(i).getUID());
        }

        //assertEquals(expected, results);
    }

    @Test
    public void searchEntriesByDescription(){
        //find pattern by description
        if (!checkTestDBExists()) {
            makeTestDb();
        }


        Pattern e = pattern4;
        List<Pattern> expected = new ArrayList<>();
        expected.add(e);

        String description = "bolero";

        List<Pattern> results = mydb.getPatternByDescription(description);

        //assertEquals(expected, results);
        Log.d("myTest", "ENTRIES BY DESCRIPTION: expected vs results");
        for(int i = 0; i<results.size(); i++){
            Log.d("myTest", expected.get(i).getUID()+":"+expected.get(i).getNum() + " vs " + results.get(i).getUID()+":"+results.get(i).getNum());
            assertEquals(expected.get(i).getUID(), results.get(i).getUID());
        }
    }

    @Test
    public void searchEntriesByBrand() {
        //find pattern by brand
        if(!checkTestDBExists()){
            makeTestDb();
        }

        List<Pattern> expected = new ArrayList<>();
        expected.add(pattern7);

        List<Pattern> results = mydb.getPatternByBrand(mydb.getBrand("Burda"));

       for(int i = 0; i<results.size(); i++){
           assertEquals(expected.get(i).getUID(), results.get(i).getUID());
       }
    }

    @Test
    public void searchEntriesByCategory() {
        //find pattern by category
        if(!checkTestDBExists()){
            makeTestDb();
        }

        List<Pattern> expected = new ArrayList<>();
        expected.add(pattern5);
        expected.add(pattern6);

        List<Pattern> results = new ArrayList<>();

        try {
            results = mydb.getPatternByCategory(mydb.getCategory("Casual"));
        }catch (CursorIndexOutOfBoundsException e){
            Log.d("myTest", "Exception " + e);
            fail();
        }

        //patterns that came back were:
        for(int i = 0; i<results.size(); i++){
            Log.d("myTest", "*************************************PATTERN "+ i);
            printToScreen(results.get(i));
        }

        assertEquals(2, results.size());
        for(int i = 0; i<results.size(); i++){
            assertEquals(expected.get(i).getUID(), results.get(i).getUID());
        }
    }

    @Test
    public void searchEntriesByDescriptionAndCategory(){
        //find pattern by description & category
        List<Pattern> result;

        //confirm db has been populated, if not do so
        if(!checkTestDBExists()){
            makeTestDb();
        }

        //create test array to lessen code
        List<TestData> testData = createTestData();
        //List<TestData> testData = createTestDataForDebug();

        // given a category or categories search all those for keywords in description
        for(TestData t : testData) {
            Log.d("myApp", t.getMsg());
            result = mydb.getPatternsMatching(t.getKeyWords(), t.getBrands(), t.getCategories());

            Log.d("myApp", "there are " + result.size() + " results");
            for (Pattern p : result) {
                printToScreen(p);
            }

            assertEquals(t.getExpected(), result.size());
        }
    }


    //helper methods
    private List<Pattern> makeTestDb(){
        Log.d("myTest", "making test db");
        List<Pattern> result = new ArrayList<Pattern>();

        //get existing patterns for expected and setting UID
        result.add(mydb.getPatternById(1));
        result.get(0).setUID(1);

        //add new patterns for expected
        //adding brands
        mydb.insertBrand("McCall's");
        mydb.insertBrand("Burda");

        //adding categories
        mydb.insertCategory("Business/Casual");
        mydb.insertCategory("Leisure");
        mydb.insertCategory("Formal");
        mydb.insertCategory("Kids");
        mydb.insertCategory("Casual");

        setPatterns();

        //adding patterns
        result.add(pattern2);
        result.add(pattern3);
        result.add(pattern4);
        result.add(pattern5);
        result.add(pattern6);
        result.add(pattern7);

        //put new patterns from list into db
        for (int i = 1; i<result.size(); i++){
            mydb.insertPattern(result.get(i));
        }

        return result;
    }

    private boolean checkTestDBExists(){
        List<Pattern> result = mydb.getAllPatterns();

        if(result.size() < 6){
            return false;
        }else{
            return true;
        }
    }

    private void setPatterns(){
        pattern2 = new Pattern("7391", (int) mydb.getBrand("McCall's"), "10-12-14-16", new int[] {(int) mydb.getCategory("Business/Casual")}, "Misses' Unlined Jacket, Vest and Skirt: Unlined jacket and front buttoned vest have princess seaming; pull-on skirt with back slit has side seam pockets and fold-back waist casing with two rows of elastic extending to front pleats", "here", "there");
        pattern2.setUID(2);
        pattern3 = new Pattern("7711", (int) mydb.getBrand("McCall's"), "14, 16, 18", new int[] {(int) mydb.getCategory("Leisure")}, "Misses' Lined or Unlined dress or jumper in two lengths: Princess seamed dress or jumper in two lengths with back zipper and optional lining has cut in armholes and optional ribbon trim.", "here", "there");
        pattern3.setUID(3);
        pattern4 = new Pattern("6855", (int) mydb.getBrand("McCall's"), "16", new int[] {(int) mydb.getCategory("Formal")}, "Misses' lined bolero and lined dress in two lengths: Cropped bolero has front and back princess seams, kimono sleeves with under sleeve inset and shoulder pads; fitted dress has front and back princess darts and back zipper; longer length dress has slit on left side with underlay.", "here", "there");
        pattern4.setUID(4);
        pattern5 = new Pattern("9206", (int) mydb.getBrand("McCall's"), "4, 5, 6", new int[] {(int) mydb.getCategory("Kids"), (int) mydb.getCategory("Casual")}, "Children's and boys' shirt , t-shirt, pants or shorts and hat - t-shirts B and C for stretch knits only: Loose fitting pullover shirt and T-shirt with extended shoulders have long or short sleeves; view A and front \"V\" neck with self-fabric neckband; views B and C with round neck have neckband of ribbed knit; pull-on pants with elasticized ankles and shorts have two rows of elastic through fold-back waist casing and side seam pockets; baseball hat also included.", "here", "there");
        pattern5.setUID(5);
        pattern6 = new Pattern("9206", (int) mydb.getBrand("McCall's"), "2, 3, 4", new int[] {(int) mydb.getCategory("Kids"), (int) mydb.getCategory("Casual")}, "Children's and boys' shirt , t-shirt, pants or shorts and hat - t-shirts B and C for stretch knits only: Loose fitting pullover shirt and T-shirt with extended shoulders have long or short sleeves; view A and front \"V\" neck with self-fabric neckband; views B and C with round neck have neckband of ribbed knit; pull-on pants with elasticized ankles and shorts have two rows of elastic through fold-back waist casing and side seam pockets; baseball hat also included.", "here", "there");
        pattern6.setUID(6);
        pattern7 = new Pattern("9711", (int) mydb.getBrand("Burda"), "3m, 6m, 9m, 12m, 18m", new int[] {(int) mydb.getCategory("Kids")}, "coordinates: loose fitting, ample", "here", "there");
        pattern7.setUID(7);
    }

    private void printToScreen(Pattern p){
        if(p.getUID() > 0){
            String msg = "UID: " + p.getUID() + " Number: " + p.getNum() + " Brand: " + mydb.getBrand(p.getBrand()) + " Categories: ";
            for(int cat : p.getCategory()){
                msg = msg.concat(mydb.getCategory(cat) + ", ");
            }
            msg = msg.concat(" Desc: " + p.getDescription());
            Log.d("myTest", msg );
        }
    }

    private List<TestData> createTestData(){
        List<TestData> testData = new ArrayList<>();

        testData.add(new TestData("in test, #1 keywords = null, brands = null, categories = null",null, null, null, 7));
        testData.add(new TestData("in test, #2 keywords = \"\", brands = null, categories = null","", null, null, 7));
        testData.add(new TestData("in test, #3 keywords = \" \", brands = null, categories = null"," ", null, null, 7));
        testData.add(new TestData("in test, #4 keywords = null, brands = null, categories = Kids",null, null, new int[] {(int) mydb.getCategory("Kids")}, 3));
        testData.add(new TestData("in test, #5 keywords = null, brands = null, categories = Kids, Casual",null, null, new int[] {(int) mydb.getCategory("Kids"), (int) mydb.getCategory("Casual")}, 3));
        testData.add(new TestData("in test, #6a keywords = null, brands = Burda, categories = null", null, new int[] {(int) mydb.getBrand("Burda")}, null, 1));
        testData.add(new TestData("in test, #6b keywords = null, brands = McCall's, categories = null", null, new int[] {(int) mydb.getBrand("McCall's")}, null, 5));
        testData.add(new TestData("in test, #7a keywords = null, brands = McCall's, categories = Leisure", null, new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Leisure")}, 1));
        testData.add(new TestData("in test, #7b keywords = null, brands = McCall's, categories = Kids", null, new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Kids")}, 2));
        testData.add(new TestData("in test, #8 keywords = null, brands = McCall's, categories = Kids, Leisure", null, new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Kids"), (int) mydb.getCategory("Leisure")}, 3));
        testData.add(new TestData("in test, #9 keywords = null, brands = McCall's, Burda, categories = null", null, new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, null, 6));
        testData.add(new TestData("in test, #10 keywords = null, brands = McCall's, Burda, categories = Leisure", null, new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, new int[] {(int) mydb.getCategory("Leisure")}, 1));
        testData.add(new TestData("in test, #11 keywords = null, brands = McCall's, Burda, categories = Leisure, Formal", null, new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, new int[] {(int) mydb.getCategory("Leisure"), (int) mydb.getCategory("Formal")}, 2));
        testData.add(new TestData("in test, #12 keywords = \"dress\", brands = null, categories = null","dress", null, null, 2));
        testData.add(new TestData("in test, #13 keywords = \"dress\", brands = null, categories = Formal","dress", null, new int[] {(int) mydb.getCategory("Formal")}, 1));
        testData.add(new TestData("in test, #14 keywords = \"dress\", brands = null, categories = Formal, Leisure","dress", null, new int[] {(int) mydb.getCategory("Formal"), (int) mydb.getCategory("Leisure")}, 2));
        testData.add(new TestData("in test, #15 keywords = \"princess\", brands = McCall's, categories = null", "princess", new int[] {(int) mydb.getBrand("McCall's")}, null, 3));
        testData.add(new TestData("in test, #16 keywords = \"princess\", brands = McCall's, categories = Business/Casual", "princess", new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Business/Casual")}, 1));
        testData.add(new TestData("in test, #17 keywords = \"princess\", brands = McCall's, categories = Business/Casual, Formal", "princess", new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Business/Casual"), (int) mydb.getCategory("Formal")}, 2));
        testData.add(new TestData("in test, #18 keywords = \"princess\", brands = McCall's, Burda, categories = null", "princess", new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, null, 3));
        testData.add(new TestData("in test, #19 keywords = \"princess\", brands = McCall's, Burda, categories = Business/Casual", "princess", new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, new int[] {(int) mydb.getCategory("Business/Casual")}, 1));
        testData.add(new TestData("in test, #20 keywords = \"princess\", brands = McCall's, Burda, categories = Business/Casual, Formal", "princess", new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, new int[] {(int) mydb.getCategory("Business/Casual"), (int) mydb.getCategory("Formal")}, 2));
        testData.add(new TestData("in test, #21 keywords = \"dress princess\", brands = null, categories = null","dress princess", null, null, 3));
        testData.add(new TestData("in test, #22 keywords = \"dress princess\", brands = null, categories = Business/Casual","dress princess", null, new int[] {(int) mydb.getCategory("Business/Casual")}, 1));
        testData.add(new TestData("in test, #23 keywords = \"dress princess\", brands = null, categories = Business/Casual, Formal","princess dress", null, new int[] {(int) mydb.getCategory("Business/Casual"), (int) mydb.getCategory("Formal")}, 2));
        testData.add(new TestData("in test, #24 keywords = \"princess dress\", brands = McCall's, categories = null", "princess dress", new int[] {(int) mydb.getBrand("McCall's")}, null, 3));
        testData.add(new TestData("in test, #25 keywords = \"princess dress\", brands = McCall's, categories = Formal", "princess dress", new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Formal")}, 1));
        testData.add(new TestData("in test, #26 keywords = \"princess dress\", brands = McCall's, categories = Leisure, Formal", "princess dress", new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Leisure"), (int) mydb.getCategory("Formal")}, 2));
        testData.add(new TestData("in test, #27 keywords = \"princess dress\", brands = McCall's, Burda, categories = null", "princess dress", new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, null, 3));
        testData.add(new TestData("in test, #28 keywords = \"princess dress\", brands = McCall's, Burda, categories = Leisure", "princess dress", new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, new int[] {(int) mydb.getCategory("Leisure")}, 1));
        testData.add(new TestData("in test, #29 keywords = \"princess dress\", brands = McCall's, Burda, categories = Leisure, Formal", "princess dress", new int[] {(int) mydb.getBrand("McCall's"), (int) mydb.getBrand("Burda")}, new int[] {(int) mydb.getCategory("Leisure"), (int) mydb.getCategory("Formal")}, 2));

        return testData;
    }

    private List<TestData> createTestDataForDebug(){
        List<TestData> testData = new ArrayList<>();

        testData.add(new TestData("in test, #1 keywords = null, brands = null, categories = null",null, null, null, 7));
        testData.add(new TestData("in test, #4a keywords = null, brands = null, categories = Leisure",null, null, new int[] {(int) mydb.getCategory("Leisure")}, 1));
        testData.add(new TestData("in test, #4b keywords = null, brands = null, categories = Kids",null, null, new int[] {(int) mydb.getCategory("Kids")}, 3));
        testData.add(new TestData("in test, #6a keywords = null, brands = Burda, categories = null", null, new int[] {(int) mydb.getBrand("Burda")}, null, 1));
        testData.add(new TestData("in test, #6b keywords = null, brands = McCall's, categories = null", null, new int[] {(int) mydb.getBrand("McCall's")}, null, 5));
        testData.add(new TestData("in test, #7a keywords = null, brands = McCall's, categories = Leisure", null, new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Leisure")}, 1));
        testData.add(new TestData("in test, #7b keywords = null, brands = McCall's, categories = Kids", null, new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Kids")}, 2));
        testData.add(new TestData("in test, #8 keywords = null, brands = McCall's, categories = Kids, Leisure", null, new int[] {(int) mydb.getBrand("McCall's")}, new int[] {(int) mydb.getCategory("Kids"), (int) mydb.getCategory("Leisure")}, 3));

        return testData;
    }
}

class TestData{
    String mTestMsg;
    String mTestKeywords;
    int[] mTestBrands;
    int[] mTestCategories;
    int mTestExpected;

    TestData(String msg, String key, int[] brand, int[] cate, int expect){
        mTestMsg = msg;
        mTestKeywords = key;
        mTestBrands = brand;
        mTestCategories = cate;
        mTestExpected = expect;
    }

    public String getMsg(){ return mTestMsg; }
    public String getKeyWords(){ return mTestKeywords;}
    public int[] getBrands(){return mTestBrands;}
    public int[] getCategories(){return mTestCategories;}
    public int getExpected(){return mTestExpected;}

}