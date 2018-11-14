package kama.patterndb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kat on 2/08/2018.
 */

public class Pattern implements Parcelable{
    private int mUID;
    private String mNum;
    private int mBrand;
    private String mSizeRange;
    private int[] mCategory;
    private String mDescription;
    private String mCoverImageLocn;
    private String mBackImageLocn;

    Pattern(){
        mUID = -1;
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

    public int getUID() { return mUID;}

    public String getNum(){
        return mNum;
    }

    public int getBrand(){ return mBrand; }

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

    public void setUID(int uid){
        mUID = uid;
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


    //Parcel Stuff
    public Pattern(Parcel in){
        this.mUID = in.readInt();
        this.mNum = in.readString();
        this.mBrand = in.readInt();
        this.mSizeRange = in.readString();
        this.mCategory = in.createIntArray();
        this.mDescription = in.readString();
        this.mCoverImageLocn = in.readString();
        this.mBackImageLocn = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUID);
        dest.writeString(this.mNum);
        dest.writeInt(this.mBrand);
        dest.writeString(this.mSizeRange);
        dest.writeIntArray(this.mCategory);
        dest.writeString(this.mDescription);
        dest.writeString(this.mCoverImageLocn);
        dest.writeString(this.mBackImageLocn);
    }

    /*public static Parcelable.Creator<Pattern> getCreator(){
        return CREATOR;
    }

    public static void setCreator(Creator<Pattern> creator){
        CREATOR = creator;
    }
*/
    public static Parcelable.Creator CREATOR = new Parcelable.Creator<Pattern>() {
        public Pattern createFromParcel(Parcel in){
            return new Pattern(in);
        }

        public Pattern[] newArray(int size){
            return new Pattern[size];
        }
    };
}
