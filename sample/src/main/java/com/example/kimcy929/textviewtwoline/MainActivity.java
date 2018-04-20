package com.example.kimcy929.textviewtwoline;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kimcy929.localeutils.LocaleUtils;
import com.kimcy929.textviewtwoline.TextViewTwoLine;

public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        //See MyApp class to config the language
        LocaleUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_line);

        showBackArrow();

        final TextViewTwoLine textViewTwoLine = findViewById(R.id.textViewTwoLine);

        Button btnChangeText = findViewById(R.id.btnChangeText);

        btnChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTwoLine.setTextTitle(getString(R.string.shutter_sound));
                textViewTwoLine.setTextDescription(getString(R.string.shutter_sound_description));

                textViewTwoLine.setLeftDrawableCompat(R.drawable.ic_vibration_black_24dp);
//
//               textViewTwoLine.setText(null, "");
            }
        });

        RelativeLayout btnHideVideo = findViewById(R.id.btnHideVideo);
        RelativeLayout btnTranslate = findViewById(R.id.btnTranslate);
        //RelativeLayout btnShutterSound = findViewById(R.id.btnShutterSound);

        btnHideVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /*btnShutterSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    private void showBackArrow() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
    }
}
