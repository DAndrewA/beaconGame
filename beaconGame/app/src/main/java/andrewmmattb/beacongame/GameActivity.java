package andrewmmattb.beacongame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
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
    String serverPath = "http://ec2-54-187-69-193.us-west-2.compute.amazonaws.com/";

    int score = 0;
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

        // writes the map into a string in JSON format
        String jsonString = new JSONObject(values).toString();

        try {
            URL url = new URL(serverPath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setChunkedStreamingMode(0);

            OutputStreamWriter postRequest = new OutputStreamWriter(connection.getOutputStream());
            postRequest.write(jsonString);
            postRequest.close();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED){
                Toast.makeText(this,"RESPONSE CODE DOESNT LIKE IT?",Toast.LENGTH_SHORT).show();
            }
            //connection.disconnect();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
