package andrewmmattb.beacongame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

    // creating the objects
    Button buttonGo;
    EditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assigning the view objects values
        buttonGo = (Button)findViewById(R.id.buttonGo);
        usernameInput = (EditText)findViewById(R.id.usernameInput);

        // sets the button click function
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
            }
        });
    }

    // starts the game activity with the username and background colour passed through
    void joinGame(){
        String username = String.valueOf(usernameInput.getText());

        if(username.length() <= 0){
            Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("username",username);
            startActivityForResult(intent,1);
        }
    }
}
