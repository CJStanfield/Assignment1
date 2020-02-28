package com.example.assignment1;

import android.net.Uri;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFile {
    Uri videoUri = null;
    int serviceResponseCode = 0;
    public UploadFile(Uri uri){
        this.videoUri = uri;
        uploadFile();
    }

    public int uploadFile(){
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        File sourceFile = new File(this.videoUri.getPath());

        if(!sourceFile.isFile()){
            return 0;
        }else
        {
            try{
                //open a url connection to the serverlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://coles-macbook-pro.local:8080");

                //Open a HTTP connection to the URL
                connection = (HttpURLConnection) url.openConnection();



                return 1;


            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }
    }
}
