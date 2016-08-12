package com.pavan.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * Created by Roston on 14/07/14.
 */
public class cover extends Activity implements View.OnClickListener {

    String tabkeeper="2";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
    }


    @Override
    public void onClick(View v) {

        finish();
        moveTaskToBack(true);

    }

    public void onNew(View view){

        setContentView(R.layout.fb_fragment);
        Intent intent=new Intent(cover.this,MainActivity.class);
        startActivity(intent);
        cover.this.finish();

    }

    public void onsetting(View view){

        //setContentView(R.layout.settings);
        Intent intent=new Intent(cover.this,Settings.class);
        intent.putExtra("tabkeeper",tabkeeper);
        startActivity(intent);
        cover.this.finish();

    }


}
