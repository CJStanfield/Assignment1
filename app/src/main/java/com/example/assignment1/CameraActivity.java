package com.example.assignment1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
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
    public static final String BASE_URL = "http://10.0.0.2/";

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

//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, );
//        }

    }

    public void uploadVideo(View view){
        //UploadFile file = new UploadFile(this.videoUri);
        final File sourceFile = new File(videoUri.getPath());
        progressDialog = new ProgressDialog(CameraActivity.this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String content_type = getFileType(sourceFile.getPath());
                String filePath = sourceFile.getAbsolutePath();

                OkHttpClient client = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse(content_type), sourceFile);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", content_type)
                        .addFormDataPart("uploaded_file", filePath.substring(filePath.lastIndexOf('/') +1), fileBody)
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:80/upload/upload.php")
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        progressDialog.dismiss();
                        //Toast.makeText(CameraActivity.this, ("error: " + response), Toast.LENGTH_LONG);
                    }
                    progressDialog.dismiss();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == VIDEO_REQUEST && resultCode == RESULT_OK){
//
//
//            //videoUri = data.getData();
//        }
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

    private String getFileType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}
