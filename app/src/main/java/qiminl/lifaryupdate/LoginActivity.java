package qiminl.lifaryupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;


public class LoginActivity extends Activity implements View.OnClickListener {
    EditText usernameEdit;
    EditText passwordEdit;
    Button loginButton;
    TextView signupLink;
    ReadJsonDataAsycTask ioData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupLink = (TextView) findViewById(R.id.singupLink);

        loginButton.setOnClickListener(this);
        signupLink.setOnClickListener(this);
        ioData = new ReadJsonDataAsycTask(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    public void onClick(View v) {
        if (v == loginButton){
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            User user = new User(username, password);
            ioData.getUser(user);
        }
        else if(v == signupLink){
            // go to sign up page
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
    }
}
