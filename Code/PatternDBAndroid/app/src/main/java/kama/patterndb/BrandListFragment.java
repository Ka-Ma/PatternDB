package kama.patterndb;

import android.app.ListFragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BrandListFragment extends ListFragment implements OnItemClickListener {
    DBHelper mydb;

    TextView mNewBrand;
    Button mAdd; //TODO need to be able to edit brands list, add delete

    View v;

    public static BrandListFragment newInstance(){
        BrandListFragment f = new BrandListFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.edit_list, null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());

        List<String> brands = mydb.getAllBrands();
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, brands); //TODO may need to make custom row and therefore custom adapter to include a delete button

        setListAdapter(brandAdapter);
        getListView().setOnItemClickListener(this);

        mNewBrand = v.findViewById(R.id.edit_list_newItem);

        mAdd = v.findViewById(R.id.edit_list_addButton);
        mAdd.setOnClickListener(clickAdd);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        //TODO edit text of brand name
    }

    private View.OnClickListener clickAdd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String newOne = mNewBrand.getText().toString();
            mydb.insertBrand(newOne);
            Toast.makeText(v.getContext(), "Added a Brand: "+ newOne, Toast.LENGTH_SHORT).show();
            //TODO update list
        }
    };

    private View.OnClickListener clickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO delete selected item
            Toast.makeText(v.getContext(), "Deleted a Brand", Toast.LENGTH_SHORT).show();
            //TODO update list
        }
    };
}
