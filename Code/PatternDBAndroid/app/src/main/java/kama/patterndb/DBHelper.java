package kama.patterndb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
    public static final String BRAND_TABLE_NAME = "brandTable";
    public static final String BRAND_COLUMN_BRANDID = "brandID";
    public static final String BRAND_COLUMN_BRAND = "brand";

    //table "category"
    public static final String CATEGORY_TABLE_NAME = "categoryTable";
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
    static int ver = 3;

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
    public List<Pattern> getAllPatterns(){
        List<Pattern> patterns = new ArrayList<Pattern>();

        String selectQuery = "SELECT * FROM " + PATTERN_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String subQuery = "SELECT * FROM " + PATTERNCATEGORY_TABLE_NAME + " WHERE " + PATTERNCATEGORY_COLUMN_PATTERNID + " = ? ";

        if(cursor.moveToFirst()){
            do{
                Pattern p = new Pattern();

                p.setNum(cursor.getString(cursor.getColumnIndex(PATTERN_COLUMN_PATTERNNUM))); //String num,
                p.setBrand(cursor.getInt(cursor.getColumnIndex(PATTERN_COLUMN_BRANDID)));  //int brand,
                p.setSizeRange(cursor.getString(cursor.getColumnIndex(PATTERN_COLUMN_SIZERANGE))); //String sizeRange,
                //sub query for associative table
                Cursor subCursor = db.rawQuery(subQuery, new String[] {Long.toString(cursor.getLong(cursor.getColumnIndex(PATTERN_COLUMN_PATTERNID)))});
                List<Integer> categories = new ArrayList<>();
                if(subCursor.moveToFirst()){
                    do{
                        categories.add(subCursor.getInt(subCursor.getColumnIndex(PATTERNCATEGORY_COLUMN_CATEGORYID)));
                    } while (subCursor.moveToNext());
                    subCursor.close();
                }
                p.setCategory(convert(categories));
                p.setDescription(cursor.getString(cursor.getColumnIndex(PATTERN_COLUMN_DESCRIPTION)));// String description,
                p.setCoverImageLocn(cursor.getString(cursor.getColumnIndex(PATTERN_COLUMN_COVERIMAGE)));// String coverImg,
                p.setBackImageLocn(cursor.getString(cursor.getColumnIndex(PATTERN_COLUMN_BACKIMAGE)));// String backImg

                patterns.add(p);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return patterns;
    }

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
    public int updateBrand(long id, String brand){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BRAND_COLUMN_BRANDID, id);
        contentValues.put(BRAND_COLUMN_BRAND, brand);

        String whereClause = BRAND_COLUMN_BRANDID + " = " + id;

        int changeCount =  db.update(BRAND_TABLE_NAME, contentValues, whereClause, null);

        db.close();

        return changeCount;
    }

    //delete brand
    public void deleteBrand(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(BRAND_TABLE_NAME, BRAND_COLUMN_BRANDID + " = ? ", new String[] {Long.toString(id)});

        db.close();
    }

    //get number of brands
    public int numBrands(){
        SQLiteDatabase db = this.getReadableDatabase();

        return (int) DatabaseUtils.queryNumEntries(db, BRAND_TABLE_NAME);
    }

    //get brand by name
    public String findBrand(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + BRAND_TABLE_NAME + " WHERE " + BRAND_COLUMN_BRANDID + "=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex(BRAND_COLUMN_BRANDID));
    }

    //get brand by id
    public long findBrand(String brand){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + BRAND_TABLE_NAME + " WHERE " + BRAND_COLUMN_BRAND + " = ? ";

        Cursor cursor = db.rawQuery(selectQuery, new String[] {brand});

        cursor.moveToFirst();

        return cursor.getLong(cursor.getColumnIndex(BRAND_COLUMN_BRANDID));
    }

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
    public void insertCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();

        //prepare row to insert
        ContentValues contentValues = new ContentValues();

        contentValues.put(CATEGORY_COLUMN_CATEGORY, category);

        //insert row
        db.insert(CATEGORY_TABLE_NAME, null, contentValues);
    }

    //update category
    public void updateCategory(long id, String category){
        SQLiteDatabase db = this.getWritableDatabase();

        //prep new values
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COLUMN_CATEGORY, category);

        //run update query
        db.update(CATEGORY_TABLE_NAME, contentValues, CATEGORY_COLUMN_CATEGORYID + " = ? ", new String[] {Long.toString(id)});

        db.close();
    }

    //delete category
    public void deleteCategory(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(CATEGORY_TABLE_NAME, CATEGORY_COLUMN_CATEGORYID + " = ? ", new String[] {Long.toString(id)});

        db.close();
    }

    //get category by name
    public long findCategory(String category){
        long id;

        String selectQuery = "SELECT * FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_COLUMN_CATEGORY + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, new String[] {category});

        cursor.moveToFirst();

        id = cursor.getLong(cursor.getColumnIndex(CATEGORY_COLUMN_CATEGORYID));

        cursor.close();
        db.close();

        return id;
    }

    //get category by id
    public String findCategory(long id){
        String result;

        String selectQuery = "SELECT * FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_COLUMN_CATEGORYID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        result = cursor.getString(1);

        cursor.close();
        db.close();

        return result;
    }

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

    private int[] convert(List<Integer> list){
        int size = list.size();
        int[] result = new int[size];

        for(int i = 0; i<size; i++){
            result[i] = list.get(i);
        }

        return result;
    }
}
