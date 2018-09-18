package kama.patterndb;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



/**
 * Created by Kat on 2/08/2018.
 */

public class MenuFragment extends Fragment {

    Button mAddButton;
    Button mSearchButton;
    Button mEditListsButton;
    Button mExitButton;

    public static MenuFragment newInstance(){
        MenuFragment f = new MenuFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.main_menu, null);

        mAddButton = (Button) v.findViewById(R.id.button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // display on the same Activity

                AddFragment add = AddFragment.newInstance();

                FragmentTransaction ft =  getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, add);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                //setContentView(R.layout.add_edit_pattern);
            }
        });

        mSearchButton = (Button) v.findViewById(R.id.button_searchMain);
        mSearchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SearchFragment search = SearchFragment.newInstance();

                FragmentTransaction ft =  getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, search);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        mEditListsButton = (Button) v.findViewById(R.id.button_editLists);
        mEditListsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditListMenuFragment editList = EditListMenuFragment.newInstance();

                FragmentTransaction ft =  getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, editList);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        mExitButton = (Button) v.findViewById(R.id.button_exit);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                System.exit(0);
            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getActivity().findViewById(R.id.fragment_container);

        //link member variables to layout items
        //there are none now

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
