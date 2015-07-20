package com.kidgeniushq.asynctasks;

import android.os.AsyncTask;

import com.kidgeniushq.twitter.TwitterLogin;

import java.util.ArrayList;

import co.kr.ingeni.twitterloginexample.MainActivity;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by benjamin.harvey on 7/19/15.
 */
public class GetFollowingAsync extends AsyncTask<Void, Void, ArrayList<User>> {

    ArrayList<User> followings;
    private MainActivity activity;

    public GetFollowingAsync (MainActivity activity){
        this.activity=activity;
        followings = new ArrayList<User>();
    }

    @Override
    protected ArrayList<User> doInBackground(Void... arg0) {
        try{
            long cursor=-1;
            int count=0;

            while(cursor!=0)
            {
                PagableResponseList<User> friendlist= TwitterLogin.twitter.getFriendsList(TwitterLogin.twitterUserData.getScreenName(),cursor);
                int sizeoffreindlist= friendlist.size();
                for(int i=0;i<sizeoffreindlist;i++)
                {
                    followings.add(friendlist.get(i));
                }
                cursor=friendlist.getNextCursor();
            }

        }catch (TwitterException twe){}
        return followings;
    }

    @Override
    protected void onPostExecute(ArrayList<User> followings) {
        super.onPostExecute(followings);
        activity.setFollowing(followings);
    }
}