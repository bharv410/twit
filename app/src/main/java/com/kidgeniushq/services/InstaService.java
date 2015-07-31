package com.kidgeniushq.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kidgeniushq.handlers.HandleXML;
import com.kidgeniushq.instagram.InstagramApp;
import com.kidgeniushq.interfaces.MyPreferences;

import net.orange_box.storebox.StoreBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
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
        //get hotnewhh
        obj = new HandleXML(finalUrl);
        obj.fetchXML();

        Log.v("benmark", "obj.title " + obj.getTitle());







        //get ig
        mApp = new InstagramApp(this, client_id, client_secret, callback_url);
        if(mApp.hasAccessToken()){
            Toast.makeText(getApplicationContext(), "got ig images", Toast.LENGTH_SHORT).show();
            URL url;
            try {
                url = new URL(TAGSELFIE_URL + "?access_token=" + getSharedPreferences(SHARED, Context.MODE_PRIVATE).getString(API_ACCESS_TOKEN, null));
                new CheckPopularIg().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            Log.v("benmark", "not logged in");
            //not logged in
            showNotif("Not loggedin IG");
        }

        super.onHandleIntent(intent);
    }

    private class CheckPopularIg extends AsyncTask<URL, Void, Integer>  {
        private JSONArray jsonArr;

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
                Log.i("benmark", jsonArr.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonArr.length();
        }

        protected void onPostExecute(Integer result) {
            MyPreferences preferences = StoreBox.create(getApplicationContext(), MyPreferences.class);
            Set<String> allItems = preferences.getIGPosts() == null ? new LinkedHashSet<String>() : preferences.getIGPosts();
            int previousSize = (allItems == null ? 0 : allItems.size());

            Set<String> items = new LinkedHashSet<String>();
            try {
                for (int i = 0; i < jsonArr.length(); i++) {
                    items.add(jsonArr.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url"));

                    String username = jsonArr.getJSONObject(i).getJSONObject("user").getString("username");
                    if(username.equals("champagnepapi") || username.equals("kendalljenner") || username.equals("menacetodennis")
                            || username.equals("instagram") || username.equals("beyonce") || username.equals("kimkardashian")
                            || username.equals("taylorswift") || username.equals("selenagomez") || username.equals("nickiminaj")
                            || username.equals("mileycyrus") || username.equals("katyperry")) {
                        allItems.add(jsonArr.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url"));
                        showNotifWithPostOrNah(username + " just posted IG");
                    }
                }
                preferences.setIGPosts(allItems);
                int addedAmount = allItems.size() - previousSize;
                Log.v("benmark", "added " + String.valueOf(addedAmount));
                Log.v("benmark", "size =  " + String.valueOf(allItems.size()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
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

        int SERVER_DATA_RECEIVED = 1;
        notificationManager.notify(SERVER_DATA_RECEIVED, notification);
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
        notificationManager.notify(0, mBuilder.build());
    }
}