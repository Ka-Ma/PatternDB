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

import java.util.ArrayList;
import java.util.List;

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

        //TODO need to add the multiselectors for brand and category

        mPatternNum = v.findViewById(R.id.text_patternNumber); //TODO add a thing to deactivate the other option if this is complete

        mKeywords = v.findViewById(R.id.text_keyword);

        /*mCategory = v.findViewById(R.id.spinner_category);
        adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, mydb.getAllCategories());
        mCategory.setAdapter(adapter);
*/
        mSearchButton = v.findViewById(R.id.button_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Pattern> patternList = null;

                Log.d("myApp", "pattern num '" + mPatternNum.getText().toString()+"'");
                Log.d("myApp", "keywords '" + mKeywords.getText().toString()+"'");

                //if pattern num is not null
                if(!mPatternNum.getText().toString().equals("")){
                    patternList = (ArrayList) mydb.getPatternByPatternNum(mPatternNum.getText().toString());
                }else if(!mKeywords.getText().toString().equals("")){
                    String keywords = mKeywords.getText().toString();
                    int[] brands = null; //TODO get from the select list, may be null
                    int[] categories = null; // TODO get from the select list, may be null

                    patternList = (ArrayList) mydb.getPatternsMatching(keywords, brands, categories);
                }else{
                    patternList = (ArrayList) mydb.getPatternsMatching("", null, null);
                }


                //search button needs to launch new fragment
                if(patternList != null) {
                    PatternListFragment patternListFragment = PatternListFragment.newInstance(patternList);
                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, patternListFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            }
        });

        mBackButton = v.findViewById(R.id.button_back);
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
