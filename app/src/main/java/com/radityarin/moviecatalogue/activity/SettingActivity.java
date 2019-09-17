package com.radityarin.moviecatalogue.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.radityarin.moviecatalogue.AlarmReceiver;
import com.radityarin.moviecatalogue.R;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Switch sw_dailyreminder, sw_release;
    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sw_dailyreminder = findViewById(R.id.sw_dailyreminder);
        sw_release = findViewById(R.id.sw_release);

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        sw_dailyreminder.setChecked(sharedPreferences.getBoolean("value", false));
        sw_release.setChecked(sharedPreferences.getBoolean("value2", false));

        sw_dailyreminder.setOnClickListener(this);
        sw_release.setOnClickListener(this);

        alarmReceiver = new AlarmReceiver();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_dailyreminder:
                boolean check = sw_dailyreminder.isChecked();
                if (check) {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    sw_dailyreminder.setChecked(true);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    sw_dailyreminder.setChecked(false);
                }
                alarmReceiver.setRepeatingAlarmDaily(this,AlarmReceiver.TYPE_DAILYREMINDER,check);
                break;
            case R.id.sw_release:
                boolean check2 = sw_release.isChecked();
                if (check2) {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value2", true);
                    editor.apply();
                    sw_release.setChecked(true);
                    alarmReceiver.setRepeatingAlarmRelease(this, AlarmReceiver.TYPE_RELEASEREMINDER);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value2", false);
                    editor.apply();
                    sw_release.setChecked(false);
                    alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_RELEASEREMINDER);
                }
                break;
        }
    }

}
