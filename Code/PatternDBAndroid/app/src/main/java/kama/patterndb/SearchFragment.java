package kama.patterndb;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    //members
    Button mSearchButton;
    Button mBackButton;
    EditText mPatternNum;
    SelectMultiSpinner mBrand;
    EditText mKeywords;
    SelectMultiSpinner mCategory;

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

        mPatternNum = v.findViewById(R.id.text_patternNumber); //TODO add a thing to deactivate the other option if this is complete

        mBrand = v.findViewById(R.id.search_spinner_brand);
        List<String> brandList = mydb.getAllBrandsNames();
        mBrand.setItems(brandList);

        mKeywords = v.findViewById(R.id.text_keyword);

        mCategory = v.findViewById(R.id.search_spinner_category);
        List<String> categoryList = mydb.getAllCategoriesNames();
        mCategory.setItems(categoryList);

        mSearchButton = v.findViewById(R.id.button_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need array of search criteria
                //String, string, string, string
                //pattern number, keywords, brands, categories
                ArrayList<String> searchCriteria = new ArrayList<String>();

                searchCriteria.add(mPatternNum.getText().toString());
                searchCriteria.add(mKeywords.getText().toString());
                searchCriteria.add(mBrand.getSelectedItemsAsString());
                searchCriteria.add(mCategory.getSelectedItemsAsString());

                Log.d("myApp", "search criteria is " + searchCriteria);

                //search button needs to launch new fragment
                PatternListFragment patternListFragment = PatternListFragment.newInstance(searchCriteria);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, patternListFragment);
                ft.addToBackStack(null);
                ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
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
