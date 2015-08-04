package com.kidgeniushq.models;

import android.util.Log;

import com.kidgeniushq.staticstuff.MainCentralData;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class InstagramPost {
    String type, captionText, link, user, userProfilePic, standardImageUrl, postId, standardVideoUrl; //image
    int likes;

    public InstagramPost(JSONObject postInJSON){
        ParseObject testObject = new ParseObject("Instagram");

        try{
            String theusername = postInJSON.getJSONObject("user").getString("username");
            this.user = theusername;
            testObject.put("username", user);
        }catch (JSONException jse){
        }
        try{
            String thestandardImageUrl = postInJSON.getJSONObject("images").getJSONObject("low_resolution").getString("url");
            this.standardImageUrl = thestandardImageUrl;
            testObject.put("imageUrl", standardImageUrl);
        }catch (JSONException jse){
        }
        try{
            String thestandardVideoUrl = postInJSON.getJSONObject("low_resolution").getString("url");
            this.standardVideoUrl = thestandardVideoUrl;
            testObject.put("standardVideoUrl", standardVideoUrl);
        }catch (JSONException jse){
        }
        try{
            this.type = postInJSON.getString("type");
            testObject.put("type", type);
            this.captionText = postInJSON.getJSONObject("caption").getString("text");
            testObject.put("captionText", captionText);
            this.link = postInJSON.getString("link");
            testObject.put("link", link);
            this.userProfilePic  = postInJSON.getJSONObject("user").getString("profile_picture");
            testObject.put("userProfilePic", userProfilePic);
            this.postId = postInJSON.getString("id");
            testObject.put("postId", postId);
            this.likes = postInJSON.getJSONObject("likes").getInt("count");
            testObject.put("likes", likes);

            if(MainCentralData.allInstagramSourceNames.contains(user)){
                testObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            Log.v("benmark", "saved " + captionText);
                        else
                            Log.v("benmark", "error saving " + e.getLocalizedMessage() + "error corde = " + String.valueOf(e.getCode()));
                    }
                });
            }
        }catch (JSONException jse){
        }
    }

    public String getUsername(){
        return user;
    }

    public String getPostId(){
        return postId;
    }

    public String getCaptionText() {
        return captionText;
    }

    public int getLikes() {
        return likes;
    }

    public String getStandardImageUrl() {
        return standardImageUrl;
    }

    public String getLink() {
        return link;
    }

    public String getStandardVideoUrl() {
        return standardVideoUrl;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }
}
