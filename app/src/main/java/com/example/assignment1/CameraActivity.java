package com.example.assignment1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CameraActivity extends AppCompatActivity {

    private static int VIDEO_REQUEST = 101;
    private Uri videoUri = null;
    private VideoView videoView;
    private static final int REQUEST_PERMISSION = 1000;
    public static final String BASE_URL = "http://192.168.86.250:80";
    public static final String ENDPOINT = "/upload/upload.php";

    public ProgressDialog progressDialog;

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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

        videoView = findViewById(R.id.videoView_Camera);

    }

    public void captureVideo(View view){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + createFileName());

        //Create a video intent to capture 5 sec worth of video
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);

        //video uri is a global variable with the video uri
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

    //uploadVideo is responsible for sending the .mp4 file to the webserver.
    public void uploadVideo(View view){
        final File sourceFile = new File(videoUri.getPath());
        progressDialog = new ProgressDialog(CameraActivity.this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String fileType = getFileType(sourceFile.getPath());
                String filePath = sourceFile.getAbsolutePath();

                OkHttpClient client = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse(fileType), sourceFile);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", fileType)
                        .addFormDataPart("uploaded_file", filePath.substring(filePath.lastIndexOf('/') +1), fileBody)
                        .build();
                Request request = new Request.Builder()
                        .url(BASE_URL + ENDPOINT)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
                    //update the json file after the video has been uploaded successfully
                    MainActivity.gestureJson.updateUploadCount(MainActivity.currentGestureId);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    //getFileType returns .mp4 and is added to the request body
    private String getFileType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
    //Creates the file name for each video in the format described in the project description
    private String createFileName(){
        return "/" + MainActivity.currentGesture + "_Practice_" + MainActivity.gestureJson.getUploadCount(MainActivity.currentGestureId) + "_Stanfield.mp4";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"permission granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
