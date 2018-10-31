package kama.patterndb;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class EditListMenuFragment extends Fragment {
    //members
    Button mBrandButton;
    Button mCategoryButton;
    Button mBackButton;

    BrandListFragment brandListFragment;
    CategoryListFragment categoryListFragment;

    DBHelper mydb;

    public static EditListMenuFragment newInstance(){
        EditListMenuFragment f = new EditListMenuFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.edit_lists_menu, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());
        View v = getActivity().findViewById(R.id.fragment_container);

        mBrandButton = v.findViewById(R.id.button_brand);
        mBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brandListFragment = BrandListFragment.newInstance();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, brandListFragment);
                ft.addToBackStack(null);
                ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


            }
        });

        mCategoryButton = v.findViewById(R.id.button_category);
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryListFragment = CategoryListFragment.newInstance();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, categoryListFragment);
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
