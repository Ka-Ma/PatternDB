package kama.patterndb;

/**
 * Created by Kat on 2/08/2018.
 */

public class Pattern {
    private String mNum;
    private int mBrand;
    private String mSizeRange;
    private int[] mCategory;
    private String mDescription;
    private String mCoverImageLocn;
    private String mBackImageLocn;

    Pattern(){
        mNum = null;
        mBrand = -1;
        mSizeRange = null;
        mCategory = null;
        mDescription = null;
        mCoverImageLocn = null;
        mBackImageLocn = null;
    }

    Pattern(String num, int brand, String sizeRange, int[] category, String description, String coverImg, String backImg){
        mNum = num;
        mBrand = brand;
        mSizeRange = sizeRange;
        mCategory = category;
        mDescription = description;
        mCoverImageLocn = coverImg;
        mBackImageLocn = backImg;
    }

    public String getNum(){
        return mNum;
    }

    public int getBrand(){
        return mBrand;
    }

    public String getSizeRange(){
        return mSizeRange;
    }

    public int[] getCategory(){
        return mCategory;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getCoverImageLocn(){
        return mCoverImageLocn;
    }

    public String getBackImageLocn(){
        return mBackImageLocn;
    }

    public void setNum(String num){
        mNum = num;
    }

    public void setBrand(int brand){
        mBrand = brand;
    }

    public void setSizeRange(String range){
        mSizeRange = range;
    }

    public void setCategory(int[] category){
        mCategory = category;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public void setCoverImageLocn(String locn){
        mCoverImageLocn = locn;
    }

    public void setBackImageLocn(String locn){
        mBackImageLocn = locn;
    }

}
