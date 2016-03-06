package andrewmmattb.beacongame;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {

    String username;

    BeaconManager beaconManager;
    BeaconConsumer beaconConsumer;

    int score = 0;
    int prevScore = 0;
    private long _scoreUpdateDelay = 2000;

    TextView usernameTextView;
    TextView scoreTextView;

    WebView scoreboardDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        usernameTextView = (TextView)findViewById(R.id.textViewGameUsername);
        scoreTextView = (TextView)findViewById(R.id.textViewGameScore);
        scoreboardDisplay = (WebView)findViewById(R.id.webViewScoreboard);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        usernameTextView.setText(username);

        setUploader();

        // displays the live updating webpage for the scoreboard
        scoreboardDisplay.getSettings().setJavaScriptEnabled(true);
        scoreboardDisplay.loadUrl("http://ec2-54-187-69-193.us-west-2.compute.amazonaws.com/");

        // BEACON CODE
        beaconConsumer = new BeaconConsumer() {
            @Override
            public void onBeaconServiceConnect() {
            }
            @Override
            public Context getApplicationContext() {
                return GameActivity.this;
            }
            @Override
            public void unbindService(ServiceConnection serviceConnection) {
            }
            @Override
            public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
                return false;
            }
        };
        beaconConsumer.getApplicationContext();
        beaconManager = BeaconManager.getInstanceForApplication(GameActivity.this);
        beaconManager.bind(beaconConsumer);
        se
    }

    void makeSeverPost() throws IOException {
        // creates a map object with username and the additional points to the previous sent score
        Map<String,Object> values = new HashMap<String,Object>();
        values.put("username", username);
        values.put("newPoints",score-prevScore);
        // sets the previous score to equal the current score
        prevScore = score;

        try {
            new MakeSeverPostTask(values).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("problem", "" + e.getMessage());
        }
    }

    // starts a timer to update the server every 2 seconds
    void setUploader(){
        Timer uploadTimer = new Timer();
        TimerTask uploadTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    makeSeverPost();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(GameActivity.this,"Error uploading score to the server",Toast.LENGTH_SHORT).show();
                }
            }
        };
        uploadTimer.scheduleAtFixedRate(uploadTask,_scoreUpdateDelay,_scoreUpdateDelay);
    }

    // BEACONS
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i("BEACONS", "I SEE BEACON!!");
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            Log.i("BEACONS","Tried starting the scanning");
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.i("BEACONS","NOT QUITE WORKING JUST YET");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(beaconConsumer);
    }
}
