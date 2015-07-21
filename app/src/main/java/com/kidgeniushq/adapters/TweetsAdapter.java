package com.kidgeniushq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.kr.ingeni.twitterloginexample.R;
import twitter4j.Status;

/**
 * Created by benjamin.harvey on 7/19/15.
 */
public class TweetsAdapter extends ArrayAdapter<Status> {

    public TweetsAdapter(Context context, ArrayList<Status> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Status m = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        Picasso.with(getContext()).load(m.getUser().getBiggerProfileImageURL()).into(thumbNail);
        //title.setText(m.getText());
        rating.setText(m.getText());
        //genre.setText("RTs = " + String.valueOf(m.getRetweetCount()));
        //year.setText(String.valueOf(m.getInReplyToScreenName()));

        return convertView;
    }
}