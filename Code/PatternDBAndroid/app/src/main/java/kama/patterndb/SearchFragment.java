package kama.patterndb;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchFragment extends Fragment {

    //members
    Button mSearchButton;
    Button mBackButton;
    EditText mPatternNum;
    EditText mKeywords;
    Spinner mCategory;
    ArrayAdapter<String> adapter;

    DBHelper mydb;

    public static SearchFragment newInstance(){
        SearchFragment f = new SearchFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.search, null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());
        View v = getActivity().findViewById(R.id.fragment_container);

        mPatternNum = (EditText) v.findViewById(R.id.text_patternNumber);

        mKeywords = (EditText) v.findViewById(R.id.text_keyword);

        /*mCategory = v.findViewById(R.id.spinner_category);
        adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, mydb.getAllCategories());
        mCategory.setAdapter(adapter);
*/
        mSearchButton = (Button) v.findViewById(R.id.button_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO need to enact query DB for pattern number and/or keywords and/or category/s and/or brand/s
                //TODO need to make PatternListFragment.java & custom listItem for patterns
                int[] brands = {1}; //get from the select list
                int[] categories = {1,2}; // get from the select list
                //get keywords from that field
                mydb.getPatternsMatching("keywords", brands, categories);
                Toast.makeText(v.getContext(), "I will search for something", Toast.LENGTH_SHORT).show();
            }
        });

        mBackButton = (Button) v.findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();

        Log.d("myApp", " frag started");
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("myApp", "resumed  fragment");

        //if anything needs refreshing do it here
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d("myApp", "paused  frag");
    }
}
