package co.kr.ingeni.twitterloginexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;

import com.kidgeniushq.adapters.TweetsAdapter;
import com.kidgeniushq.adapters.UsersAdapter;
import com.kidgeniushq.asynctasks.GetTweetsAsync;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import twitter4j.Status;
import twitter4j.User;

public class MainActivity extends Activity {

	public static boolean loggedIn = false;

	private ImageView twitterLoginBtn,todImageView;
	private RelativeLayout tweetOfTheDayLayout;
	private ListView followingListView;
	private TextView todTextView;
	private FloatingActionButton fab;

	ProgressDialog progress;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setTitle("");

		tweetOfTheDayLayout = (RelativeLayout)findViewById(R.id.tweetofthedaylayout);
		todImageView = (ImageView)findViewById(R.id.todImageView);
		todTextView = (TextView)findViewById(R.id.todText);
		tweetOfTheDayLayout.setVisibility(View.INVISIBLE);
		todImageView.setVisibility(View.INVISIBLE);
		todTextView.setVisibility(View.INVISIBLE);
		twitterLoginBtn = (ImageView) findViewById(R.id.twitter_login_btn);
		Picasso.with(getApplicationContext()).load(getString(R.string.login_button_url)).into(twitterLoginBtn);

		twitterLoginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TwitterLogin.getTwitterLoginCheck() != true) {
					TwitterLogin twitterLogin = new TwitterLogin(MainActivity.this);
					twitterLogin.setTwitterLogin();
				}
			}
		});

		followingListView = (ListView)findViewById(R.id.list);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.attachToListView(followingListView);
		fab.hide(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (TwitterLogin.getTwitterLoginCheck() != false && MainActivity.loggedIn) {
			twitterLoginBtn.setVisibility(View.GONE);
			progress = ProgressDialog.show(this, "Please wait",
					"loading friends", true);
			//new GetFollowingAsync(MainActivity.this).execute();
			new GetTweetsAsync(MainActivity.this).execute();
		}
	}

	public void setFollowing(ArrayList<User> following){
		progress.dismiss();

		UsersAdapter adapterForFollowings = new UsersAdapter(getApplicationContext(), following);
		followingListView.setAdapter(adapterForFollowings);
		adapterForFollowings.notifyDataSetChanged();
	}

	public void setTweets(ArrayList<Status> tweets){
		progress.dismiss();
		mHandler.postDelayed(new Runnable() {
			public void run() {
				fab.show();
			}
		}, 1000);

		todImageView.setVisibility(View.VISIBLE);
		todTextView.setVisibility(View.VISIBLE);
		Picasso.with(getApplicationContext()).load(tweets.get(0).getUser().getBiggerProfileImageURL()).into(todImageView);
		todTextView.setText(tweets.get(0).getText());

		tweetOfTheDayLayout.setVisibility(View.VISIBLE);
		TweetsAdapter adapterForTweets = new TweetsAdapter(getApplicationContext(), new ArrayList<Status>(tweets.subList(1,5)));
		followingListView.setAdapter(adapterForTweets);
		adapterForTweets.notifyDataSetChanged();
	}

	public void goToSettings(View v){
		startActivity(new Intent(MainActivity.this, SettingsActivity.class));
	}
}
