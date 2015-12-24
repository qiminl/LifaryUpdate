package qiminl.lifaryupdate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import java.util.HashMap;

/**
 * Created by Qimin Liu on 2015/12/17.
 */
public class ReadJsonDataAsycTask {
    User loadUser;
    Context context;
    public ReadJsonDataAsycTask(Context context){
        this.context = context;
    }

    public void postUser(User user){
        loadUser = user;
        new PostUserOnline().execute(context.getString(R.string.REGISTER));
    }
    public void getUser(User user){
        loadUser = user;
        new GetUserOnline().execute(context.getString(R.string.LOGIN));
    }


    /*
    *  register a user online
     *  return: the unique id of the user if success, otherwise return a null
    * */
    private class UserOnline extends AsyncTask<String, Void, String>{
        private HashMap <String, String> map = new HashMap<>();
        @Override
        protected String doInBackground(String... url) {
            try {
                ConnectionWithPost con = new ConnectionWithPost();
                // put everything into the map
                map.put("email", loadUser.getUsername());
                map.put("name", "name");
                map.put("password", loadUser.getPassword());
                // upload the map to online
                Message msg = con.uploadDiary(map, url[0], true);

                if(msg.getSuccess() == "1"){
                    return msg.getMessage();
                }
                else
                    return null;

            }catch (Exception e){
                e.printStackTrace();
                Log.d("http", "CONNECTION ERROR: " + "\t" + e.getLocalizedMessage());
            }

            return null;
        }

    }

    private class PostUserOnline extends UserOnline {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null){ // successful created the account
                Log.d("Asynctask", "unique id = " + result);
                // save user locally
                User user = new User(result, loadUser.getUsername(), loadUser.getPassword());
                saveUserLocally(user);
                // TODO: go to main page
                Intent mIntent = new Intent(context, DiaryListActivity.class);
                context.startActivity(mIntent);
            }
            else{
                Toast.makeText(context, "Account is already exists", Toast.LENGTH_LONG).show();
            }
            Log.d("Asynctask", "finished");
        }
    }
    /*
    *  Login a user
     *  return: the unique id of the user if success, otherwise return a null
    * */
    private class GetUserOnline extends UserOnline{
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null){ // successful created the account
                Log.d("Asynctask", "unique id = " + result);
                // save user locally
                User user = new User(result, loadUser.getUsername(), loadUser.getPassword());
                saveUserLocally(user);
                // TODO: go to main page
                Intent mIntent = new Intent(context, DiaryListActivity.class);
                context.startActivity(mIntent);
            }
            else{
                Toast.makeText(context, "please confirm your username or password3", Toast.LENGTH_LONG).show();
            }
            Log.d("Asynctask", "finished");
        }
    }


    /*  Helper Class
    * Save the user loccaly by SharedPreference
    * Only one user canbe save in local
    * */
    private void saveUserLocally(User user){
        String username = user.getUsername();
        String password = user.getPassword();
        String unique = user.getUnique();
        SharedPreferences sharedPref = context.getSharedPreferences
                (context.getString(R.string.user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.username), username);
        editor.putString(context.getString(R.string.password), password);
        editor.putString(context.getString(R.string.unique), unique);
        editor.commit();
    }
}
