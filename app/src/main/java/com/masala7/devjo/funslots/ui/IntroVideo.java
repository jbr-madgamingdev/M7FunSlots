package com.masala7.devjo.funslots.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.masala7.devjo.funslots.R;

public class IntroVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_video);

        // Temporary Redirect
        Intent openApp = new Intent(IntroVideo.this, SlotGame.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(openApp);
    }
}