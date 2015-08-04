package com.kidgeniushq.importantlists;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class ExampleListFragment extends ListFragment {
    ArrayList<String> linkArray;
    ArrayList<String> urlArray;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
//        listView.setDivider(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_light)));
        listView.setDividerHeight(1);
        listView.setVerticalScrollBarEnabled(false);
        listView.setBackgroundColor(Color.LTGRAY);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        urlArray = new ArrayList<String>();
        linkArray = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoPost");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject po : objects) {
                        linkArray.add(po.getString("postText"));
                        urlArray.add(po.getString("postUrl"));
                    }
                    ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, linkArray);
                    setListAdapter(a);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlArray.get(position))));
    }
}