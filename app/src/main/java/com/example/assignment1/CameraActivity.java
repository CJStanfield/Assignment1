package com.example.assignment1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    private static int VIDEO_REQUEST = 101;
    private Uri videoUri = null;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        videoView = findViewById(R.id.videoView_Camera);

    }

    public void captureVideo(View view){
        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);

        videoUri = Uri.fromFile(mediaFile);

        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        if(videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST);
        }
    }

    public void playRecordedVideo(View view){
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    public void uploadVideo(View view){
        UploadFile file = new UploadFile(this.videoUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == VIDEO_REQUEST && resultCode == RESULT_OK){
            videoUri = data.getData();
        }
    }

}
