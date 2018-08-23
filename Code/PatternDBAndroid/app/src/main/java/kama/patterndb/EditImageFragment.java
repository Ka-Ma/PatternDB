package kama.patterndb;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

public class EditImageFragment extends Fragment {
    //members
    CropImageView mImageView;
    Button mRotateButton;
    Button mDoneButton;
    Button mBackButton;

    DBHelper mydb;
    View v;
    private static final String TAG = "myApp";

    public static EditImageFragment newInstance(String ip){
        EditImageFragment f = new EditImageFragment();

        //any args in Bundle
        Bundle args = new Bundle();
        args.putString("imagePath", ip);
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
        String ip = getArguments().getString("imagePath");
        Log.d(TAG, "imagePath = " + ip);
        Bitmap imageBitmap = BitmapFactory.decodeFile(ip);
        Log.d(TAG, "imageBitmap is " + imageBitmap.toString() + " stats: " + imageBitmap.getWidth() + " and "+ imageBitmap.getHeight() + " other stuff " + imageBitmap.getAllocationByteCount());


        mImageView = (CropImageView) v.findViewById(R.id.cropImageView);
        mImageView.setImageBitmap(imageBitmap);


        /*
        mBackButton = (Button) v.findViewById(R.id.button_back);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        */
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
