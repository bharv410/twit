package com.kidgeniushq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.kidgeniushq.interfaces.MyPreferences;
import com.kidgeniushq.staticstuff.MainCentralData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import net.orange_box.storebox.StoreBox;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import co.kr.ingeni.twitterloginexample.R;
import co.kr.ingeni.twitterloginexample.VerifyPostActivity;

/**
 * Created by benjamin.harvey on 8/6/15.
 */
public class HnHHFragment extends Fragment{
    private List<String> titles,links;
    private String finalUrl="http://feeds.feedburner.com/realhotnewhiphop.xml";
    private String titleNameForReal;

    public HnHHFragment(){
        Random num = new Random();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_articles,
                container, false);

//        listView.setDivider(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_light)));
//        listView.setDividerHeight(1);
//        listView.setVerticalScrollBarEnabled(false);
//        listView.setBackgroundColor(Color.LTGRAY);
        finalUrl = getActivity().getIntent().getStringExtra("url");
        new CheckHotNewHipHop(finalUrl, getActivity()).execute();

        return view;
    }

    private class CheckHotNewHipHop extends AsyncTask<URL, Void, Boolean> {
        private String title = "title";
        private String link = "url";
        private String image = "src";
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

        protected Boolean doInBackground(URL... urls) {
            titles = new ArrayList<String>();
            titles.add("Erase these top 3");
            links = new ArrayList<String>();
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
                ListView listView = (ListView) getActivity().findViewById(R.id.listView);
                final ArrayAdapter<String> allTitlesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,titles);
                listView.setAdapter(allTitlesAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String titleeee = titles.get(i);
                        String linkkkk = links.get(i);
                        new DownloadTask(getActivity(), titleeee, linkkkk).execute(linkkkk);
                    }
                });
            }
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

                            }else if(name.equals("src")) {
                                Log.v("benmark", "imagetext = " + text);
                                image = text;
                            }

                            break;
                    }
                    if(!titles.contains(title)){
                        titles.add(title);
                        links.add(link);
                        Log.v("benmark", "link = " + String.valueOf(links.size()) + "= " + link);
                        Log.v("benmark", "title " + String.valueOf(titles.size()) + "= " + title);
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

    private class DownloadTask extends AsyncTask<String, Void, String> {
        private Context context;
        private String title, url;

        public DownloadTask(Context ctx, String pos, String link){
            this.context = ctx;
            this.title = pos;
            this.url = link;
        }
        @Override
        protected String doInBackground(String... urls) {
            HttpResponse response = null;
            HttpGet httpGet = null;
            HttpClient mHttpClient = null;
            String s = "";

            try {
                if(mHttpClient == null){
                    mHttpClient = new DefaultHttpClient();
                }

                httpGet = new HttpGet(urls[0]);


                response = mHttpClient.execute(httpGet);
                s = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.v("benmark", "full response = " + s);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String result){
            if(result.contains("og:image")){
                Log.v("benmark", "gheyy");
                String[] parts = result.split("\"og:image\"");
                if(parts.length==1){
                    Toast.makeText(getActivity(), "NO OG IMAGE FOUND", Toast.LENGTH_LONG).show();
                }else{
                    String partAfterOGImage = parts[1];
                    Log.v("benmark", "found partAfterOGImage " + partAfterOGImage);
                    String[] getRidOfTheEnd = partAfterOGImage.split("/><m");
                    String theLinkSHouldBe = getRidOfTheEnd[0].split("\"")[1];

                    Log.v("benmark", " theLinkSHouldBe " + theLinkSHouldBe);
                    updateParseOgImage(theLinkSHouldBe, title, url);
                }
            }
        }
    }

    private void updateParseOgImage(String link , final String caption, String url){
        Log.v("benmark", link);
        Intent intent = new Intent(getActivity(), VerifyPostActivity.class);
        intent.putExtra("url",link);
        intent.putExtra("caption",caption);
        intent.putExtra("link",url);
        startActivity(intent);
    }
}
