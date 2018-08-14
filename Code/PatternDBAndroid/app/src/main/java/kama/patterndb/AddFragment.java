package kama.patterndb;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Kat on 2/08/2018.
 */

public class AddFragment extends Fragment {

    //members
    Button mSaveButton;
    Button mBackButton;
    Button mOCRButton;
    Button mBackImageButton;
    Button mCoverImageButton;
    EditText mPatternNum;
    Spinner mBrand;
    EditText mSizeRange;
    Spinner mCategory;
    EditText mDescription;
    ImageView mCoverImage;
    ImageView mBackImage;

    DBHelper mydb;

    private static final int CAMERA_REQUEST_BACK = 1;
    private static final int CAMERA_REQUEST_FRONT = 2;

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
        mPatternNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO enable all the things that rely on a pattern number being there
                if(!mPatternNum.getText().toString().equals("")){
                    mOCRButton.setEnabled(TRUE);
                }else{
                    mOCRButton.setEnabled(FALSE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBrand = (Spinner) v.findViewById(R.id.spinner_brand);
        mDescription = (EditText) v.findViewById(R.id.text_description);
        mBackImage = (ImageView) v.findViewById(R.id.imgView_backImg);
        mCoverImage = (ImageView) v.findViewById(R.id.imgView_coverImg);

        mCoverImageButton = (Button) v.findViewById(R.id.button_coverImg);
        mCoverImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.d("myApp", "about to start camera activity");
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_FRONT);
                    //TODO need to be able to edit image for accepting
                    //TODO need to be able to set save location and file name
                } catch (Exception e) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
                }
            }
        });

        mBackImageButton = (Button) v.findViewById(R.id.button_backImg);
        mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.d("myApp", "about to start camera activity");
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_BACK);
                    //TODO need to be able to edit image for accepting
                    //TODO need to be able to set save location and file name
                } catch (Exception e) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
                }
            }
        });

        mOCRButton = (Button) v.findViewById(R.id.button_ocr);
        mOCRButton.setEnabled(FALSE);
        mOCRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBackImage != null){
                    Log.d("myApp", "clicked the OCR button");
                    Toast.makeText(v.getContext(), "I will send you off to select part of an image to scan for text", Toast.LENGTH_SHORT).show();
                    //TODO OCR STUFF
                    //do some ocr'ing
                    //select part of back image to ocr scan
                    //convert image to text and save in description edittext
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("myApp", "got activity result for camera");
        Log.d("myApp", "request code is "+ requestCode+ " and result code is "+resultCode);

        if(resultCode == RESULT_OK){
            Log.d("myApp", "image captured");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(requestCode == CAMERA_REQUEST_BACK){
                Log.d("myApp", "the codes for back are good");
                mBackImage.setImageBitmap(imageBitmap);
            }else if (requestCode == CAMERA_REQUEST_FRONT){
                Log.d("myApp", "the codes for front are good");
                mCoverImage.setImageBitmap(imageBitmap);
            }
        }
    }

}
