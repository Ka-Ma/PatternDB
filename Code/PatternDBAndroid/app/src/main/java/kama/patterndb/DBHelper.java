package kama.patterndb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kat on 2/08/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    //Database name
    public static final String DATABASE_NAME = "PatternDatabase.db";

    //table "brand"
    public static final String BRAND_TABLE_NAME = "brand";
    public static final String BRAND_COLUMN_BRANDID = "brandID";
    public static final String BRAND_COLUMN_BRAND = "brand";

    //table "category"
    public static final String CATEGORY_TABLE_NAME = "category";
    public static final String CATEGORY_COLUMN_CATEGORYID = "categoryID";
    public static final String CATEGORY_COLUMN_CATEGORY = "category";

    //table "pattern"
    public static final String PATTERN_TABLE_NAME = "pattern";
    public static final String PATTERN_COLUMN_PATTERNID = "patternID";
    public static final String PATTERN_COLUMN_PATTERNNUM = "patternNum";
    public static final String PATTERN_COLUMN_BRANDID = "brandID";
    public static final String PATTERN_COLUMN_SIZERANGE = "sizeRange";
    public static final String PATTERN_COLUMN_DESCRIPTION = "description";
    public static final String PATTERN_COLUMN_COVERIMAGE = "coverImage";
    public static final String PATTERN_COLUMN_BACKIMAGE = "backImage";

    //table "patternCategory"
    public static final String PATTERNCATEGORY_TABLE_NAME = "patternCategory";
    public static final String PATTERNCATEGORY_COLUMN_PATTERNID = "patternID";
    public static final String PATTERNCATEGORY_COLUMN_CATEGORYID = "categoryID";

    //version to change if tables need to be recreated
    static int ver = 2;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, ver);
    }

    //only run if database does not exist
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d("myApp", "The database is created");

        //create table "brand"
        db.execSQL(
                "create table " + BRAND_TABLE_NAME + "("+
                        BRAND_COLUMN_BRANDID + " integer primary key autoincrement, " +
                        BRAND_COLUMN_BRAND + " text)"
        );

        //create table "category"
        db.execSQL(
                "create table " + CATEGORY_TABLE_NAME + "("+
                        CATEGORY_COLUMN_CATEGORYID+ " integer primary key autoincrement, " +
                        CATEGORY_COLUMN_CATEGORY + " text)"
        );

        //create table "pattern"
        db.execSQL(
                "create table " + PATTERN_TABLE_NAME + "("+
                        PATTERN_COLUMN_PATTERNID + " integer primary key autoincrement, " +
                        PATTERN_COLUMN_PATTERNNUM + " text, " +
                        PATTERN_COLUMN_BRANDID + " integer, " +
                        PATTERN_COLUMN_SIZERANGE + " text, " +
                        PATTERN_COLUMN_DESCRIPTION + " text, " +
                        PATTERN_COLUMN_COVERIMAGE + " text, " +
                        PATTERN_COLUMN_BACKIMAGE + " text, " +
                        "FOREIGN KEY (" + PATTERN_COLUMN_BRANDID + ") REFERENCES " + BRAND_TABLE_NAME + "(" + BRAND_COLUMN_BRANDID + "))"
        );

        //create table "patternCategory"
        db.execSQL(
                "create table " + PATTERNCATEGORY_TABLE_NAME + "("+
                        PATTERNCATEGORY_COLUMN_PATTERNID + " integer, " +
                        PATTERNCATEGORY_COLUMN_CATEGORYID + " integer, " +
                        "PRIMARY KEY (" + PATTERNCATEGORY_COLUMN_PATTERNID + ", " + PATTERNCATEGORY_COLUMN_CATEGORYID + "), " +
                        "FOREIGN KEY (" + PATTERNCATEGORY_COLUMN_PATTERNID + ") REFERENCES " + PATTERN_TABLE_NAME + "(" + PATTERN_COLUMN_PATTERNID + "), " +
                        "FOREIGN KEY (" + PATTERNCATEGORY_COLUMN_CATEGORYID + ") REFERENCES " + CATEGORY_TABLE_NAME + "(" + CATEGORY_COLUMN_CATEGORYID + "))"
        );

        Log.d("myApp", "database crested");

    }

    //only called when version number is lower than requested in constructor to delete existing and call onCreate
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("myApp", "database upgraded");

        db.execSQL("DROP TABLE IF EXISTS " + BRAND_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PATTERNCATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PATTERN_TABLE_NAME);
        onCreate(db);
    }

    //new pattern
    public long insertPattern(Pattern pattern) {
        Log.d("myApp", "inserting entry into db");

        long id;

        SQLiteDatabase db = this.getWritableDatabase();

        //prepare row to insert
        ContentValues contentValues = new ContentValues();

        contentValues.put(PATTERN_COLUMN_PATTERNNUM, pattern.getNum());
        contentValues.put(PATTERN_COLUMN_BRANDID, pattern.getBrand());
        contentValues.put(PATTERN_COLUMN_SIZERANGE, pattern.getSizeRange());
        //what about categories? See below
        contentValues.put(PATTERN_COLUMN_DESCRIPTION, pattern.getDescription());
        contentValues.put(PATTERN_COLUMN_COVERIMAGE, pattern.getCoverImageLocn());
        contentValues.put(PATTERN_COLUMN_BACKIMAGE, pattern.getBackImageLocn());

        //insert row
        id = db.insert(PATTERN_TABLE_NAME, null, contentValues);

        //for each category
        for(int category: pattern.getCategory()) {
            ContentValues categoryValues = new ContentValues();
            categoryValues.put(PATTERNCATEGORY_COLUMN_PATTERNID, id);
            categoryValues.put(PATTERNCATEGORY_COLUMN_CATEGORYID, category);

            db.insert(PATTERNCATEGORY_TABLE_NAME, null, categoryValues);
        }

        Log.d("myApp", "db id is "+id);

        return id;
    }

    //TODO all the queries below
    //update pattern
    //delete pattern
    //get pattern by id
    //get pattern/s by pattern number
    //get patterns from keywords
    //get patterns from search criteria
    //get list of all patterns

    //new brand
    public void insertBrand(String brand){
        SQLiteDatabase db = this.getWritableDatabase();

        //prepare row to insert
        ContentValues contentValues = new ContentValues();

        contentValues.put(BRAND_COLUMN_BRAND, brand);

        //insert row
        db.insert(BRAND_TABLE_NAME, null, contentValues);
    }

    //update brand
    //delete brand
    //get brand by id

    //get list of brands
    public List<String> getAllBrands(){
        List<String> brands = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + BRAND_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                brands.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return brands;
    }

    //new category
    public void insertCategory(String brand){
        SQLiteDatabase db = this.getWritableDatabase();

        //prepare row to insert
        ContentValues contentValues = new ContentValues();

        contentValues.put(CATEGORY_COLUMN_CATEGORY, brand);

        //insert row
        db.insert(CATEGORY_TABLE_NAME, null, contentValues);
    }

    //update category
    //delete category
    //get category by id

    //get list of categories
    public List<String> getAllCategories(){
        List<String> categories = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + CATEGORY_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                categories.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }
}
