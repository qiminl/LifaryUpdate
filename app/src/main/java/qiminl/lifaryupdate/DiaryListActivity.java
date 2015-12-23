package qiminl.lifaryupdate;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class DiaryListActivity extends Activity  implements View.OnClickListener {

    ImageButton editButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        ActionBarSetting();
        cutFAB();

        editButton = (ImageButton) findViewById(R.id.fab);
        editButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_diary_list, menu);
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

    private void ActionBarSetting() {
        // Action Bar Setting
        ActionBar mActionBar = getActionBar();
        mActionBar.setTitle("Lifary");
    }
    /*
    * Cut the floating Action Button(FAB) to oval
    * set ripping effect
    * */
    private void cutFAB(){
        View buttonView = findViewById(R.id.fab);
        buttonView.setOutlineProvider(new ViewOutlineProvider() {
           @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void getOutline(View view, Outline outline) {
               int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
               outline.setOval(0, 0, diameter, diameter);

            }
        });

        buttonView.setClipToOutline(true);
    }

    @Override
    public void onClick(View v) {
        if(v == editButton){
            // TODO: go to editDiary activity
            Intent i = new Intent(this, EditDiaryActivity.class);
            startActivity(i);
        }
    }
}
