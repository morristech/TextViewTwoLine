package com.example.kimcy929.textviewtwoline;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class FakeSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fake_settings);

        showBackArrow();

        LinearLayout systemSettingsLayout = findViewById(R.id.systemSettingsLayout);

        for (int i = 0; i < systemSettingsLayout.getChildCount(); i++) {
            View view = systemSettingsLayout.getChildAt(i);
            view.setOnClickListener(onClickListener);
        }
    }

    private View.OnClickListener onClickListener = v -> {

    };

    @SuppressWarnings("ConstantConditions")
    private void showBackArrow() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
    }
}
