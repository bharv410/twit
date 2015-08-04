package com.kidgeniushq.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.kidgeniushq.twitter.TwitterLogin;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

import co.kr.ingeni.twitterloginexample.MainActivity;
import twitter4j.ResponseList;
import twitter4j.TwitterException;

/**
 * Created by benjamin.harvey on 7/19/15.
 */
public class GetTweetsAsync extends AsyncTask<Void, Void, ArrayList<twitter4j.Status>> {

    ArrayList<twitter4j.Status> tweets;
    private MainActivity activity;

    public GetTweetsAsync (MainActivity activity){
        this.activity=activity;
        tweets = new ArrayList<twitter4j.Status>();
    }

    @Override
    protected ArrayList<twitter4j.Status> doInBackground(Void... arg0) {
        try{
            ResponseList<twitter4j.Status> response = TwitterLogin.twitter.timelines().getHomeTimeline();
            for(twitter4j.Status st : response){
                tweets.add(st);
            }
        }catch (TwitterException twe){}
        return tweets;
    }

    @Override
    protected void onPostExecute(ArrayList<twitter4j.Status> tweets) {
        for(int i=0; i<100 && i<tweets.size(); i++){
            ParseObject testObject = new ParseObject("Tweets");
            testObject.put("siteType", "twitter");
            testObject.put("postUniqueId", String.valueOf(tweets.get(i).getId()));
            testObject.put("postText", tweets.get(i).getText());
            testObject.put("username", tweets.get(i).getUser().getScreenName());
            testObject.put("imageUrl", tweets.get(i).getSource());
            if(tweets.get(i).getMediaEntities()[0].getMediaURL()!=null)
                testObject.put("imageUrl",tweets.get(i).getMediaEntities()[0].getMediaURL() );
             testObject.saveInBackground(new SaveCallback() {
                 @Override
                 public void done(ParseException e) {
                     if (e == null)
                         Log.v("benmark", "saved!");
                     else
                         Log.v("benmark", "error!" + String.valueOf(e.getCode()));
                 }
             });
        }
        super.onPostExecute(tweets);
        activity.setTweets(tweets);
    }
}