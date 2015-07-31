package com.kidgeniushq.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class InstagramPost {
    String type, captionText, link, user, userProfilePic, standardImageUrl, postId, standardVideoUrl; //image
    int likes;

    public InstagramPost(JSONObject postInJSON){
        try{
            String theusername = postInJSON.getJSONObject("user").getString("username");
            this.user = theusername;
        }catch (JSONException jse){
        }
        try{
            String thestandardImageUrl = postInJSON.getJSONObject("images").getJSONObject("low_resolution").getString("url");
            this.standardImageUrl = thestandardImageUrl;
        }catch (JSONException jse){
        }
        try{
            String thestandardVideoUrl = postInJSON.getJSONObject("low_resolution").getString("url");
            this.standardVideoUrl = thestandardVideoUrl;
        }catch (JSONException jse){
        }
        try{
            this.type = postInJSON.getString("type");
            this.captionText = postInJSON.getJSONObject("caption").getString("text");
            this.link = postInJSON.getString("link");
            this.userProfilePic  = postInJSON.getJSONObject("user").getString("profile_picture");
            this.postId = postInJSON.getString("id");
            this.likes = postInJSON.getJSONObject("likes").getInt("count");
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
