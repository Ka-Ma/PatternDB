package kama.patterndb;

import android.content.Context;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;

import static org.junit.Assert.*;



@RunWith(AndroidJUnit4.class)
public class DbUnitTest {
    private DBHelper mydb;

    Context context;

    @Before
    public void initDB(){
        context = InstrumentationRegistry.getTargetContext() ;
        mydb = new DBHelper(context);
    }

    @After
    public void closeDb() throws IOException{
        mydb.close();
    }

    @Test
    public void addEntry() throws Exception {
        int[] category = {1};
        Pattern pattern = new Pattern("testPattern", 1, "Size", category, "description", "directory for cover image", "directory for back image");
        long pattNum = mydb.insertPattern(pattern);
        assertEquals(1, pattNum);
    }


}
