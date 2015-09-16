package com.kidgeniushq.staticstuff;

import android.util.Log;

import com.kidgeniushq.models.HNHHArticle;
import com.kidgeniushq.models.InstagramPost;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin.harvey on 8/3/15.
 */
public class MainCentralData {
    public static List<InstagramPost> allIGPostsInParse;
    public static List<HNHHArticle> allHNHHArticles;


    //list of strings
    public static List<String> allIGIds, allHNHHTitles;
    public static List<String> allArticleSourcesUrls = getAllRSSFeedUrls();
    public static List<String> allArticleSourcesNames = getAllRSSFeedNames();
    public static List<String> allInstagramSourceNames = getAllInstagramNames();
    public static List<String> allTwitterSourceNames = getAllTwitterNames();
    public static List<String> allSoundcloudSourceNames = getAllSoundcloudNames();
    public static List<String> tabTitles = new ArrayList<String>() {{
        add("Articles");
        add("Instagram");
        add("Twitter");
        add("Soundcloud");
    }};

    public static void loadAllIGPosts(){
        allIGIds = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoPost");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject cur : list){
                        String postId = cur.getString("postId");
                        allIGIds.add(postId);
                    }
                } else {
                    // something went wrong
                }
            }
        });
    }

    public static void loadAllHNHHArticles(){
        allHNHHTitles = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HotNewHipHop");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject cur : list){
                        String postId = cur.getString("postTitle");
                        allHNHHTitles.add(postId);
                    }
                } else {
                    // something went wrong
                }
            }
        });
    }

    private static ArrayList<String> getAllRSSFeedUrls(){
        ArrayList<String> allArticleSourceUrl = new ArrayList<String>();
        allArticleSourceUrl.add("http://www.rap-up.com/feed/");
        allArticleSourceUrl.add("http://assets.complex.com/feeds/channels/music.xml");
        allArticleSourceUrl.add("http://assets.complex.com/feeds/channels/all.xml");
        allArticleSourceUrl.add("http://feeds.feedburner.com/realhotnewhiphop.xml");
        allArticleSourceUrl.add("http://www.power1051fm.com/podcast/breakfastclub_interviews.xml");
        allArticleSourceUrl.add("http://feeds.feedburner.com/TheFaderMagazine/");
        allArticleSourceUrl.add("http://noisey.vice.com/rss");
        allArticleSourceUrl.add("http://hiphopdx.com/rss/news.xml");
        allArticleSourceUrl.add("http://feeds.feedburner.com/hypebeast/feed");
        allArticleSourceUrl.add("http://feeds.feedburner.com/highsnobiety/rss?format=xml");
        allArticleSourceUrl.add("http://sports.espn.go.com/espn/rss/news");
        allArticleSourceUrl.add("http://www.vibe.com/category/vixen/feed/");
        return allArticleSourceUrl;
    }

    private static ArrayList<String> getAllRSSFeedNames(){
        ArrayList<String> allArticleSourceNames = new ArrayList<String>();
        allArticleSourceNames.add("rap-up");
        allArticleSourceNames.add("complex music");
        allArticleSourceNames.add("complex");
        allArticleSourceNames.add("hotnewhiphop");
        allArticleSourceNames.add("breakfastclub");
        allArticleSourceNames.add("TheFaderMagazine/");
        allArticleSourceNames.add("noisey");
        allArticleSourceNames.add("hiphopdx");
        allArticleSourceNames.add("hypebeast");
        allArticleSourceNames.add("highsnobiety");
        allArticleSourceNames.add("espn");
        allArticleSourceNames.add("vibe");
        return allArticleSourceNames;
    }

    private static ArrayList<String> getAllTwitterNames(){
        ArrayList<String> allArticleSourceNames = new ArrayList<String>();
        allArticleSourceNames.add("MeekMill");
        allArticleSourceNames.add("Drake");
        allArticleSourceNames.add("1future");
        allArticleSourceNames.add("kendricklamar");
        allArticleSourceNames.add("rihanna");
        return allArticleSourceNames;
    }

    private static ArrayList<String> getAllInstagramNames(){
        ArrayList<String> allArticleSourceNames = new ArrayList<String>();
        allArticleSourceNames.add("MeekMill");
        allArticleSourceNames.add("Future");
        allArticleSourceNames.add("kevinhart4real");
        allArticleSourceNames.add("BdotAdot5");
        allArticleSourceNames.add("karrueche");
        allArticleSourceNames.add("champagnepapi");
        allArticleSourceNames.add("partyomo");
        allArticleSourceNames.add("floydmayweather");
        allArticleSourceNames.add("nickiminaj");
        allArticleSourceNames.add("kendalljenner");
        allArticleSourceNames.add("kyliejenner");
        allArticleSourceNames.add("badgirlriri");
        allArticleSourceNames.add("treysongz");
        allArticleSourceNames.add("chrisbrownofficial");
        return allArticleSourceNames;
    }

    private static ArrayList<String> getAllSoundcloudNames(){
        ArrayList<String> allArticleSourceNames = new ArrayList<String>();
        allArticleSourceNames.add("octobersveryown");
        allArticleSourceNames.add("topdawgent");
        allArticleSourceNames.add("future");
        allArticleSourceNames.add("metroboomin");
        allArticleSourceNames.add("FettyWap1738");
        allArticleSourceNames.add("future");
        allArticleSourceNames.add("partyomo");
        allArticleSourceNames.add("theweekndbible");
        return allArticleSourceNames;
    }
}
