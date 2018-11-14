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

import java.util.ArrayList;
import java.util.List;

public class BrandListFragment extends ListFragment implements OnItemClickListener {
    DBHelper mydb;

    TextView mNewBrand;
    Button mAdd;

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

        setList();

        mNewBrand = v.findViewById(R.id.edit_list_newItem);

        mAdd = v.findViewById(R.id.edit_list_addButton);
        mAdd.setOnClickListener(clickAdd);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //adapter does this stuff
    }

    private View.OnClickListener clickAdd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        String newOne = mNewBrand.getText().toString();
        mydb.insertBrand(newOne);
        mNewBrand.setText("");

        setList();
        }
    };

    private void setList(){
        ArrayList<ListItem> brands = mydb.getAllBrandsDetails();

        BrandListAdapter brandAdapter = new BrandListAdapter(getActivity(), R.layout.listview_item_row_cat_or_brand_lists, brands);
        setListAdapter(brandAdapter);
    }
}
