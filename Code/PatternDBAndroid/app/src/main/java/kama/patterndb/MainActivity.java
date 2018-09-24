package kama.patterndb;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "myApp";

    MenuFragment menuFragment;

    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);

        /*
        //create and check status of app specific folder for testing purposes
        String filename = "ALLCAPSTOBESEEN.jpg";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(TAG, "external media mounted");
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File fileForReasons = new File(storageDir, filename);
            try{
                FileOutputStream written = new FileOutputStream(fileForReasons);
                written.write(1);
                written.close();
            }catch (Exception e){
                Log.d(TAG, "exception: "+e);
            }
            Log.d(TAG, "storageDir can execute "+ storageDir.canExecute());
            Log.d(TAG, "storageDir can read "+ storageDir.canRead());
            Log.d(TAG, "storageDir can write "+ storageDir.canWrite());
            try {
                Log.d(TAG, "storageDir can createNewFile "+ storageDir.createNewFile());
            } catch (IOException e) {
                Log.d(TAG, "tried to create new file and then this happened: "+ e);
            }
            Log.d(TAG, "storageDir can exists "+ storageDir.exists());
            Log.d(TAG, "storageDir can has free space "+ storageDir.getFreeSpace());
            if(fileForReasons.exists()){
                Log.d(TAG,"WHEN CREATING A FILE WITH new File(strdir, filename), it works");
            }else{
                Log.d(TAG, "!!!!!!!!!!!!I may cry now!!!!!!!!!!!");
            }
            File[] filesHere = storageDir.listFiles();
            Log.d(TAG, "files in "+ storageDir);
            for(File file: filesHere){
                Log.d(TAG, "--- "+file.toString());
            }
        }else{
            Log.d(TAG, "external media unavailable");
        }
*/

        if(savedInstanceState == null) {

            menuFragment = menuFragment.newInstance();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, menuFragment);
            ft.commit();
        }else{
            Log.d("myApp", "savedInstanceState not null");
        }


    }
}
