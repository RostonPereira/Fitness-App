
package com.pavan.slidingmenu;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class Settings extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    public void onBackPressed(){

        setContentView(R.layout.cover);
//        Intent intent=new Intent(this,cover.class);
//        startActivity(intent);
//        this.finish();
    }
}
