package kama.patterndb;

import android.content.Context;

import android.database.CursorIndexOutOfBoundsException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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
        long id = mydb.getBrand("testTOCHANGEBrand");
        int result = mydb.updateBrand(id, "testCHANGEDBrand");

        assertEquals(1, result);
    }

    @Test
    public void deleteBrand(){
        long id = mydb.getBrand(brand1);
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
        //find a particular category
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
        long id = mydb.getCategory(cat1);
        mydb.updateCategory(id, expected);
        String results = mydb.getCategory(id);

        assertEquals(expected, results);
    }

    @Test
    public void deleteCategory(){
        long id = mydb.getCategory("test1Category");
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
        //find a pattern
        //set new pattern dets
        //send it to the db

        //confirm the entry has been updated
    }

    @Test
    public void deleteEntry(){
        //find a pattern
        //delete a pattern

        //confirm the entry no longer exists
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
        //confirm db has been populated, if not do so

        // given a category or categories search all those for keywords in description
        Log.d("myApp", "in test, all null");
        mydb.getPatternsMatching(null, null, null);

        Log.d("myApp", "in test, keywords not null but nothing, others null");
        mydb.getPatternsMatching("", null, null);

        Log.d("myApp", "in test, is a keyword, others null");
        mydb.getPatternsMatching("find", null, null);

        Log.d("myApp", "in test, multiple keywords, others null");
        mydb.getPatternsMatching("find these words", null, null);

        Log.d("myApp", "in test, none null");
        mydb.getPatternsMatching("find these words", new int[] {1,2}, new int[] {3,4});

        Log.d("myApp", "in test, brands null");
        mydb.getPatternsMatching("find these words", null, new int[] {3,4});

        Log.d("myApp", "in test, categories null");
        mydb.getPatternsMatching("find these words", new int[] {1,2}, null);

        //TODO confirm the patterns coming back are correct.

    }



    private List<Pattern> makeTestDb(){
        Log.d("myTest", "making test db");
        List<Pattern> result = new ArrayList<Pattern>();

        //get existing patterns for expected and setting UID
        result.add(mydb.getPatternById((long)1));
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
        Log.d("myTest", "Pattern UID: " + p.getUID());
        Log.d("myTest", "Pattern Number: " + p.getNum());
        Log.d("myTest", "Pattern Brand: " + mydb.getBrand(p.getBrand()));
        for(int cat : p.getCategory()){
            Log.d("myTest", "Pattern Categories: " + mydb.getCategory(cat));
        }

    }
}
