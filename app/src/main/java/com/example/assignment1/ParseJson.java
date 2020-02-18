package com.example.assignment1;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class ParseJson {
    String filePath;
    String json;

    public ParseJson(String file, Context context){
        this.filePath = file;
        try{
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            this.json = new String(buffer);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public JSONObject get_json(int index){
        try {
            JSONObject obj = new JSONObject(this.json);
            JSONArray jsonArray = (JSONArray) obj.get("Gestures");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject temp = jsonArray.getJSONObject(i);
                int gesture_number = temp.getInt("id");
                if(gesture_number == index)
                    return temp;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
