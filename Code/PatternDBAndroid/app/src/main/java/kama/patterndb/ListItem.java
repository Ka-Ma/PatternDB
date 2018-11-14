package kama.patterndb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kat on 14/11/2018.
 */

public class ListItem implements Parcelable{
    private int mUID;
    private String mItem;

    ListItem(){
        mUID = -1;
    }

    ListItem(String item){
        mItem = item;
    }

    public int getUID() { return mUID;}

    public String getItem(){
        return mItem;
    }

    public void setUID(int uid){
        mUID = uid;
    }

    public void setItem(String item){
        mItem = item;
    }

    //Parcel Stuff
    public ListItem(Parcel in){
        this.mUID = in.readInt();
        this.mItem = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUID);
        dest.writeString(this.mItem);
    }

    public static Creator CREATOR = new Creator<ListItem>() {
        public ListItem createFromParcel(Parcel in){
            return new ListItem(in);
        }

        public ListItem[] newArray(int size){
            return new ListItem[size];
        }
    };
}
