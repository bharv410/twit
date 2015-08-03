package com.kidgeniushq;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import co.kr.ingeni.twitterloginexample.R;


public class NotifToParseActivity extends Activity {

    String postVia, poster, postText;
    boolean isArticle;
    TextView savedToParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_to_parse);
        savedToParse = (TextView)findViewById(R.id.textviewbandouplaodtoparse);
        savedToParse.setVisibility(View.GONE);

        if(getIntent().getStringExtra("postVia")!=null)
            postVia = getIntent().getStringExtra("postVia");

        if(getIntent().getStringExtra("poster")!=null)
            poster = getIntent().getStringExtra("poster");

        if(getIntent().getStringExtra("postText")!=null)
            postText = getIntent().getStringExtra("postText");

        ParseObject testObject = new ParseObject("PostsBeta");
        testObject.put("postVia", postVia);
        testObject.put("poster", poster);
        testObject.put("postText", postText);
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                savedToParse.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notif_to_parse, menu);
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
}
