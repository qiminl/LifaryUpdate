package qiminl.lifaryupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SignUpActivity extends Activity implements View.OnClickListener{

    EditText usernameEdit;
    EditText passwordEdit;
    EditText repasswordEdit;
    Button signupButton;
    TextView loginLink;
    ReadJsonDataAsycTask ioData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getActionBar().hide();
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        repasswordEdit = (EditText) findViewById(R.id.rePasswordEdit);
        signupButton = (Button) findViewById(R.id.signupButton);
        loginLink = (TextView) findViewById(R.id.loginLink);

        signupButton.setOnClickListener(this);
        loginLink.setOnClickListener(this);
        ioData = new ReadJsonDataAsycTask(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
        if(v == signupButton){
            Log.d("signup", "signup button clicked");
            // get username and passwords
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            String rePassword = repasswordEdit.getText().toString();
            // if two password matches
            if(passwordMatch(password, rePassword)){
                User user = new User(username, password);
                ioData.postUser(user);
            }
            else{
                Toast.makeText(this, "please confirm your password", Toast.LENGTH_LONG).show();
            }
        }

        else if(v == loginLink){
            // go to login page
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
    /*
    *  Check if two passowrd matches
    *   @param  password1 and password2 are two passwords for compar
    * */
    private boolean passwordMatch(String password1, String passWord2){
        if(!password1.equals("") && password1.equals(passWord2)){
            return true;
        }
        return false;
    }


}
