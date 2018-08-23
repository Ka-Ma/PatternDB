package kama.patterndb;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.EffectContext;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.media.effect.Effect;
import android.media.effect.EffectFactory;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.media.effect.EffectFactory.EFFECT_CROP;
import static android.media.effect.EffectFactory.isEffectSupported;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Kat on 2/08/2018.
 */

public class AddFragment extends Fragment {
    //can i have the view here?
    View v;

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

    private static final String TAG = "myApp";
    private static final int CAMERA_REQUEST_BACK = 1;
    private static final int CAMERA_REQUEST_FRONT = 2;
    private Uri mCoverImageUri;
    private Uri mBackImageUri;
    private String mCoverImageFileName;
    private String mBackImageFileName;

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

        v = inflater.inflate(R.layout.add_edit_pattern, null);

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
                    mBackImageButton.setEnabled(TRUE);
                    mCoverImageButton.setEnabled(TRUE);
                }else{
                    mOCRButton.setEnabled(FALSE);
                    mBackImageButton.setEnabled(FALSE);
                    mCoverImageButton.setEnabled(FALSE);
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
        mCoverImageButton.setEnabled(FALSE);
        mCoverImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.d(TAG, "about to start camera activity where in the file will saved locally");
                    Log.d(TAG, "camera intent has " + cameraIntent.toString());
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile("cover");
                        } catch (IOException ex) {
                            Log.d(TAG, "oh no, file for photo could not be made: " + ex);
                        }

                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(v.getContext(), "kama.patterndb.provider", photoFile);
                            mCoverImageUri = photoURI;
                            Log.d(TAG, "photoURI is " + photoURI.toString());
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            Log.d(TAG, "camera intent now has " + cameraIntent.toString());
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_FRONT);
                        }

                    }
                }catch (Exception e) {
                            Log.d(TAG, "tried to start intent and there was an exception somewhere in there that says: "+e);
                            Toast.makeText(v.getContext().getApplicationContext(), "Exception: "+ e, Toast.LENGTH_LONG).show();
                }

            }
        });

        mBackImageButton = (Button) v.findViewById(R.id.button_backImg);
        mBackImageButton.setEnabled(FALSE);
        mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.d(TAG, "about to start camera activity where in the file will saved locally");
                    Log.d(TAG, "camera intent has " + cameraIntent.toString());
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile("back");
                        } catch (IOException ex) {
                            Log.d(TAG, "oh no, file for photo could not be made: " + ex);
                        }

                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(v.getContext(), "kama.patterndb.provider", photoFile);
                            mBackImageUri = photoURI;
                            Log.d(TAG, "photoURI is " + photoURI.toString());
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            Log.d(TAG, "camera intent now has " + cameraIntent.toString());
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_BACK);
                        }

                    }
                }catch (Exception e) {
                    Log.d(TAG, "tried to start intent and there was an exception somewhere in there that says: "+e);
                    Toast.makeText(v.getContext().getApplicationContext(), "Exception: "+ e, Toast.LENGTH_LONG).show();
                }
            }
        });

        mCoverImage = (ImageView) v.findViewById(R.id.imgView_coverImg);
        mCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment editImage = EditImageFragment.newInstance(mCoverImageFileName);

                FragmentTransaction ft =  getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, editImage);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        mOCRButton = (Button) v.findViewById(R.id.button_ocr);
        mOCRButton.setEnabled(FALSE);
        mOCRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBackImage != null){
                    Log.d(TAG, "clicked the OCR button");
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

        Log.d(TAG, " frag started");
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d(TAG, "resumed  fragment");

        //if anything needs refreshing do it here
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d(TAG, "paused  frag");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "got activity result for camera");
        Log.d(TAG, "request code is "+ requestCode+ " and result code is "+resultCode + " where OK is " + RESULT_OK);

        if(resultCode == RESULT_OK){
            Log.d(TAG, "image captured");

            if(requestCode == CAMERA_REQUEST_BACK){
                Log.d(TAG, "the codes for back are good");
                Bitmap imageBitmap = BitmapFactory.decodeFile(mBackImageFileName);
                mBackImage.setImageBitmap(imageBitmap);

            }else if (requestCode == CAMERA_REQUEST_FRONT){
                Log.d(TAG, "the codes for front are good");
                resizeCoverImage();
                Bitmap imageBitmap = BitmapFactory.decodeFile(mCoverImageFileName);
                mCoverImage.setImageBitmap(imageBitmap);
                
                Log.d(TAG, "uri is "+ mCoverImageUri.toString());
            }
        }
    }

    private File createImageFile(String side) throws IOException {
        String imageFileName = mPatternNum.getText().toString() + "_" + side + "_";
        Log.d(TAG, "filename is "+ imageFileName);
        File storageDir = v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "storageDir is "+ storageDir);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        Log.d("mApp", "file just created is "+ image.toString());

        if(side.equals("cover")){
            mCoverImageFileName = image.getAbsolutePath();
        }else if(side.equals("back")){
            mBackImageFileName = image.getAbsolutePath();
        }

        return image;
    }

    private void editImage(String side){
        //get appropriate side open in editor
        CropImageView cropImageView;
        //TODO need to think about layout/structure
        //cropImageView.setImageUriAsync(mCoverImageUri)
        //overwrite it
    }

    private void resizeCoverImage() {
         Log.d(TAG, "making cover image smaller to save space");
        //TODO change size of cover image to match device screen size
        Bitmap image = BitmapFactory.decodeFile(mCoverImageFileName);
        Log.d(TAG, "cover image stats: height"+ image.getHeight()+", width "+image.getWidth()+", size (byte count) "+ image.getAllocationByteCount());

        if(isEffectSupported(EFFECT_CROP)){
            Log.d(TAG, "effect crop is supported");
            /*
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width =  dm.widthPixels;
            EffectContext ec = EffectContext.createWithCurrentGlContext();
            EffectFactory ef = ec.getFactory();
            Effect e = ef.createEffect(EFFECT_CROP);
            glTexImage2D();
            e.apply(in, width, height, out); //need to do some more searching
            */
        }
    }

}
