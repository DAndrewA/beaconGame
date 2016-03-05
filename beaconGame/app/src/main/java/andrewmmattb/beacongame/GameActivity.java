package andrewmmattb.beacongame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

public class GameActivity extends Activity {

    String username;
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
    }
}
