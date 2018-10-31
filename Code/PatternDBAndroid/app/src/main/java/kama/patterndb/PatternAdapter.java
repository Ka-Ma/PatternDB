package kama.patterndb;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PatternAdapter extends ArrayAdapter<Pattern> {

    Context mContext;
    int mLayoutResourceId;
    List<Pattern> mData = null;

    DBHelper mydb;

    public PatternAdapter(Context context, int layoutResourceId, List <Pattern> data){
        super(context, layoutResourceId, data);
        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mData = data;

        mydb = new DBHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        PatternHolder holder = null;

        if(row == null){
            LayoutInflater inflater =  LayoutInflater.from(mContext);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new PatternHolder();
            holder.imgPatternCover = row.findViewById(R.id.imgView_coverImg);
            holder.textPatternNum = row.findViewById(R.id.textListPatternNum);
            holder.textPatternSize = row.findViewById(R.id.textListPatternSize);
            holder.textPatternCategories = row.findViewById(R.id.textListPatternCategories);

            row.setTag(holder);
        }else{
            holder = (PatternHolder)row.getTag();
        }

        Pattern pattern = mData.get(position);

        String brand = mydb.getBrand(pattern.getBrand());
        String categories = "";
        for(int cat : pattern.getCategory()){
            categories = categories.concat(mydb.getCategory(cat)+ ", ");
        }
        categories = categories.substring(0, categories.length() - 2);

        holder.textPatternNum.setText(brand + ": " + pattern.getNum());
        holder.textPatternSize.setText("Sizes: " + pattern.getSizeRange());
        holder.textPatternCategories.setText("Categories: " + categories);
        //holder.imgPatternCover.setImageResource(pattern.getCoverImageLocn()); //TODO retrieve cover image to set image in list

        return row;
    }

    static class PatternHolder
    {
        ImageView imgPatternCover;
        TextView textPatternNum;
        TextView textPatternSize;
        TextView textPatternCategories;
    }
}
