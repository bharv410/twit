package com.kidgeniushq.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import co.kr.ingeni.twitterloginexample.MainActivity;
import co.kr.ingeni.twitterloginexample.TwitterLogin;
import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.Status;

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
        super.onPostExecute(tweets);
        activity.setTweets(tweets);
    }
}