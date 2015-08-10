package co.kr.ingeni.twitterloginexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

public class VerifyPostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_post);

        //grid.setBackgroundColor(Color.parseColor("#999999"));
        TextView textView = (TextView) findViewById(R.id.grid_text);
        TextView dateTextView = (TextView) findViewById(R.id.textViewDate);
        dateTextView.setText("Right Now");
        TextView socialTextView = (TextView) findViewById(R.id.socialTextView);
        ImageView imageView = (ImageView)findViewById(R.id.grid_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getIntent().getStringExtra("url");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        final String siteType = "article";
        if(!siteType.contains("article"))
            socialTextView.setText(siteType);

        textView.setText(getIntent().getStringExtra("caption"));

        Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("url"))
                    .placeholder(R.drawable.progress_animation)
                    .into(imageView);

        Log.v("benmark", "linkkkk   " + getIntent().getStringExtra("link"));
        Log.v("benmark", "titleeee " + getIntent().getStringExtra("caption"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void postNow(View v) {
        // does something very interesting
        ParseObject gameScore = new ParseObject("VerifiedBandoPost");
        gameScore.put("postLink", getIntent().getStringExtra("link"));
        gameScore.put("postText", getIntent().getStringExtra("caption"));
        gameScore.put("imageUrl", getIntent().getStringExtra("url"));
        gameScore.put("viewCount", 0);
        gameScore.put("siteType", "article");
        gameScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "posted " + getIntent().getStringExtra("caption"), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void postToTopNow(View v) {
        // does something very interesting
        ParseObject gameScore = new ParseObject("BandoFeaturedPost");
        gameScore.put("postLink", getIntent().getStringExtra("link"));
        gameScore.put("text", getIntent().getStringExtra("caption"));
        gameScore.put("imageUrl", getIntent().getStringExtra("url"));
        gameScore.put("viewCount", 0);
        gameScore.put("siteType", "article");
        gameScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "posted " + getIntent().getStringExtra("caption"), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
