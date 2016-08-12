package com.pavan.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * Created by Roston on 12/07/14.
 */
public class popup extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void musicPlayer(View view){



        Toast.makeText(getApplicationContext(), "Inside the music player",Toast.LENGTH_LONG).show();



    }

}

