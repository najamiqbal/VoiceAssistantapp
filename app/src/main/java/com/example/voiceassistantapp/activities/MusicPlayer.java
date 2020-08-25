package com.example.voiceassistantapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.adapters.AudioListAdapter;
import com.example.voiceassistantapp.models.AudioModel;
import com.example.voiceassistantapp.utils.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends AppCompatActivity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    private RecyclerView recyclerView;
    private RelativeLayout swipeLayout;
    private TextView audioFileNameTV;

    private AudioListAdapter audioModelListAdapter;

    private List<AudioModel> audioModelList;

    private LinearLayoutManager verticalLayoutManager;

    private MediaPlayer mp;

    private int selectedIndex = -1;
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        recyclerView = findViewById(R.id.recycler_view);
        swipeLayout = findViewById(R.id.swipe_layout);
        audioFileNameTV = findViewById(R.id.audio_file_name_tv);

        mp = new MediaPlayer();

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        checkPermissions();

        swipeLayout.setOnTouchListener(new OnSwipeTouchListener(MusicPlayer.this) {
            public void onSwipeTop() {
                Log.e("Swipe :", "TOP");
                if (mp != null && isPause) {
                    mp.start();
                    isPause = false;
                }
            }

            public void onSwipeRight() {
                Log.e("Swipe :", "RIGHT");
                if (audioModelList != null && audioModelList.size() > 0 && selectedIndex > 0) {
                    selectedIndex--;
                    playAudio(audioModelList.get(selectedIndex).getPath());
                    audioFileNameTV.setText("Playing:\n" + audioModelList.get(selectedIndex).getName());
                    recyclerView.smoothScrollToPosition(selectedIndex);

                    for (int i = 0; i < audioModelList.size(); i++) {
                        audioModelList.get(i).setPlaying(false);
                    }
                    audioModelList.get(selectedIndex).setPlaying(true);
                    audioModelListAdapter.setData(audioModelList);
                    audioModelListAdapter.notifyDataSetChanged();
                }
            }

            public void onSwipeLeft() {
                Log.e("Swipe :", "LEFT");
                System.out.println("==============>>>LEFTF");
                if (audioModelList != null && audioModelList.size() > 0 && selectedIndex < audioModelList.size() - 1) {
                    selectedIndex++;
                    System.out.println("==============>>>LEFTF"+audioModelList.get(selectedIndex).getPath());
                    playAudio(audioModelList.get(selectedIndex).getPath());
                    audioFileNameTV.setText("Playing:\n" + audioModelList.get(selectedIndex).getName());
                    recyclerView.smoothScrollToPosition(selectedIndex);
                    for (int i = 0; i < audioModelList.size(); i++) {
                        audioModelList.get(i).setPlaying(false);
                    }
                    audioModelList.get(selectedIndex).setPlaying(true);
                    audioModelListAdapter.setData(audioModelList);
                    audioModelListAdapter.notifyDataSetChanged();
                }
                System.out.println("==============>>>LEFTF here...");
            }

            public void onSwipeBottom() {
                Log.e("Swipe :", "DOWN");
                if (mp != null && mp.isPlaying()) {
                    mp.pause();
                    isPause = true;
                }
            }
        });
    }

    private void listAudioFilesToView() {
        audioModelList = getAllAudioFilesFromDevice();
        verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        audioModelListAdapter = new AudioListAdapter(MusicPlayer.this, audioModelList);
        recyclerView.setAdapter(audioModelListAdapter);
    }

    private List<AudioModel> getAllAudioFilesFromDevice() {

        final List<AudioModel> tempAudioList = new ArrayList<>();

        Log.e("Name :" , " Album============ :");

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[0];

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            projection = new String[]{MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        }

        Cursor c = getContentResolver().query(uri,
                projection,
                null,           // To read the files of a specific folder then use ==>     MediaStore.Audio.Media.DATA + " like ? ",
                null,       // To read the files of a specific folder then use ==>     new String[]{"%yourFolderName%"},
                null);

        if (c != null) {
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                String path = c.getString(1);
                String album = c.getString(0);
                String name = c.getString(2);

                String artist = album.substring(album.lastIndexOf("/") + 1);

//                audioModel.setName(name);
//                audioModel.setAlbum(album);
//                audioModel.setArtist(artist);
//                audioModel.setPath(path);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);


                String song_name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                int song_id = c.getInt(c.getColumnIndex(MediaStore.Audio.Media._ID));
                String fullpath = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album_name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int album_id = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String artist_name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                int artist_id = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                String more = "song_name: " + song_name
                        + "\nsong_id: " + song_id
                        + "\nfullpath: " + fullpath
                        + "\nalbum_name: " + album_name
                        + "\nalbum_id: " + album_id
                        + "\nartist_name: " + artist_name
                        + "\nartist_id: " + artist_id;
                audioModel.setMore(more);

                audioModel.setName(song_name);
                audioModel.setAlbum(album_name);
                audioModel.setArtist(artist_name);
                audioModel.setPath(fullpath);

                tempAudioList.add(audioModel);
            }
            c.close();
        }
        return tempAudioList;
    }

    public void playSelectedIndex(int index){
        if (audioModelList != null && audioModelList.size() > 0 && selectedIndex < audioModelList.size() - 1) {
            selectedIndex = index;
            System.out.println("==============>>>LEFTF"+audioModelList.get(selectedIndex).getPath());
            playAudio(audioModelList.get(selectedIndex).getPath());
            audioFileNameTV.setText("Playing:\n" + audioModelList.get(selectedIndex).getName());
            recyclerView.smoothScrollToPosition(selectedIndex);
            for (int i = 0; i < audioModelList.size(); i++) {
                audioModelList.get(i).setPlaying(false);
            }
            audioModelList.get(selectedIndex).setPlaying(true);
            audioModelListAdapter.setData(audioModelList);
            audioModelListAdapter.notifyDataSetChanged();
        }
    }

    public void playAudio(String path) {
        System.out.println("=========" + path);
        if (mp != null) mp.release();
        try {
            mp = new MediaPlayer();
//                mp.setDataSource(path + File.separator + fileName);
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("===exception==="+e.getMessage());
        }
        isPause = false;
    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MusicPlayer.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MusicPlayer.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MusicPlayer.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MusicPlayer.this, permissionsRequired[1])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayer.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MusicPlayer.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayer.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MusicPlayer.this.getPackageName(), null);
                        intent.setData(uri);
                        MusicPlayer.this.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(MusicPlayer.this.getBaseContext(), "Go to Permissions to Grant", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MusicPlayer.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.apply();
        } else {
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }
            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MusicPlayer.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MusicPlayer.this, permissionsRequired[1])
            ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayer.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MusicPlayer.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MusicPlayer.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        listAudioFilesToView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MusicPlayer.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null)
            mp.release();
    }
}