package kama.patterndb;

import android.content.Context;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private DBHelper mydb;

    Context context;

    String brand1 = "test1Brand";
    String brand2 = "test2Brand";
    String brand3 = "test3Brand";
    String brand4 = "test4Brand";
    String cat1 = "test1Category";
    String cat2 = "test2Category";
    String cat3 = "test3Category";
    String cat4 = "test4Category";


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
        int[] category = {1};
        Pattern pattern = new Pattern("testPattern", 1, "Size", category, "description", "directory for cover image", "directory for back image");
        mydb.insertPattern(pattern);
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
        long id = mydb.findBrand("testTOCHANGEBrand");
        int result = mydb.updateBrand(id, "testCHANGEDBrand");

        assertEquals(1, result);
    }

    @Test
    public void deleteBrand(){
        long id = mydb.findBrand(brand1);
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
        long id = mydb.findCategory(cat1);
        mydb.updateCategory(id, expected);
        String results = mydb.findCategory(id);

        assertEquals(expected, results);
    }

    @Test
    public void deleteCategory(){
        long id = mydb.findCategory("test1Category");
        mydb.deleteCategory(id);
        List<String> results = mydb.getAllCategories();

        List<String> expected = new ArrayList<String>();
        expected.add(cat2);
        expected.add(cat3);
        expected.add(cat4);

        assertEquals(expected, results);
    }

    @Test
    public void addEntry() throws Exception {
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
    }

    @Test
    public void deleteEntry(){
        //find a pattern
        //delete a pattern
    }

    @Test
    public void getAllEntries(){
        //get list of all patterns
        mydb.getAllPatterns();
    }

    @Test
    public void searchEntries(){
        //
    }


}
