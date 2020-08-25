package com.example.voiceassistantapp.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.adapters.TextSavedAdapter;
import com.example.voiceassistantapp.models.MyTextSaved;
import com.example.voiceassistantapp.utils.Align;
import com.example.voiceassistantapp.utils.Config;
import com.example.voiceassistantapp.utils.MyTextsavedDatabase;
import com.example.voiceassistantapp.utils.StackLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TextSavedActivity extends AppCompatActivity implements TextSavedAdapter.OnLongClickListener,TextToSpeech.OnInitListener{
    private RecyclerView rcvTextSaved;
    private MyTextsavedDatabase myTextsavedDatabase;
    private TextToSpeech textToSpeech;
    int androidAPILevel = android.os.Build.VERSION.SDK_INT;
    private List<MyTextSaved> myTextSaveds = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_saved_notes);
        initializeTextToSpeech();
        initailization();
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this,  this);
    }
    private void initailization() {

        rcvTextSaved = findViewById(R.id.rcv_TextSaved);
        Config config = new Config();
        config.secondaryScale = 0.8f;
        config.scaleRatio = 0.9f;
        config.maxStackCount = 4;
        config.initialStackCount = 22;
        config.space = getResources().getDimensionPixelOffset(R.dimen.item_space);

        config.align = Align.RIGHT;
        myTextsavedDatabase = new MyTextsavedDatabase(this);
        myTextSaveds = myTextsavedDatabase.getAllMyTextSaved();
        rcvTextSaved.setLayoutManager(new StackLayoutManager(config));
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        //rcvTextSaved.setLayoutManager(layoutManager);

        TextSavedAdapter textSavedAdapter = new TextSavedAdapter(this, myTextSaveds);
        rcvTextSaved.setAdapter(textSavedAdapter);
        textSavedAdapter.setOnLongClickListener(this);
    }

    @Override
    public void onlongClicked(String myText) {
        speak(myText);
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

    @Override
    public void onDeleteTextSavedClicked(String nameTextDelete) {
        myTextsavedDatabase.deleteTextsaved(nameTextDelete);
        myTextSaveds = myTextsavedDatabase.getAllMyTextSaved();

        TextSavedAdapter textSavedAdapter = new TextSavedAdapter(this, myTextSaveds);
        rcvTextSaved.setAdapter(textSavedAdapter);
        textSavedAdapter.setOnLongClickListener(this);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("en"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            }
        }
    }
}
