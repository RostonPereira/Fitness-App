package com.pavan.slidingmenu.slidelist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.pavan.slidingmenu.*;

import java.util.ArrayList;

/**
 * Created by Roston on 29/07/14.
 */

@SuppressLint("NewApi")
public class DB extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.me, container, false);

        onLoad(rootView);

        return rootView;
    }

    public void onLoad(View view){

        System.out.println("========================================From the DB-fragment for SQLite.=============================");
        MainActivity activity=(MainActivity)getActivity();
        activity.DataBase(view);


    }
}
