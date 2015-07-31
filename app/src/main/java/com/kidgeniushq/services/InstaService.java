package com.kidgeniushq.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.kidgeniushq.handlers.HandleXML;
import com.kidgeniushq.instagram.InstagramApp;
import com.kidgeniushq.interfaces.MyPreferences;
import com.kidgeniushq.models.InstagramPost;

import net.orange_box.storebox.StoreBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import co.kr.ingeni.twitterloginexample.MainActivity;
import co.kr.ingeni.twitterloginexample.R;
import co.kr.ingeni.twitterloginexample.SettingsActivity;
import android.os.Handler;

import static com.kidgeniushq.instagram.InstagramApp.streamToString;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class InstaService extends WakefulIntentService{

    private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
    private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
    private final String callback_url = "http://phantom.com";
    private final String TAGSELFIE_URL = "https://api.instagram.com/v1/media/popular";
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
    protected void onHandleIntent(Intent intent) {
        checkHotNewHipHop();
        checkIG();
        scheduleNextUpdate();
        super.onHandleIntent(intent);
    }

    private void checkHotNewHipHop(){
        new CheckHotNewHipHop(finalUrl, this).execute();
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonArr.length();
        }

        protected void onPostExecute(Integer result) {
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
        }
    }

    private class CheckHotNewHipHop extends AsyncTask<URL, Void, String>  {
        private String title = "title";
        private String link = "url";
        private String image = "image";
        private String description = "description";
        private String urlString = null;
        private XmlPullParserFactory xmlFactoryObject;

        String notif = "";

        public volatile boolean parsingComplete = true;
        Context context;

        public CheckHotNewHipHop(String url, Context ctx){
            this.urlString = url;
            this.context = ctx;
        }

        protected String doInBackground(URL... urls) {
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

                parseXMLAndStoreIt(myparser);
                stream.close();
            }

            catch (Exception e) {
            }
            return "";
        }

        protected void onPostExecute(String result) {
            MyPreferences preferences = StoreBox.create(getApplicationContext(), MyPreferences.class);
            Set<String> allItems = preferences.getArticles() == null ? new LinkedHashSet<String>() : preferences.getArticles();

            if(notif.equals("") || allItems.contains(result))
                Log.v("benmark", "no meek hhnhh");
            else{
                showNotifWithPostOrNah(notif);
            }

            allItems.add(notif);
            preferences.setArticles(allItems);

        }
        public void parseXMLAndStoreIt(XmlPullParser myParser) {
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
                                if(title.contains("Meek"))
                                    notif = title;
                            }

                            else if(name.equals("link")){
                                link = text;
                                if(link.contains("Meek"))
                                    notif = link;
                            }

                            else if(name.equals("description")){
                                description = text;
                            }

                            else{
                            }

                            break;
                    }

                    event = myParser.next();
                }

                parsingComplete = false;
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotifIfNewPicIsPopular(String username, String postId, Set<String> allItems){
        if(username!=null && !allItems.contains(postId) && (username.equals("champagnepapi") || username.equals("kendalljenner") || username.equals("menacetodennis")
                || username.equals("instagram") || username.equals("beyonce") || username.equals("kimkardashian")
                || username.equals("taylorswift") || username.equals("selenagomez") || username.equals("nickiminaj")
                || username.equals("mileycyrus") || username.equals("katyperry"))) {
            showNotifWithPostOrNah(username + " just posted IG");
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

    private void showNotifWithPostOrNah(String text){
        Intent deleteIntent = new Intent(this, SettingsActivity.class);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_upload)
                .setContentTitle(text)
                .setTicker(text)
                .setColor(Color.RED)
                .addAction(android.R.drawable.sym_action_email, "Post Now", pendingIntentCancel)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel Upload", pendingIntentCancel);

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
        long nextUpdateTimeMillis = currentTimeMillis + 15 * DateUtils.SECOND_IN_MILLIS;
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