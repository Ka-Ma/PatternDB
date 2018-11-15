package kama.patterndb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditBrandListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<ListItem> list;
    private Context context;
    private int mLayoutResourceId;

    DBHelper mydb;

    public EditBrandListAdapter(Context context, int layoutResourceId, ArrayList<ListItem> list){
        this.list = list;
        this.context = context;
        this.mLayoutResourceId = layoutResourceId;

        mydb = new DBHelper(context);
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public ListItem getItem(int pos){
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return list.get(pos).getUID();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mLayoutResourceId, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = view.findViewById(R.id.textListCatOrBrand);
        listItemText.setText(list.get(position).getItem());

        //Handle buttons and add onClickListeners
        Button deleteBtn = view.findViewById(R.id.buttonDeleteCatOrBrandListItem);
        Button updateBtn = view.findViewById(R.id.buttonEditCatOrBrandListItem);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "deleting " + getItem(position).getItem(), Toast.LENGTH_SHORT).show();
                mydb.deleteBrand(getItem(position).getUID());
                notifyDataSetChanged();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                //opening dialog to edit
                AlertDialog.Builder updateDialog = new AlertDialog.Builder(v.getContext());
                updateDialog.setTitle("Update Brand");
                final EditText input = new EditText(v.getContext());
                input.setText(getItem(position).getItem());
                updateDialog.setView(input);

                updateDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mydb.updateBrand(getItem(position).getUID(), input.getText().toString());
                    }
                });

                updateDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                updateDialog.show();


                notifyDataSetChanged(); //FIXME this doesn't appear to update list
            }
        });

        return view;
    }
}
