package com.example.voiceassistantapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.utils.KEY;
import com.example.voiceassistantapp.utils.MyDictionary;
import com.example.voiceassistantapp.utils.MyDictionaryDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DialerActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    EditText edtPhoneNo;
    private TextToSpeech textToSpeech;
    private Boolean mSpeechText = false;
    private Boolean mBeep;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Boolean mCheckCountDownTime = false;
    private CountDownTimer mCountDownTimer;
    private Boolean mCheckText;
    private Boolean mFirstCommand=true;
    private List<MyDictionary> myDictionaries = new ArrayList<>();
    private MyDictionaryDatabase myDictionaryDatabase;
    private String mText;
    private String mLanguageSelected,mLanguageTranslate;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesMainHome;
    private Handler handler;
    int androidAPILevel = android.os.Build.VERSION.SDK_INT;
    //TextView lblinfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialer_activity);
        edtPhoneNo = findViewById(R.id.edtPhoneNumber);
        myDictionaryDatabase = new MyDictionaryDatabase(this);
        sharedPreferences = getSharedPreferences(KEY.Shared_Preferences_Settings, Context.MODE_PRIVATE);
        sharedPreferencesMainHome = getSharedPreferences(KEY.Shared_Preferences_Main, Context.MODE_PRIVATE);
        //lblinfo =  findViewById(R.id.lblinfo);
        initializeTextToSpeech();
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                speak("Welcome to Dialer");
            }
        },2000);

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

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this,  this);
    }
    public void buttonClickEvent(View v) {
        String phoneNo = edtPhoneNo.getText().toString();
        try {

            switch (v.getId()) {
                case R.id.btnAterisk:
                   // lblinfo.setText("");
                    phoneNo += "*";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("*", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnHash:
                   // lblinfo.setText("");
                    phoneNo += "#";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("#", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnZero:
                    //lblinfo.setText("");
                    phoneNo += "0";
                    textToSpeech.speak("0", TextToSpeech.QUEUE_FLUSH, null);
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnOne:
                   // lblinfo.setText("");
                    phoneNo += "1";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("1", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnTwo:
                   // lblinfo.setText("");
                    phoneNo += "2";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("2", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnThree:
                   // lblinfo.setText("");
                    phoneNo += "3";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("3", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnFour:
                   // lblinfo.setText("");
                    phoneNo += "4";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("4", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnFive:
                  //  lblinfo.setText("");
                    phoneNo += "5";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("5", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnSix:
                  //  lblinfo.setText("");
                    phoneNo += "6";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("6", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnSeven:
                 //   lblinfo.setText("");
                    phoneNo += "7";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("7", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnEight:
                //    lblinfo.setText("");
                    phoneNo += "8";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("8", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnNine:
                  //  lblinfo.setText("");
                    phoneNo += "9";
                    edtPhoneNo.setText(phoneNo);
                    textToSpeech.speak("9", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btndel:
                  //  lblinfo.setText("");
                    if (phoneNo != null && phoneNo.length() > 0) {
                        phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                        textToSpeech.speak("1 number clear", TextToSpeech.QUEUE_FLUSH, null);
                    }

                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnClearAll:
                   // lblinfo.setText("");
                    edtPhoneNo.setText("");
                    textToSpeech.speak("All clear", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case R.id.btnCall:
                    if (phoneNo.trim().equals("")) {
                       // lblinfo.setText("Please enter a number to call on!");
                        Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show();
                        textToSpeech.speak("Please enter number", TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+Uri.encode(phoneNo.trim())));
                        startActivity(callIntent);
                       /* Boolean isHash = false;
                        if (phoneNo.subSequence(phoneNo.length() - 1, phoneNo.length()).equals("#")) {
                            phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                            String callInfo = "tel:" + phoneNo + Uri.encode("#");
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(callInfo));
                            startActivity(callIntent);
                        } else {
                            String callInfo = "tel:" + phoneNo;
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(callInfo));
                            startActivity(callIntent);
                        }*/
                    }
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
                        if (mFirstCommand){
                            edtPhoneNo.setText("");
                        }
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

        } catch (Exception ex) {

        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
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
    protected void onResume() {
        super.onResume();
        setLanguageSelected();
        setLanguageTranslate();
        setBeep();
        setSpeechText();
    }
    public void setLanguageSelected() {
        mLanguageSelected = sharedPreferencesMainHome.getString(KEY.LANGUAGETOSPEECH, "en-US");
        //mTextViewDisplayCurrentLanguage.setText(mLanguageSelected);
    }
    public void setLanguageTranslate() {
        mLanguageTranslate = sharedPreferencesMainHome.getString(KEY.LANGUAGETOTRANSLATE, "en");
        //mTextDisplayTranslate.setText(mLanguageTranslate);
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
               /* for (int i = 0; i < myDictionaries.size(); i++) {
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

                }*/

                if (mCheckText == false) {
                    if (mFirstCommand){
                    edtPhoneNo.setText(edtPhoneNo.getText().toString() + mText.toLowerCase());
                    // mTextDelete = mText.toLowerCase() + " ";
                    edtPhoneNo.setSelection(edtPhoneNo.length());
                    mFirstCommand=false;

                    Log.d("#121", "onResults: this loop2");
                }else {
                        if (TextUtils.equals(mText,"call")){
                            Log.d("#121", "onResults: this loop2");
                            mFirstCommand=true;
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+Uri.encode(edtPhoneNo.getText().toString().trim())));
                            startActivity(callIntent);
                        }else if (TextUtils.equals(mText,"clear")){
                            mFirstCommand=true;
                            edtPhoneNo.setText("");
                        }else {
                            mFirstCommand=true;
                        }
                    }
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
                Toast.makeText(DialerActivity.this, getResources().getString(R.string.stop), Toast.LENGTH_SHORT).show();
            }

        }.start();
    }
}
