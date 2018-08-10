package kama.patterndb;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Kat on 2/08/2018.
 */

public class AddFragment extends Fragment {

    //members
    Button mSaveButton;
    Button mBackButton;
    Button mOCRButton;
    Button mBackImageButton;
    EditText mPatternNum;
    Spinner mBrand;
    EditText mSizeRange;
    Spinner mCategory;
    EditText mDescription;
    EditText mCoverImage;
    ImageView mBackImage;

    DBHelper mydb;

    public static AddFragment newInstance(){
        AddFragment f = new AddFragment();

        //any args in Bundle
        //Bundle args = new Bundle();
        //args.putInt("index", 0);
        //f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.add_edit_pattern, null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());
        View v = getActivity().findViewById(R.id.fragment_container);

        mPatternNum = (EditText) v.findViewById(R.id.text_patternNumber);
        mBrand = (Spinner) v.findViewById(R.id.spinner_brand);
        mDescription = (EditText) v.findViewById(R.id.text_description);
        mBackImage = (ImageView) v.findViewById(R.id.imgView_backImg);

        mBackImageButton = (Button) v.findViewById(R.id.button_backImg);
        mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 22);
                    //TODO need to be able to edit image for accepting
                    //TODO need to be able to set save location and file name
                } catch (Exception e) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
                }
            }
        });

        mOCRButton = (Button) v.findViewById(R.id.button_ocr);
        mOCRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBackImage != null){
                    //do some ocr'ing
                }
            }
        });

        mSaveButton = (Button) v.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patternNum = mPatternNum.getText().toString();
                int brandID;
                String size;
                int[] category;
                String description;
                String cover = "location of cover image";
                String back = "location of back image";

                //there are some/most elements that need to be complete before the pattern can be saved.
                if(patternNum.equals("")){
                    Toast.makeText(v.getContext(),"Please enter a pattern number", Toast.LENGTH_SHORT).show();
                }else{
                    //String num, int brand, String sizeRange, int[] category, String description, String coverImg, String backImg
                    category = new int[] {1,2}; //TODO get list of category ids
                    //TODO get and set the various variables to pass to the pattern
                    Pattern pattern = new Pattern(patternNum, 1, "Size", category, "description", "directory for cover image", "directory for back image");
                    Long id = mydb.insertPattern(pattern);
                    Toast.makeText(v.getContext(), "pattern "+id+" saved", Toast.LENGTH_SHORT).show();
                    //TODO clear or refresh the add screen to empty fields
                }
            }
        });

        mBackButton = (Button) v.findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO need to go back to previous fragment, also need to activate that ability in the native back button.
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
