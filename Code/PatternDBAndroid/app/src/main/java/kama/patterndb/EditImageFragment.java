package kama.patterndb;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.net.Uri;


import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;


public class EditImageFragment extends Fragment {
    //members
    CropImageView mImageView;
    Button mRotateLButton;
    Button mRotateRButton;
    Button mDoneButton;

    Uri imagePathURI;
    Uri newImagePathURI;
    DBHelper mydb;
    View v;

    private static final String TAG = "myApp";

    public static EditImageFragment newInstance(Uri ip){
        EditImageFragment f = new EditImageFragment();

        //any args in Bundle
        Bundle args = new Bundle();
        args.putParcelable("imagePath", ip);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.edit_image, null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mydb = new DBHelper(getActivity());
        Log.d(TAG, "edit image fragment activity created");

        imagePathURI = getArguments().getParcelable("imagePath");
        Log.d(TAG, "imagePathUri is "+ imagePathURI.toString());
        Log.d(TAG, "path is "+ imagePathURI.getPath());

        newImagePathURI = Uri.parse(updatedFileName(imagePathURI));
        Log.d(TAG, "newImagePathUri is " + newImagePathURI.toString());
        Log.d(TAG, "path is "+ newImagePathURI.getPath());

        Bitmap imageBitmap = null;
        try{
            imageBitmap = MediaStore.Images.Media.getBitmap(v.getContext().getContentResolver(), imagePathURI); //BitmapFactory.decodeFile(imagePathURI);
        }catch (Exception e){
            Log.d(TAG, "there was an exception when trying to make bitmap: "+e);
        }

        Log.d(TAG, "imageBitmap is " + imageBitmap.toString() + " stats: " + imageBitmap.getWidth() + " and "+ imageBitmap.getHeight() + " other stuff " + imageBitmap.getAllocationByteCount());


        mImageView = (CropImageView) v.findViewById(R.id.cropImageView);
        mImageView.setImageBitmap(imageBitmap);
        mImageView.setCropMode(CropImageView.CropMode.FREE);

        mRotateLButton = (Button) v.findViewById(R.id.buttonRotateLeft);
        mRotateLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });

        mRotateRButton = (Button) v.findViewById(R.id.buttonRotateRight);
        mRotateRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        mDoneButton = (Button) v.findViewById(R.id.buttonDone);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "user clicked done");

                mImageView.crop(imagePathURI).execute(new CropCallback() {
                    @Override public void onSuccess(Bitmap cropped){
                        mImageView.save(cropped).execute(newImagePathURI, mSaveCallback); //FIXME java.lang.UnsupportedOperationException: No external updates -- maybe try checking what onClickListner does for capturing image
                    }
                    @Override public void onError(Throwable e){
                        Log.d(TAG, "error saving cropped image, error message: "+e);
                    }
                });

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

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri){
            Log.d(TAG, "in onSuccess");
/*
            Boolean deleted = v.getContext().deleteFile(imagePathURI.getPath());
            Log.d(TAG, "did the old file get deleted? "+ deleted);

            Log.d(TAG, "is the new file still there? Check the following list");
            String[] files = v.getContext().fileList();
            for(String file: files){
                Log.d(TAG, "--> "+file);
            }

            getFragmentManager().popBackStack();
*/
        }
        @Override public void onError(Throwable e) {
            Log.d(TAG, "Well shucks granma... the callback failed because "+e);
        }
    };


    private String updatedFileName(Uri u){
        String updated = null;
        String original = u.toString();

        //peel off .jpg, add E.jpg
        updated = original.substring(0, original.length() - 4) + "E.jpg";
        Log.d(TAG, "original = "+ original);
        Log.d(TAG, "updated = "+updated);

        return updated;
    }
}
