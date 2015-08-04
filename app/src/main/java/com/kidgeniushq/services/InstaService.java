package com.kidgeniushq.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kidgeniushq.ArticlesDatasource;
import com.kidgeniushq.NotifToParseActivity;
import com.kidgeniushq.handlers.HandleXML;
import com.kidgeniushq.instagram.InstagramApp;
import com.kidgeniushq.interfaces.MyPreferences;
import com.kidgeniushq.models.HNHHArticle;
import com.kidgeniushq.models.InstagramPost;
import com.kidgeniushq.staticstuff.MainCentralData;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import net.orange_box.storebox.StoreBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Comment;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import co.kr.ingeni.twitterloginexample.MainActivity;
import co.kr.ingeni.twitterloginexample.R;
import co.kr.ingeni.twitterloginexample.SettingsActivity;

import static com.kidgeniushq.instagram.InstagramApp.streamToString;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class InstaService extends WakefulIntentService{

    private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
    private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
    private final String callback_url = "http://phantom.com";
    private final String TAGSELFIE_URL = "https://api.instagram.com/v1/users/self/feed";
    private static final String SHARED = "Instagram_Preferences";
    private static final String API_ACCESS_TOKEN = "access_token";

    private InstagramApp mApp;
    private InstagramApp.OAuthAuthenticationListener listener;

    private String finalUrl="http://feeds.feedburner.com/realhotnewhiphop.xml";
    private HandleXML obj;

    public InstaService() {
        super("InstaService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //articlesDatasource
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        checkHotNewHipHop();
        checkOtherSites();
        checkIG();
        scheduleNextUpdate();
        super.onHandleIntent(intent);
    }

    private void checkHotNewHipHop(){
        new CheckHotNewHipHop(finalUrl, this).execute();
    }
    private void checkOtherSites(){
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(1), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(2), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(3), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(4), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(5), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(6), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(7), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(8), this).execute();
        new CheckHotNewHipHop(MainCentralData.allArticleSourcesUrls.get(9), this).execute();
    }

    private void checkIG(){
        //get ig
        mApp = new InstagramApp(this, client_id, client_secret, callback_url);
        if(mApp.hasAccessToken()){
            URL url;
            try {
                url = new URL(TAGSELFIE_URL + "?access_token=" + getSharedPreferences(SHARED, Context.MODE_PRIVATE).getString(API_ACCESS_TOKEN, null));
                new CheckPopularIg().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Not logged in IG. cant get in background", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Not logged in IG. cant get in background", Toast.LENGTH_LONG).show();
        }
    }

    private class CheckPopularIg extends AsyncTask<URL, Void, Integer>  {
        private JSONArray jsonArr;

        protected Integer doInBackground(URL... urls) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String response = streamToString(urlConnection.getInputStream());

                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                jsonArr = jsonObj.getJSONArray("data");
                return jsonArr.length();
            } catch (Exception ex) {
                ex.printStackTrace();
                return 0;
            }
        }

        protected void onPostExecute(Integer result) {
            if (jsonArr != null) {
                MyPreferences preferences = StoreBox.create(getApplicationContext(), MyPreferences.class);
                Set<String> allItems = preferences.getIGPosts() == null ? new LinkedHashSet<String>() : preferences.getIGPosts();
                Set<String> items = new LinkedHashSet<String>();
                try {
                    for (int i = 0; i < jsonArr.length(); i++) {
                        InstagramPost igpost = new InstagramPost(jsonArr.getJSONObject(i));
                        items.add(igpost.getPostId());
                        showNotifIfNewPicIsPopular(igpost.getUsername(), igpost.getPostId(), allItems);
                        allItems.add(igpost.getPostId());
                    }

                    preferences.setIGPosts(allItems);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "No popular igs found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class CheckHotNewHipHop extends AsyncTask<URL, Void, Boolean>  {
        private String title = "title";
        private String link = "url";
        private String image = "image";
        private String description = "description";
        private String urlString = null;
        private XmlPullParserFactory xmlFactoryObject;

        String notif = "";

        public volatile boolean parsingComplete = true;
        Context context;

        private List<String> titles;

        public CheckHotNewHipHop(String url, Context ctx){
            this.urlString = url;
            this.context = ctx;
        }

        protected Boolean doInBackground(URL... urls) {
            titles = new ArrayList<String>();
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();
                InputStream stream = conn.getInputStream();

                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();

                myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                myparser.setInput(stream, null);

                parseXMLAndStoreIt(myparser, stream, urlString);
                return true;
            }catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(getApplicationContext(), "think of best way to handle", Toast.LENGTH_LONG).show();
            }

            MyPreferences preferences = StoreBox.create(getApplicationContext(), MyPreferences.class);
            Set<String> allItems = preferences.getArticles() == null ? new LinkedHashSet<String>() : preferences.getArticles();

//            for(HNHHArticle article : result){
//                if(article.getCaption().contains("Meek")){
//                    showNotifWithPostOrNah(articlesDatasource.createArticle(article.getCaption(), article.getInfo(), article.getLink()).getCaption(), true, "HotNewHipHop");
//                }
//            }

            allItems.add(notif);
            preferences.setArticles(allItems);

        }

        private void parseXMLAndStoreIt(XmlPullParser myParser, InputStream stream, String siteType) {
            int event;
            String text=null;

            try {
                event = myParser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    String name=myParser.getName();

                    switch (event){
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            text = myParser.getText();
                            break;

                        case XmlPullParser.END_TAG:

                            if(name.equals("title")){
                                title = text;
                            }

                            else if(name.equals("link")){
                                link = text;
                            }

                            else if(name.equals("description")){
                                description = text;
                            }

                            else if(name.equals("url")){
                            urlString = text;
                        }
                            break;
                    }
                    ParseObject testObject = new ParseObject("BandoPost");
                    testObject.put("siteType", siteType);
                    testObject.put("postUniqueId", title);
                    testObject.put("postText", title);

                        if(link!=null)
                            testObject.put("imageUrl", link);
                        if(description!=null)
                            testObject.put("description", description);
                        if(urlString!=null)
                            testObject.put("postUrl", urlString);

                    if(!titles.contains(title)){
                        titles.add(title);
                        testObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null)
                                    Log.v("benmark", "saved!");
                                else
                                    Log.v("benmark", "error!" + String.valueOf(e.getCode()));
                            }
                        });
                    }else{
                        Log.v("benmark", "skipping duplicate of " + title);
                    }
                        event = myParser.next();
                }

                parsingComplete = false;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                stream.close();
            }catch (IOException e){

            }
        }
    }

    private void showNotifIfNewPicIsPopular(String username, String postId, Set<String> allItems){
        if(username!=null && !allItems.contains(postId) && MainCentralData.allInstagramSourceNames.contains(username)) {
            showNotifWithPostOrNah(username + " just posted IG", false, "instagram");
        }
    }

    private void showNotif(String text){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.gear;
        CharSequence notiText = "Your notification from the service";
        long meow = System.currentTimeMillis();

        Notification notification = new Notification(icon, notiText, meow);

        Context context = getApplicationContext();
        CharSequence contentTitle = text;
        CharSequence contentText = text;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notification);
    }

    private void showNotifWithPostOrNah(String text, boolean isArticle, String postVia){
        Intent sendToParse = new Intent(this, NotifToParseActivity.class);
        sendToParse.putExtra("isArticle", isArticle);
        sendToParse.putExtra("postvia", postVia);
        sendToParse.putExtra("postText", text);
        PendingIntent pendingIntentPost = PendingIntent.getBroadcast(this, 0, sendToParse, PendingIntent.FLAG_UPDATE_CURRENT);

        //building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_upload)
                .setContentTitle(text)
                .setTicker(text)
                .setColor(Color.RED)
                .addAction(android.R.drawable.sym_action_email, "Post Now", pendingIntentPost)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel Upload", pendingIntentPost);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, mBuilder.build());
    }

    private void scheduleNextUpdate()
    {
        Intent intent = new Intent(this, this.getClass());
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // The update frequency should often be user configurable.  This is not.

        long currentTimeMillis = System.currentTimeMillis();
        long nextUpdateTimeMillis = currentTimeMillis + (30*60 * DateUtils.SECOND_IN_MILLIS);
        Time nextUpdateTime = new Time();
        nextUpdateTime.set(nextUpdateTimeMillis);

        if (nextUpdateTime.hour < 7 || nextUpdateTime.hour >= 23)
        {
            nextUpdateTime.hour = 8;
            nextUpdateTime.minute = 0;
            nextUpdateTime.second = 0;
            nextUpdateTimeMillis = nextUpdateTime.toMillis(false) + DateUtils.DAY_IN_MILLIS;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
    }
}