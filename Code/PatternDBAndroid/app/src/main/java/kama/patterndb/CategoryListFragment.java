package kama.patterndb;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class CategoryListFragment extends ListFragment implements OnItemClickListener {
    DBHelper mydb;

    View v;

    EditText mNewItem;
    Button mAdd;

    public static CategoryListFragment newInstance(){
        CategoryListFragment f = new CategoryListFragment();

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

        mNewItem = v.findViewById(R.id.edit_list_newItem);

        mAdd = v.findViewById(R.id.edit_list_addButton);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "new cat is "+ mNewItem.getText().toString(), Toast.LENGTH_SHORT).show();
                mydb.insertCategory(mNewItem.getText().toString());
                mNewItem.setText("");

                setList();

                //FIXME trying to get the on screen keyboard to disappear once the user is done with it.
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //This is managed by adapter
    }

    @Override
    public void onResume() {
        super.onResume();

        setList();

    }

    private void setList(){
        ArrayList<ListItem> categories = mydb.getAllCategoriesDetails();

        EditCategoryListAdapter categoryAdapter = new EditCategoryListAdapter(getActivity(), R.layout.listview_item_row_cat_or_brand_lists, categories);
        setListAdapter(categoryAdapter);
    }
}
