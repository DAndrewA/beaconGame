package andrewmmattb.beacongame;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Base64;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameActivity extends Activity {

    String username;
    String serverPath = "ec2-54-187-69-193.us-west-2.compute.amazonaws.com";

    int score = -10;
    int prevScore = 0;

    TextView usernameTextView;
    TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        usernameTextView = (TextView)findViewById(R.id.textViewGameUsername);
        scoreTextView = (TextView)findViewById(R.id.textViewGameScore);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        usernameTextView.setText(username);

        try {
            makeSeverPost();
        }
        catch (IOException e) {
            Toast.makeText(GameActivity.this,"There was an IO error, called after function call (line 56)",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void makeSeverPost() throws IOException {
        // creates a map object with username and the additional points to the previous sent score
        Map<String,Object> values = new HashMap<String,Object>();
        values.put("username",username);
        values.put("newPoints",score-prevScore);
        // sets the previous score to equal the current score
        prevScore = score;

        try {
            new MakeSeverPostTask(values).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("problem",""+e.getMessage());
        }
    }

    
}
