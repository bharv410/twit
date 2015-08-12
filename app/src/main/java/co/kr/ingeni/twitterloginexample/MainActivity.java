package co.kr.ingeni.twitterloginexample;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.kidgeniushq.adapters.TweetsAdapter;
import com.kidgeniushq.adapters.UsersAdapter;
import com.kidgeniushq.asynctasks.GetTweetsAsync;
import com.kidgeniushq.instagram.InstagramApp;
import com.kidgeniushq.services.InstaService;
import com.kidgeniushq.staticstuff.MainCentralData;
import com.kidgeniushq.twitter.TwitterLogin;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import android.widget.AdapterView.OnItemClickListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import twitter4j.Status;
import twitter4j.User;

public class MainActivity extends ListActivity {

	private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
	private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
	private final String callback_url = "http://phantom.com";
	private final String INSTYPREFIX_URL = "https://api.instagram.com/v1//users/";
	private final String INSTSUFIX_URLL = "/media/recent/?access_token=[ACCESS_TOKEN]";
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



		MainCentralData.loadAllIGPosts();
		MainCentralData.loadAllHNHHArticles();

		// initiate the listadapter
		ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
				R.layout.row_layout, R.id.listText, MainCentralData.allArticleSourcesUrls);

		// assign the list adapter
		setListAdapter(myAdapter);

		startService(new Intent(getApplicationContext(), InstaService.class));
	}

	// when an item of the list is clicked
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);

		String selectedItem = (String) getListView().getItemAtPosition(position);
		//String selectedItem = (String) getListAdapter().getItem(position);
		startWithFeed(selectedItem);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}

	public void startWithFeed(String url){
		Intent i = new Intent(this, RealImportantActivity.class);
		i.putExtra("url", url);
		startActivity(i );
	}

}
