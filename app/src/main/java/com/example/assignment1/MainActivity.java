package com.example.assignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.assignment1.ParseJson;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    Button button_play;
    VideoView videoView;
    TextView current_gesture_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer =findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Up video
        button_play = findViewById(R.id.button_play);
        videoView = findViewById(R.id.videoView);
        current_gesture_title = findViewById(R.id.textView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        onBackPressed();

        int index = Integer.parseInt(menuItem.getTitleCondensed().toString());
        ParseJson json = new ParseJson("gesture_list.json", this);
        JSONObject gesture_obj = json.get_json(index);

        try {
            current_gesture_title.setText(gesture_obj.getString("name"));
            Uri uri = Uri.parse(gesture_obj.getString("url"));
            videoView.setVideoURI(uri);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public void videoPlay(View v){
//        String videoPath = "https://www.signingsavvy.com/media/mp4-ld/24/24851.mp4";
//        Uri uri = Uri.parse(videoPath);
//        videoView.setVideoURI(uri);
        videoView.start();
    }
}
