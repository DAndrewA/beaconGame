package andrewmmattb.beacongame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    Button buttonGo;
    EditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonGo = (Button)findViewById(R.id.buttonGo);
        usernameInput = (EditText)findViewById(R.id.usernameInput);

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
            }
        });
    }

    void joinGame(){
        String username = String.valueOf(usernameInput.getText());
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("username",username);
        startActivityForResult(intent,1);
    }
}
