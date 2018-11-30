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

    //TODO need to have this fragment update list on return from edit or delete
    //FIXME when scrolling to the bottom of the list on actual phone it crashes, why?

    public static PatternListFragment newInstance(ArrayList<String> searchCriteria){ //TODO I think i need to update this to send the search criteria
        PatternListFragment f = new PatternListFragment();

        //any args in Bundle
        Bundle args = new Bundle();
        args.putStringArrayList("criteria", searchCriteria);
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
    public void onResume() {
        super.onResume();

        //update the list
        setList();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());

        setList();

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

    public void setList(){
        ArrayList<String> searchCriteria = getArguments().getStringArrayList("criteria");
        //where searchCriteria array is 0:pattern number, 1:keywords, 2:brands, 3:categories

        ArrayList<Pattern> patternList = null;

        //if pattern num is not null //TODO check search criteria works as expected
        if(!searchCriteria.get(0).equals("")){
            patternList = (ArrayList) mydb.getPatternByPatternNum(searchCriteria.get(0));
        }else if(!searchCriteria.get(1).equals("")){
            String keywords = searchCriteria.get(1);
            int[] brands = convertStringListToIntList(searchCriteria.get(2), "brand");
            int[] categories = convertStringListToIntList(searchCriteria.get(3), "category");

            patternList = (ArrayList) mydb.getPatternsMatching(keywords, brands, categories);
        }else{
            patternList = (ArrayList) mydb.getPatternsMatching("", null, null);
        }

        PatternAdapter adapter = new PatternAdapter(v.getContext(), R.layout.listview_item_row_pattern, patternList);

        setListAdapter(adapter);
    }

    private int[] convertStringListToIntList(String list, String type){
        int[] intList = null;
        ArrayList al = new ArrayList();
        int index = 0;

        while(index < list.lastIndexOf(",")) {
            int next = list.indexOf(",", index);
            al.add(list.substring(index, next));
            index = next;
        }

        intList = new int[al.size()];
        for(int i = 0; i<al.size(); i++){
            if(type.equals("brand")){
            intList[i] = mydb.getBrand((String) al.get(i));
            }
            else if (type.equals("category")){
                intList[i] = mydb.getCategory((String) al.get(i));
            }
        }

        Log.d("myApp", "this array list is " + intList.toString());

        return intList;
    }
}
