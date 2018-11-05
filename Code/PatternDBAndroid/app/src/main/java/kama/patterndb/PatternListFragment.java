package kama.patterndb;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PatternListFragment extends ListFragment implements OnItemClickListener {
    DBHelper mydb;

    View v;


    public static PatternListFragment newInstance(ArrayList<Pattern> list){
        PatternListFragment f = new PatternListFragment();

        //any args in Bundle
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", list);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.listview_pattern, null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());

        List<Pattern> patternList = getArguments().getParcelableArrayList("list");
        PatternAdapter adapter = new PatternAdapter(v.getContext(), R.layout.listview_item_row_pattern, patternList);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        Log.d("myApp", "position = " + position + ", id = " + id);
        Log.d("myApp", "somethign meaningful? " + parent.getItemAtPosition(position)); //it's a pattern! yay

        AddEditPatternFragment edit = AddEditPatternFragment.newInstance((Pattern)parent.getItemAtPosition(position));

        FragmentTransaction ft =  getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, edit);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}