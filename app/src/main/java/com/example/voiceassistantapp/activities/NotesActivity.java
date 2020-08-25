package com.example.voiceassistantapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.models.MyTextSaved;
import com.example.voiceassistantapp.utils.KEY;
import com.example.voiceassistantapp.utils.MyDictionary;
import com.example.voiceassistantapp.utils.MyDictionaryDatabase;
import com.example.voiceassistantapp.utils.MyTextsavedDatabase;
import com.example.voiceassistantapp.utils.OnSwipeTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NotesActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    TextView txt_file_name;
    EditText et_main_note;
    ImageView mic_note;
    LinearLayout linear_bottom;

    private Boolean mSpeechText = false;
    private Boolean mBeep;
    private Intent mSpeechRecognizerIntent;
    private SpeechRecognizer mSpeechRecognizer;
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
    private TextToSpeech textToSpeech;
    private Handler handler;
    private MyTextsavedDatabase myTextsavedDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity);
        initializeTextToSpeech();
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Wellcome();
            }
        },2000);
        initialization();
    }
    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this,  this);
    }


    private void Wellcome() {
        speak("Wellcome to voice Page");
    }
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    private void initialization() {
        myTextsavedDatabase = new MyTextsavedDatabase(this);
        txt_file_name=findViewById(R.id.txt_FiletxtHomeMain);
        txt_file_name.setText(getSaltString());
        et_main_note=findViewById(R.id.edt_HomeMain);
        mic_note=findViewById(R.id.imv_MicHomeMain);
        linear_bottom=findViewById(R.id.linear_BottomHomeMain);
        linear_bottom.setOnTouchListener(new OnSwipeTouchListener(NotesActivity.this){

            public void onSwipeRight() {
                Log.e("Swipe :", "RIGHT");

                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                myTextsavedDatabase.addAlbum(new MyTextSaved(et_main_note.getText().toString()
                        , txt_file_name.getText().toString(), currentDate));
                Toast.makeText(NotesActivity.this, getResources().getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
                speak("Note Saved");
                //Toast.makeText(NotesActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Log.e("Swipe :", "LEFT");
                et_main_note.setText("");
                speak("All text Clear");
                Toast.makeText(NotesActivity.this, "CLear", Toast.LENGTH_SHORT).show();
            }



        });
        myDictionaryDatabase = new MyDictionaryDatabase(this);
        sharedPreferences = getSharedPreferences(KEY.Shared_Preferences_Settings, Context.MODE_PRIVATE);
        sharedPreferencesMainHome = getSharedPreferences(KEY.Shared_Preferences_Main, Context.MODE_PRIVATE);
        mic_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Toast.makeText(NotesActivity.this, getResources().getString(R.string.start), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(NotesActivity.this, getResources().getString(R.string.stop), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        speak("Wellcome to Notes Page");
        setLanguageSelected();
        setLanguageTranslate();
        setBeep();
        setSpeechText();
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
    public void setLanguageSelected() {
        mLanguageSelected = sharedPreferencesMainHome.getString(KEY.LANGUAGETOSPEECH, "en-US");
        //mTextViewDisplayCurrentLanguage.setText(mLanguageSelected);
    }
    public void setLanguageTranslate() {
        mLanguageTranslate = sharedPreferencesMainHome.getString(KEY.LANGUAGETOTRANSLATE, "en");
        //mTextDisplayTranslate.setText(mLanguageTranslate);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("en"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            }
        }
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
                        et_main_note.setText(et_main_note.getText().toString() + myDictionaries.get(i).getAppUnderstand().toString() + " ");
                        mCheckText = true;
                        //mTextDelete = myDictionaries.get(i).getAppUnderstand() + " ";
                        et_main_note.setSelection(et_main_note.length());

                    }
                }
                if (mCheckText == false) {
                    et_main_note.setText(et_main_note.getText().toString() + mText.toLowerCase() + " ");
                    //mTextDelete = mText.toLowerCase() + " ";
                    et_main_note.setSelection(et_main_note.length());
                }
                //mCheckBackText = false;
                mSpeechRecognizer.cancel();
               // mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

               // mCheckDeleteText = true;

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
                Toast.makeText(NotesActivity.this, getResources().getString(R.string.stop), Toast.LENGTH_SHORT).show();
            }

        }.start();
    }
}
