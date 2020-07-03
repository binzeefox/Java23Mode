package com.binzeefox.java23mode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.binzeefox.foxframe.core.base.FoxActivity;

public class MainActivity extends FoxActivity {

    @Override
    protected int onSetLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void create(Bundle savedInstanceState) {

    }
}