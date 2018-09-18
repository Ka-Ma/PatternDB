package kama.patterndb;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.net.Uri;
import android.widget.Toast;


import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;


public class EditImageFragment extends Fragment {
    //layout members
    CropImageView mImageView;
    Button mRotateLButton;
    Button mRotateRButton;
    Button mDoneButton;
    Button mNextButton; // for debugging

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

        Log.d(TAG, "edit image fragment activity created");

        mydb = new DBHelper(getActivity());

        imagePathURI = getArguments().getParcelable("imagePath");
        Log.d(TAG, "imagePathUri is "+ imagePathURI.toString());
        Log.d(TAG, "path is "+ imagePathURI.getPath());

        //create new image file //TODO change this to create a new file and use ContentResolver to get the uri, maybe
        File photoFile = null;
        try {
            photoFile = createImageFile(updatedFileName(imagePathURI));
        } catch (IOException ex) {
            Log.d(TAG, "oh no, file for photo could not be made: " + ex);
        }

        if (photoFile != null) {
            Log.d(TAG, "photoFile is not null so newImagePathURI is given an address");
            newImagePathURI = FileProvider.getUriForFile(v.getContext(), "kama.patterndb.provider", photoFile);
            v.getContext().grantUriPermission("kama.patterndb", newImagePathURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else{Log.d(TAG, "photoFile is null, so that's your problem");}

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
        mImageView.setCropEnabled(true);


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

        mNextButton = v.findViewById(R.id.buttonNEXT);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //The idea is to cycle through the images that are saved.
            }
        });

        mDoneButton = (Button) v.findViewById(R.id.buttonDone);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "user clicked done");
                /*Log.d(TAG, "what files are there? Check the following list");
                String[] files = v.getContext().fileList();
                for(String file: files){
                    Log.d(TAG, "--> "+file);
                }

                Log.d(TAG, "what files are in the cache? Check the following list");
                File[] filesCache = v.getContext().getCacheDir().listFiles();
                for(File file: filesCache){
                    Log.d(TAG, "--> "+file.toString());
                }*/

                mImageView.crop(imagePathURI).execute(new CropCallback() {
                    @Override public void onSuccess(Bitmap cropped){
                        mImageView.save(cropped)
                                .execute(/*newImagePathURI*//*createNewUri(updatedFileName(imagePathURI))*/imagePathURI, mSaveCallback); //FIXME java.lang.UnsupportedOperationException: No external updates -- maybe try checking what onClickListner does for capturing image... the tempiamge is a cache image which looks like it doesn't allow things to get editing.
                        Log.d(TAG, "SUCCESS image cropped");

                    }
                    @Override public void onError(Throwable e){
                        Log.d(TAG, "error saving cropped image, error message: " + e);
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
        @Override
        public void onSuccess(Uri outputUri){
            Log.d(TAG, "in onSuccess");
            Toast.makeText(v.getContext(), "success", Toast.LENGTH_SHORT).show();

            Boolean deleted = v.getContext().deleteFile(imagePathURI.getPath());
            Log.d(TAG, "did the old file get deleted? "+ deleted);

            Log.d(TAG, "is the new file still there? Check the following list");
            String[] files = v.getContext().fileList();
            for(String file: files){
                Log.d(TAG, "--> "+file);
            }

            getFragmentManager().popBackStack();
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Well shucks granma... the callback failed because " + e);
            Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            /*
            08-28 13:08:18.546 20506-20506/kama.patterndb W/System.err:     java.lang.UnsupportedOperationException: No external updates
                                                                            at android.support.v4.content.FileProvider.update(FileProvider.java:503)
                                                                            at android.content.ContentProvider$Transport.update(ContentProvider.java:359)
                                                                            at android.content.ContentResolver.update(ContentResolver.java:1672)
            08-28 13:08:18.551 20506-20506/kama.patterndb W/System.err:     at com.isseiaoki.simplecropview.util.Utils.updateGalleryInfo(Utils.java:532)
                                                                            at com.isseiaoki.simplecropview.CropImageView.saveImage(CropImageView.java:1317)
                                                                            at com.isseiaoki.simplecropview.CropImageView.access$2100(CropImageView.java:58)
                                                                            at com.isseiaoki.simplecropview.CropImageView$14.run(CropImageView.java:1833)
            08-28 13:08:18.552 20506-20506/kama.patterndb W/System.err:     at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:457)
                                                                            at java.util.concurrent.FutureTask.run(FutureTask.java:266)
            08-28 13:08:18.553 20506-20506/kama.patterndb W/System.err:     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1162)
            08-28 13:08:18.564 20506-20506/kama.patterndb W/System.err:     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:636)
            08-28 13:08:18.569 20506-20506/kama.patterndb W/System.err:     at java.lang.Thread.run(Thread.java:764)
            */
        }
    };

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess(){
            Log.d(TAG, "Success on mLoadCallback");
        }
        @Override public void onError(Throwable e) {
            Log.d(TAG, "Error on mLoadCallback: "+e);

        }
    };


    private String updatedFileName(Uri u){
        String updated = null;
        String original = u.toString();

        //peel off directory and ###.jpg
        int firstIndex = original.lastIndexOf('/') + 1;
        int lastIndex = original.lastIndexOf('_');
        updated = original.substring(firstIndex, lastIndex)+".jpg";
        Log.d(TAG, "original = " + original);
        Log.d(TAG, "updated = " + updated);

        return updated;
    }

    private File createImageFile(String newFileName) throws IOException {
        File storageDir = v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, newFileName + ".jpg");

        Log.d(TAG, "file just created is "+ image.toString());
        Log.d(TAG, "and it exists? " + image.exists());

        return image;
    }


    public Uri createNewUri(String filename){
        Uri uri = null;

        //get dirPath (String)
        File dirPath = null;
        if(v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).canWrite()){
            dirPath = v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Log.d(TAG, "has permission to write, whoot!");
        }else{
            Log.d(TAG, "BOOM! can't see permission to write");
        }
        //get filename (String)
        //String filename = "politeTest.jpg";
        String path = dirPath.toString() + "/" + filename;
        File file = new File(dirPath, filename);
        Log.d(TAG, "file " + file.toString() + " exists " + file.exists());
        ContentValues values = new ContentValues();  //purpose???
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATA, path);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
            Log.d(TAG, "file exists: " + file.toString());
        }else{
            Log.d(TAG, "FILE DOES NOT EXIST, sad panda: " + file.toString());
        }

        //ContentResolver resolver = v.getContext().getContentResolver();
        //Log.d(TAG, "resolver "+ resolver);
        //uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); //FIXME this bombs the program
        //Log.d(TAG, "saveUri given by ContentResolver is " + uri);
        uri = FileProvider.getUriForFile(v.getContext(), "kama.patterndb.provider", file); //FIXME this doesn't allow the file to be written to.
        Log.d(TAG, "saveUri given by FileProvider is " + uri);

        return uri;
    }

}
