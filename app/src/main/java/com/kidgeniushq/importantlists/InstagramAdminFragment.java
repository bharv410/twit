package com.kidgeniushq.importantlists;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.kr.ingeni.twitterloginexample.R;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class InstagramAdminFragment extends ListFragment {
    ArrayList<String> linkArray;
    ArrayList<String> photoArray;
    ArrayList<String> captionsArray;
    ArrayList<String> usernamesArray;
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
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton("post", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "right now it shows eerything not deleted", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .setNegativeButton("delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoPost");
                                query.whereEqualTo("postId", linkArray.get(arg2));
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if(e==null){
                                            for(ParseObject po : list){
                                                try {
                                                    po.delete();
                                                    onStart();
                                                }catch (ParseException pe){
                                                    Log.v("benmark","error deleting + }" + String.valueOf(pe.getCode()));
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        linkArray = new ArrayList<String>();
        photoArray = new ArrayList<String>();
        captionsArray = new ArrayList<String>();
        usernamesArray = new ArrayList<String>();
        urlArray = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("BandoPost");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject po : objects) {
                        linkArray.add(po.getString("postId"));
                        photoArray.add(po.getString("imageUrl"));
                        usernamesArray.add(po.getString("username"));
                        captionsArray.add(po.getString("captionText"));
                        urlArray.add(po.getString("link"));
                    }
                    MySimpleArrayAdapter a = new MySimpleArrayAdapter(getActivity(), usernamesArray, photoArray, captionsArray);
                    //ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, linkArray);
                    setListAdapter(a);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlArray.get(position))));
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final List<String> values, values2, values3;

        public MySimpleArrayAdapter(Context context, List<String>  values, List<String>  values2, List<String>  values3) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
            this.values2 = values2;
            this.values3 = values3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.iglistitem, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.itemBigText);
            TextView otherTextView = (TextView) rowView.findViewById(R.id.itemSmallText);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.itemImage);

            imageView.setImageResource(R.drawable.ic_gear_normal);
            Picasso.with(context).load(values2.get(position)).into(imageView);

            textView.setText(values.get(position));
            otherTextView.setText(values3.get(position));

            return rowView;
        }
    }
}