package qiminl.lifaryupdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isUserExist()){
            Intent i = new Intent(this, DiaryListActivity.class);
            setContentView(R.layout.activity_launcher);
            startActivity(i);
        }
        else{
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
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

    /*
    * Helper Function
    * check if User exists in local data
    * */

    private boolean isUserExist(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user), Context.MODE_PRIVATE);
        String defaulValue = "";
        String unique = sharedPref.getString("unique", defaulValue);
        if(unique == defaulValue){
            return false;
        }
        return true;
    }
}
