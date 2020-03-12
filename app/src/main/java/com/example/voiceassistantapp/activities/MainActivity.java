package com.example.voiceassistantapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.utils.PermissionsUtility;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {
    private TextToSpeech textToSpeech;
    private Handler handler;
    ImageView battery;
    private static final int VOICE_RECOGNIZE_RESULT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        battery=findViewById(R.id.battery);
        battery.setOnClickListener(this);
        initializeTextToSpeech();

        PermissionsUtility.checkRequiredPermission(this);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Wellcome();
            }
        },2000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Wellcome();
    }

    private void Wellcome() {
        speak("Wellcome to voice assistant");
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this,  this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        speak("Wellcome to voice assistant");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionsUtility.RECORD_AUDIO_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                    speak("Permission granted");
                } else {
                    speak("Permission denied!");
                    finishActivityAfterDelay();
                }
                break;
            case PermissionsUtility.READ_CONTACT_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speak("Permission Granted");
                } else {
                    speak("Permission denied!");
                    finishActivityAfterDelay();
                }
                break;

            case PermissionsUtility.WRITE_CONTACT_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speak("Permission Granted");
                } else {
                    speak("Permission denied!");
                }
                break;
        }

    }

    private void speak(String word) {
        textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null);

    }

    private void finishActivityAfterDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.shutdown();
                textToSpeech = null;
                finish();
            }
        }, 3500);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("en"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
        textToSpeech = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.battery:
                //startActivity(new Intent(MainActivity.this,batteryLevelActivity.class));
                getBattery_percentage();
                break;
        }
    }

    void getBattery_percentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        float p = batteryPct * 100;
        //tv_battery.setText(String.valueOf(Math.round(p)));
        Log.d("Battery percentage", String.valueOf(Math.round(p)));
        speak(String.valueOf(Math.round(p)));

    }
}
