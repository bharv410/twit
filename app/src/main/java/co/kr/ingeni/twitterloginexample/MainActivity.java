package co.kr.ingeni.twitterloginexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.kidgeniushq.adapters.TweetsAdapter;
import com.kidgeniushq.adapters.UsersAdapter;
import com.kidgeniushq.asynctasks.GetInstagramImagesAsync;
import com.kidgeniushq.asynctasks.GetTweetsAsync;
import com.kidgeniushq.instagram.InstagramApp;
import com.kidgeniushq.services.InstaService;
import com.kidgeniushq.twitter.TwitterLogin;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import android.widget.AdapterView.OnItemClickListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import twitter4j.Status;
import twitter4j.User;

public class MainActivity extends Activity {

	private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
	private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
	private final String callback_url = "http://phantom.com";
	private final String TAGSELFIE_URL = "https://api.instagram.com/v1/tags/selfie/media/recent";
	private static final String SHARED = "Instagram_Preferences";
	private static final String API_ACCESS_TOKEN = "access_token";

	private InstagramApp mApp;
	private InstagramApp.OAuthAuthenticationListener listener;

	//done ig stuff

	public static boolean loggedInTwitter = false;

	private ImageView twitterLoginBtn,todImageView;
	private RelativeLayout tweetOfTheDayLayout;
	private ListView followingListView;
	private TextView todTextView,todTitleTextView;
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

		twitterLoginBtn.setVisibility(View.GONE);

		followingListView = (ListView)findViewById(R.id.list);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.attachToListView(followingListView);

		tweetOfTheDayLayout.setVisibility(View.VISIBLE);
		todTextView.setVisibility(View.VISIBLE);
		todTextView.setTextColor(Color.BLUE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (TwitterLogin.getTwitterLoginCheck() != false && MainActivity.loggedInTwitter) {
			twitterLoginBtn.setVisibility(View.GONE);
			progress = ProgressDialog.show(this, "Please wait",
					"loading friends", true);
			//new GetFollowingAsync(MainActivity.this).execute();
			new GetTweetsAsync(MainActivity.this).execute();
		}
		mApp = new InstagramApp(this, client_id, client_secret, callback_url);
		if(mApp.hasAccessToken()){

			startService(new Intent(getApplicationContext(), InstaService.class));

			Toast.makeText(getApplicationContext(), "got ig images", Toast.LENGTH_SHORT).show();
			URL url;
			try {
				url = new URL(TAGSELFIE_URL + "?access_token=" + getSharedPreferences(SHARED, Context.MODE_PRIVATE).getString(API_ACCESS_TOKEN, null));
				new GetInstagramImagesAsync(MainActivity.this).execute(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}else{
			todTextView.setText("Connect your IG to see photo here");
			todTextView.setTextColor(Color.RED);
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
		todTitleTextView = (TextView)findViewById(R.id.todTitleTextView);
		todTitleTextView.setText("Tweet Of The Day");

		TweetsAdapter adapterForTweets = new TweetsAdapter(getApplicationContext(), new ArrayList<Status>(tweets.subList(3,5)));
		followingListView.setAdapter(adapterForTweets);
		adapterForTweets.notifyDataSetChanged();

		followingListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				Status tweet = (Status)followingListView.getItemAtPosition(position);
				Intent i = new Intent(Intent.ACTION_VIEW);
				String tweetUrl = "https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + String.valueOf(tweet.getId());
				i.setData(Uri.parse(tweetUrl));
				startActivity(i);
			}
		});
	}

	public void goToSettings(View v){
		todTextView.setVisibility(View.INVISIBLE);
		startActivity(new Intent(MainActivity.this, SettingsActivity.class));
	}

	public void setImageUrl(String url){
		todImageView.setVisibility(View.VISIBLE);
		todTextView.setVisibility(View.VISIBLE);
		Picasso.with(getApplicationContext()).load(url).into(todImageView);
		todTextView.setText("(No caption)");

		tweetOfTheDayLayout.setVisibility(View.VISIBLE);
	}
}
