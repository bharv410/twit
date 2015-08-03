package com.kidgeniushq.staticstuff;

import android.util.Log;

import com.kidgeniushq.models.InstagramPost;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin.harvey on 8/3/15.
 */
public class MainCentralData {
    public static List<InstagramPost> allIGPostsInParse;
    public static List<String> allIGIds;
    public static List<InstagramPost> allHNHHArticles;

    public static void loadAllIGPosts(){
        allIGIds = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Instagram");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject cur : list){
                        String postId = cur.getString("postId");
                        allIGIds.add(postId);
                        Log.v("benmark", "pist id = " + postId);
                    }
                } else {
                    // something went wrong
                }
            }
        });
    }

    public static void loadAllHNHHArticles(){

    }
}
