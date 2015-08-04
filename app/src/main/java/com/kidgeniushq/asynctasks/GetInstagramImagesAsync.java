package com.kidgeniushq.asynctasks;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.os.AsyncTask;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import co.kr.ingeni.twitterloginexample.MainActivity;
import co.kr.ingeni.twitterloginexample.R;

/**
 * Created by benjamin.harvey on 7/20/15.
 */

import static com.kidgeniushq.instagram.InstagramApp.streamToString;

public class GetInstagramImagesAsync extends AsyncTask<URL, Void, Integer> {
    private JSONArray jsonArr;
    private MainActivity activity;

    public GetInstagramImagesAsync (MainActivity activity){
        this.activity=activity;
    }

    protected Integer doInBackground(URL... urls) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            //urlConnection.setDoOutput(true);
            urlConnection.connect();
            String response = streamToString(urlConnection.getInputStream());

            System.out.println(response);
            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            jsonArr = jsonObj.getJSONArray("data");
            System.out.println("jsonArr" + jsonArr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonArr.length();
    }

    protected void onPostExecute(Integer result) {
        ArrayList<String> items = new ArrayList<String>();
        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                items.add(jsonArr.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        activity.setImageUrl(items.get(1));

    }
}