package com.example.voiceassistantapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.utils.KEY;
import com.example.voiceassistantapp.utils.MyDictionary;
import com.example.voiceassistantapp.utils.MyDictionaryDatabase;
import com.example.voiceassistantapp.utils.PermissionsUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {
    private TextToSpeech textToSpeech;
    private EditText mEdtHomeMain;
    private Handler handler;
    RelativeLayout battery;
    ImageView mic_button;
    private static final int VOICE_RECOGNIZE_RESULT = 100;
    private Boolean mSpeechText = false;
    private Boolean mBeep;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Boolean mCheckCountDownTime = false;
    private CountDownTimer mCountDownTimer;
    private Boolean mCheckText;
    private String mMytext;
    private List<MyDictionary> myDictionaries = new ArrayList<>();
    private MyDictionaryDatabase myDictionaryDatabase;
    private String mText;
    private String mLanguageSelected,mLanguageTranslate;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesMainHome;
    int androidAPILevel = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mEdtHomeMain = findViewById(R.id.edt_HomeMain);
        myDictionaryDatabase = new MyDictionaryDatabase(this);
        sharedPreferences = getSharedPreferences(KEY.Shared_Preferences_Settings, Context.MODE_PRIVATE);
        sharedPreferencesMainHome = getSharedPreferences(KEY.Shared_Preferences_Main, Context.MODE_PRIVATE);
        battery=findViewById(R.id.battery);
        mic_button=findViewById(R.id.imv_MicHomeMain);
        mic_button.setOnClickListener(this);
        battery.setOnClickListener(this);
        initializeTextToSpeech();

        PermissionsUtility.checkRequiredPermission(this);
       // PermissionsUtility.checkReadContactAndCallPerrmission(this);

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
        speak("Wellcome to voice assistant. tap on bottom mic. and speak command. for music. Play music. for dialer. speak open dialer. for battery. speak battery level. for notes. speak my notes or note.");
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this,  this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeTextToSpeech();
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Wellcome();
            }
        },2000);
       // speak("Wellcome to voice assistant. tap on bottom mic and speak command. for music. Play music. for dialer. speak open dialer. for battery. speak battery level. for notes. speak my notes or note.");
        setLanguageSelected();
        setLanguageTranslate();
        setBeep();
        setSpeechText();
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
       // textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        if (androidAPILevel < 21) {
            HashMap<String,String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1"); // change the 0.5 to any value from 0-1 (1 is default)
            textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, params);
        } else { // android API level is 21 or higher...
            Bundle params = new Bundle();
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1f); // change the 0.5f to any value from 0f-1f (1f is default)
            textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, params, null);
        }

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
    protected void onPause() {
        super.onPause();
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
            case R.id.imv_MicHomeMain:
                if (mSpeechText == false) {
                    if (mBeep == false) {
                        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_VIBRATE);
                    } else {
                        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                                audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                    }
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    mSpeechText = true;
                    mCheckCountDownTime = false;
                    mEdtHomeMain.setText("");
                    Toast.makeText(this, getResources().getString(R.string.start), Toast.LENGTH_SHORT).show();
                } else {

                    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (mBeep == false) {
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                                audio.getStreamMaxVolume(AudioManager.ADJUST_SAME), AudioManager.RINGER_MODE_SILENT);
                    } else {
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                                audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                    }
                    if (mCheckCountDownTime == true) {
                        mCountDownTimer.cancel();
                    }

                    mSpeechRecognizer.cancel();
                    mSpeechText = false;
                    Toast.makeText(this, getResources().getString(R.string.stop), Toast.LENGTH_SHORT).show();

                }
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
        speak("Battery percentage is "+String.valueOf(Math.round(p)));

    }
    public void setBeep() {
        mBeep = false;
    }

    public void setSpeechText() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguageSelected);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        myDictionaries.clear();
        myDictionaries = myDictionaryDatabase.getAllMyDictionary();
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("#121", "onBeginningOfSpeech: ");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("#121", "onBufferReceived: ");
            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                mSpeechRecognizer.cancel();

                Log.d("#121", String.valueOf(error));
                if ((error == 7 || error == 6) && mCheckCountDownTime == false) {
                    CountDownTimeSpeechToText();
                    mCheckCountDownTime = true;
                }

                Log.d("#121", String.valueOf(mSpeechText));
                if (mSpeechText == true) {
                    Log.d("#121", "why");
                    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_VIBRATE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        }
                    },500);

                }

            }

            @Override
            public void onResults(Bundle results) {
                Log.d("#121", "onResults: ");
                if (mCheckCountDownTime == true) {
                    mCheckCountDownTime = false;
                    mCountDownTimer.cancel();
                }
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                mCheckText = false;
                mText = matches.get(0);
                Log.d("#121", mText);
                for (int i = 0; i < myDictionaries.size(); i++) {
                    if (myDictionaries.get(i).getYouSay().toLowerCase().equals(mText.toLowerCase()) == true) {
                        mEdtHomeMain.setText(mEdtHomeMain.getText().toString() + myDictionaries.get(i).getAppUnderstand().toString());
                        mCheckText = true;
                       // mTextDelete = myDictionaries.get(i).getAppUnderstand() + " ";
                        mEdtHomeMain.setSelection(mEdtHomeMain.length());
                        if (TextUtils.equals(mEdtHomeMain.getText().toString(),"battery level")){
                            Log.d("#121", "onResults: this loop");
                            getBattery_percentage();
                        }
                        Log.d("#121", "onResults: this loop");
                    }

                }
                if (mCheckText == false) {
                    mEdtHomeMain.setText(mEdtHomeMain.getText().toString() + mText.toLowerCase());
                   // mTextDelete = mText.toLowerCase() + " ";
                    mEdtHomeMain.setSelection(mEdtHomeMain.length());
                    if (TextUtils.equals(mEdtHomeMain.getText().toString(),"battery level")){
                        Log.d("#121", "onResults: this loop2");
                        getBattery_percentage();
                    }else if (TextUtils.equals(mEdtHomeMain.getText().toString(),"open dialer") || TextUtils.equals(mEdtHomeMain.getText().toString(),"open dialler"))
                    {
                        startActivity(new Intent(MainActivity.this,DialerActivity.class));
                    }else if (TextUtils.equals(mEdtHomeMain.getText().toString(),"play music"))
                    {
                        startActivity(new Intent(MainActivity.this,MusicPlayer.class));
                    }else if (TextUtils.equals(mEdtHomeMain.getText().toString(),"note"))
                    {
                        startActivity(new Intent(MainActivity.this,NotesActivity.class));
                    }
                    else if (TextUtils.equals(mEdtHomeMain.getText().toString(),"my notes"))
                    {
                        startActivity(new Intent(MainActivity.this,TextSavedActivity.class));
                    }
                    Log.d("#121", "onResults: this loop2");
                }
               // mCheckBackText = false;
                mSpeechRecognizer.cancel();
                //mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                //mCheckDeleteText = true;

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }



    public void CountDownTimeSpeechToText() {
        Float mTimeLongestBreak = sharedPreferences.getFloat(KEY.LONGESTBREAK, 1);
        mTimeLongestBreak = mTimeLongestBreak * 60 * 1000;
        int mTImeLongestBreakInt = (int) Math.round(mTimeLongestBreak);
        mCountDownTimer = new CountDownTimer(mTImeLongestBreakInt, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("#121", "Time : " + String.valueOf((int) (millisUntilFinished / 1000) % 60));

            }

            @Override
            public void onFinish() {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (mBeep == false) {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                            audio.getStreamMaxVolume(AudioManager.ADJUST_SAME), AudioManager.RINGER_MODE_SILENT);
                } else {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                            audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                }

                mSpeechRecognizer.cancel();
                mSpeechText = false;
                Toast.makeText(MainActivity.this, getResources().getString(R.string.stop), Toast.LENGTH_SHORT).show();
            }

        }.start();
    }
    public void setLanguageSelected() {
        mLanguageSelected = sharedPreferencesMainHome.getString(KEY.LANGUAGETOSPEECH, "en-US");
        //mTextViewDisplayCurrentLanguage.setText(mLanguageSelected);
    }
    public void setLanguageTranslate() {
        mLanguageTranslate = sharedPreferencesMainHome.getString(KEY.LANGUAGETOTRANSLATE, "en");
        //mTextDisplayTranslate.setText(mLanguageTranslate);
    }
}
