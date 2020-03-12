package com.example.voiceassistantapp.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voiceassistantapp.R;

import java.util.Locale;

public class batteryLevelActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView tv_battery;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_level_activity);
        tv_battery = findViewById(R.id.battery_tv);
        initializeTextToSpeech();
        getBattery_percentage();

    }

    void getBattery_percentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        float p = batteryPct * 100;
        tv_battery.setText(String.valueOf(Math.round(p)));
        Log.d("Battery percentage", String.valueOf(Math.round(p)));
        speak(String.valueOf(Math.round(p)));

    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this,  this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("en"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            }
        }
    }

    private void speak(String word) {
        textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null);

    }
}
