package com.example.assignment1;

import android.content.Context;
import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Arrays;

public class ParseJson {
    String filePath;
    String json;
    int elementCount = 20;
    public int [] uploadCounts = new int[elementCount];

    public ParseJson(String file, Context context){
        this.filePath = file;
        Arrays.fill(uploadCounts, 0);
        try{
            InputStream inputStream = context.getAssets().open(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            this.json = new String(buffer);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //returns the json object of the current gesture based on the index
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

    //keeps track of how many times a video has been uploaded. This is not the best solution. A server side
    // api call that returns this information would be much better.
    public boolean updateJsonUploadCount(JSONObject object, int val){
        try{
            object.put("uploadCount", val);
            FileWriter file = new FileWriter(Environment.getDataDirectory().getAbsolutePath() + "/gesture_list.json");
            file.write(object.toString());
            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void updateUploadCount(int index){
        uploadCounts[index] = uploadCounts[index] + 1;
    }

    public int getUploadCount(int index){
        return uploadCounts[index];
    }
}
