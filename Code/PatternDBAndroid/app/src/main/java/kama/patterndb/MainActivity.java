package kama.patterndb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    MenuFragment menuFragment;

    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);

        if(savedInstanceState == null) {

            menuFragment = menuFragment.newInstance();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, menuFragment);
            ft.commit();
        }else{
            Log.d("myApp", "savedInstanceState not null");
        }


    }
}
