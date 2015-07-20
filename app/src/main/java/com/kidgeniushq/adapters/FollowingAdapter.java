package com.kidgeniushq.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.kr.ingeni.twitterloginexample.R;
import twitter4j.User;

import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by benjamin.harvey on 7/19/15.
 */
public class FollowingAdapter extends ArrayAdapter<User> {
    private Activity activity;
    private LayoutInflater inflater;
    private List<User> followings;

    public FollowingAdapter(Activity activity, List<User> items) {
        super(activity,0,items);
        this.activity = activity;
        this.followings = items;
    }

    @Override
    public int getCount() {
        return followings.size();
    }

    @Override
    public User getItem(int location) {
        return followings.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        User m = followings.get(position);

        // thumbnail image
        Picasso.with(activity).load(m.getProfileImageURL()).into(thumbNail);

        // title
        title.setText(m.getScreenName());

        // rating
        rating.setText("Full name: " + String.valueOf(m.getName()));

        // genre

        genre.setText(m.getTimeZone());

        // release year
        year.setText(String.valueOf(m.getFollowersCount()));

        return convertView;
    }

}