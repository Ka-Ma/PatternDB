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

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private DBHelper mydb;

    Context context;

    @Before
    public void initDB(){
        context = InstrumentationRegistry.getTargetContext();

        //delete existing database to allow for clean test
        context.deleteDatabase(DBHelper.DATABASE_NAME);

        //now bind database
        mydb = new DBHelper(context);
    }

    @After
    public void closeDb() throws IOException{
        mydb.close();
    }

    @Test
    public void addBrand(){
        String bra = "testBrand";
        mydb.insertBrand(bra);
        List<String> results = mydb.getAllBrands();
        assertEquals(1, results.size());
        assertEquals(bra, results.get(results.size()-1));
    }

    public void getAllBrands(){
        mydb.insertBrand("test2Brand");
        mydb.insertBrand("test3Brand");
        mydb.insertBrand("test4Brand");
        List<String> results = mydb.getAllBrands();

        List<String> expected = new ArrayList<String>();
        expected.add("testBrand");
        expected.add("test2Brand");
        expected.add("test3Brand");
        expected.add("test4Brand");

        assertEquals(expected, results);
    }

    @Test
    public void updateBrand(){

    }

    @Test
    public void deleteBrand(){

    }

    @Test
    public void addCategory() {
        String cat = "testCategory";
        mydb.insertCategory(cat);
        List<String> results = mydb.getAllCategories();
        assertEquals(1, results.size());
        assertEquals(cat, results.get(results.size()-1));
    }

    @Test
    public void addEntry() throws Exception {
        int[] category = {1};
        Pattern pattern = new Pattern("testPattern", 1, "Size", category, "description", "directory for cover image", "directory for back image");

        long pattNum = mydb.insertPattern(pattern);
        assertEquals(1, pattNum);
    }

    @Test
    public void testingTheTest() {
        //nothing
    }


}
