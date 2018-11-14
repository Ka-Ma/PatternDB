package kama.patterndb;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.media.effect.EffectFactory.EFFECT_CROP;
import static android.media.effect.EffectFactory.isEffectSupported;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Kat on 2/08/2018.
 */

public class AddEditPatternFragment extends Fragment {  //TODO refactor to allow for editing the pattern, add delete button, make sure proper buttons are enabled at the right time
    //logging purposes
    private static final String TAG = "myApp";

    //making view available all over
    View v;

    //layout members
    Button mSaveButton;
    Button mBackButton;
    Button mDeleteButton;
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

    //other stuff
    DBHelper mydb;
    private static Boolean mEditing = false;

    //intent request codes
    private static final int CAMERA_REQUEST_BACK = 1;
    private static final int CAMERA_REQUEST_FRONT = 2;
    private static final int CROP_REQUEST_COVER = 3;

    private Uri mCoverImageUri;
    private Uri mBackImageUri;
    private String mCoverImageFileName;
    private String mBackImageFileName;


    public static AddEditPatternFragment newInstance(){
        AddEditPatternFragment f = new AddEditPatternFragment();

        return f;
    }

    public static AddEditPatternFragment newInstance(Pattern p){ //TODO check this overloading didn't break it in the long run
        AddEditPatternFragment f = new AddEditPatternFragment();

        //any args in Bundle
        Bundle args = new Bundle();
        args.putParcelable("pattern", p);
        f.setArguments(args);
        mEditing = true;

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

        bindMembers();

        //for testing purposes:
        mydb.insertBrand("TestB1");
        mydb.insertCategory("TestC1");

        setListeners();
        loadData();

        setDependents(mEditing);  //This disables or enables subsequent fields dependent on the Pattern Number field being complete

        if(mEditing){
            Pattern p = getArguments().getParcelable("pattern");
            if(p.getUID() != -1){
                populateFields(p);
            }
        }
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

    private void bindMembers() {
        mydb = new DBHelper(getActivity());

        mPatternNum = v.findViewById(R.id.text_patternNumber);
        mBrand = v.findViewById(R.id.spinner_brand);
        mDescription = v.findViewById(R.id.text_description);
        mSizeRange = v.findViewById(R.id.text_size);
        mCategory = v.findViewById(R.id.spinner_category);
        mBackImage = v.findViewById(R.id.imgView_backImg);
        mCoverImage = v.findViewById(R.id.imgView_coverImg);
        mCoverImageButton = v.findViewById(R.id.button_coverImg);
        mBackImageButton = v.findViewById(R.id.button_backImg);
        mOCRButton = v.findViewById(R.id.button_ocr);
        mSaveButton = v.findViewById(R.id.button_save);
        mBackButton = v.findViewById(R.id.button_back);
        mDeleteButton = v.findViewById(R.id.button_delete);
    }

    private void setDependents(boolean state) {
        mBrand.setEnabled(state);
        mDescription.setEnabled(state);
        mSizeRange.setEnabled(state);
        mCategory.setEnabled(state);
        mBackImage.setEnabled(state);
        mCoverImage.setEnabled(state);
        mCoverImageButton.setEnabled(state);
        mBackImageButton.setEnabled(state);
        mOCRButton.setEnabled(state);
        mSaveButton.setEnabled(state);
    }

    private void setListeners(){
        mPatternNum.addTextChangedListener(patternNumWatcher);
        mCoverImageButton.setOnClickListener(coverImageCapture);
        mBackImageButton.setOnClickListener(backImageCapture);
        mCoverImage.setOnClickListener(coverImageEdit);
        mOCRButton.setOnClickListener(ocrAction);
        mSaveButton.setOnClickListener(clickSave);
        mBackButton.setOnClickListener(clickBack);
        mDeleteButton.setOnClickListener(clickDelete);
    }

    private void loadData() {
        List<String> brands = mydb.getAllBrands();
        List<String> categories = mydb.getAllCategories();

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_dropdown_item, brands);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_dropdown_item, categories);

        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBrand.setAdapter(brandAdapter);
        mCategory.setAdapter(categoryAdapter);
    }

    private void populateFields(Pattern p){
        mPatternNum.setText(p.getNum());
        mBrand.setSelection(p.getBrand());
        mDescription.setText(p.getDescription());
        mSizeRange.setText(p.getSizeRange());
        //mCategory.setSelection(p.getCategory()); //TODO set multiple selection
        //mBackImage; //TODO once images are sorted out, need to populate field
        //mCoverImage;
    }

    private void clearFields(){
        mPatternNum.setText("");
        mBrand.setSelection(0);
        mDescription.setText("");
        mSizeRange.setText("");
        mCategory.setSelection(0);
        //mBackImage; //TODO once images are sorted out, need to clear field
        //mCoverImage;
    }

    private final TextWatcher patternNumWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!mPatternNum.getText().toString().equals("")){
                setDependents(TRUE);
            }else{
                setDependents(FALSE);
            }
        }

        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener coverImageCapture = new View.OnClickListener() {
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
                        Uri photoURI = FileProvider.getUriForFile(v.getContext(), "kama.patterndb.provider", photoFile); //TODO note trying different way to get URI
                        Log.d(TAG, "photoURI is " + photoURI.toString());
                        mCoverImageUri = Uri.parse(mCoverImageFileName);
                        Log.d(TAG, "mCoverImageURI is " + mCoverImageUri.toString());
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
    };

    private View.OnClickListener backImageCapture = new View.OnClickListener() { //TODO compare cover and back onClickListeners, can they be combined?
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
    };

    private View.OnClickListener coverImageEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //FIXME I think this needs to be an intent in order for the uri permissions to change things to transfer but looks problematic
            /*Intent cropIntent = new Intent();
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCoverImageUri);
            startActivityForResult(cropIntent, CROP_REQUEST_COVER);
*/
            /*
            EditImageFragment editImage = EditImageFragment.newInstance(FileProvider.getUriForFile(v.getContext(), "kama.patterndb.provider", new File(mCoverImageFileName)));

            FragmentTransaction ft =  getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, editImage);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            */

            Toast.makeText(v.getContext(), "Eventually this will let you edit the image and that edit will be saved", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener ocrAction = new View.OnClickListener() {
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
    };

    private View.OnClickListener clickSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Boolean carryOn = false;
            String patternNum = mPatternNum.getText().toString();
            int brandID = (int) mBrand.getSelectedItemId();
            String size = mSizeRange.getText().toString();
            int[] category;
            String description = mDescription.getText().toString();
            String cover = "location of cover image"; //TODO once the image saving problem is resolved need to work on this
            String back = "location of back image";
            Pattern pattern = null;

            //there are some/most elements that need to be complete before the pattern can be saved.
            if(patternNum.equals("")){
                Toast.makeText(v.getContext(),"Please enter a pattern number", Toast.LENGTH_SHORT).show();

            }else{
                carryOn = true;

                category = new int[] {1,2}; //TODO get list of category ids
                //TODO get and set the various variables to pass to the pattern
                //String num, int brand, String sizeRange, int[] category, String description, String coverImg, String backImg
                pattern = new Pattern(patternNum, brandID, size, category, description, "directory for cover image", "directory for back image");
            }

            if (mEditing && carryOn){
                Pattern p = getArguments().getParcelable("pattern");
                pattern.setUID(p.getUID());

                mydb.updatePattern(pattern);

                Toast.makeText(v.getContext(),"Updated " + pattern.getNum(), Toast.LENGTH_SHORT).show();

                getFragmentManager().popBackStack();
            }else if (carryOn){
                int id = mydb.insertPattern(pattern);
                Toast.makeText(v.getContext(), "pattern "+pattern.getNum()+" saved as "+ id, Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }
    };

    private View.OnClickListener clickBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().popBackStack();
        }
    };

    private View.OnClickListener clickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mPatternNum.getText().toString();
            Pattern p = getArguments().getParcelable("pattern");
            int id = p.getUID();
            mydb.deletePattern(id);
            Toast.makeText(v.getContext(), "pattern "+name+" deleted", Toast.LENGTH_SHORT).show();

            getFragmentManager().popBackStack();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "got activity result for camera");
        Log.d(TAG, "request code is "+ requestCode+ " and result code is "+resultCode + " where OK is " + RESULT_OK);

        if(resultCode == RESULT_OK){
            Log.d(TAG, "image captured");
            Bitmap imageBitmap;

            switch(requestCode){
                case CAMERA_REQUEST_BACK:
                    Log.d(TAG, "the codes for back are good");
                    imageBitmap = BitmapFactory.decodeFile(mBackImageFileName);
                    mBackImage.setImageBitmap(imageBitmap);
                    break;

                case CAMERA_REQUEST_FRONT:
                    Log.d(TAG, "the codes for front are good");
                    resizeCoverImage();
                    imageBitmap = BitmapFactory.decodeFile(mCoverImageFileName);
                    mCoverImage.setImageBitmap(imageBitmap);

                    Log.d(TAG, "uri is "+ mCoverImageUri.toString());
                    break;
                case CROP_REQUEST_COVER:
                    Log.d(TAG, "the crop happened i guess");
                    break;

                default:
                    Log.d(TAG, "something unrecognised ");
                    break;

            }
        }
    }

    private File createImageFile(String side) throws IOException {
        String imageFileName = mPatternNum.getText().toString() + "_" + side + "_";
        Log.d(TAG, "filename is "+ imageFileName);
        File storageDir = v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "storageDir is "+ storageDir);
        File image = new File(storageDir, imageFileName + ".jpg");

        Log.d(TAG, "file just created is "+ image.toString());
        Log.d(TAG, "and it exists? " + image.exists());

        if(side.equals("cover")){
            mCoverImageFileName = image.getAbsolutePath();
        }else if(side.equals("back")){
            mBackImageFileName = image.getAbsolutePath();
        }

        return image;
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
