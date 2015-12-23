package qiminl.lifaryupdate;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Outline;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Layout;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.Calendar;


public class EditDiaryActivity extends Activity implements AdapterView.OnItemSelectedListener, MediaCommunication, View.OnClickListener {


    private static final String LOG_TAG = "AudioRecordTest";
    public static final int PRIVATE = 0;
    public static final int PUBLIC = 1;

    ImageView imgView;
    EditText dateEdit;
    Spinner shareSpinner;
    EditText textEdit;
    Button playButton;
    TextView locationView;
    EditButtonFragment frag;

    // delete buttons
    ImageButton delImg;
    ImageButton delAudio;
    ImageButton delLoc;

    // Diary attr
    String dateTime;
    int shareSelection;
    String diaryText;
    String audioFileName;
    MediaPlayer mPlayer = null;
    boolean mStartPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        actionbarSetting();

        initialize();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playButton:
                onPlay(mStartPlaying);
                mStartPlaying = !mStartPlaying;
                break;
            case R.id.deleteImage:
                // TODO: set image string to null
                deleteView(imgView, delImg);
                break;
            case R.id.deleteAudio:
                // TODO: set audio string to null
                deleteView(playButton, delAudio);
                break;
            case R.id.locDelete:
                // TODO: set location to null
                deleteView(locationView, delLoc);
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Edit", "Select: " + position );
        shareSelection = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // select private
    }

    @Override
    public void audioCom(String mFilename) {

        audioFileName = mFilename;
        displayView(playButton, delAudio);

        float audioDuration = 0;
        mStartPlaying = true;
        // set up duration of the audioFile
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioFileName);
            mPlayer.prepare();
            audioDuration = mPlayer.getDuration() / 1000;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Audio Com prepare() failed");
        }
        mPlayer.release();
        mPlayer = null;
        playButton.setText(audioDuration + "\"");

    }

    @Override
    public void imgCom(String imgFileName) {

    }


    /*************** Media Player Functions **********************/
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }




    /******** Delete Functions ***************/

    private void deleteView(View v, ImageButton imgBut){
        v.setVisibility(View.GONE);
        imgBut.setVisibility(View.GONE);
    }

    /******** Display Functions ***************/

    private void displayView(View v, ImageButton imgBut){
        v.setVisibility(View.VISIBLE);
        imgBut.setVisibility(View.VISIBLE);
    }

    /*********** Setting Action Bar *****************/
    private void actionbarSetting(){
        ActionBar actionBar = getActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.main_actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                //Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_HORIZONTAL);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.titleText);
        textviewTitle.setText("New Diary");
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /*********** Initialize Views *****************/
    private void initialize(){

        // Instantiate
        // image view
        imgView = (ImageView) findViewById(R.id.imageView);
        imgView.setVisibility(View.GONE);
        delImg = (ImageButton) findViewById(R.id.deleteImage);
        delImg.setOnClickListener(this);
        delImg.setVisibility(View.GONE);

        // date text
        dateEdit = (EditText) findViewById(R.id.dateEdit);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String str = today.month + "-" + today.monthDay + "-"+today.year
                + " " + today.format("%k:%M:%S");
        dateTime = str;
        dateEdit.setText(dateTime);

        // share spinner
        shareSpinner = (Spinner) findViewById(R.id.shareSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.share_selection, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shareSpinner.setAdapter(adapter);
        shareSpinner.setOnItemSelectedListener(this);
        shareSpinner.setSelection(PRIVATE);   // select first one

        // edit text
        textEdit = (EditText) findViewById(R.id.editText);

        // play button
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        playButton.setVisibility(View.GONE);

        delAudio = (ImageButton) findViewById(R.id.deleteAudio);
        delAudio.setOnClickListener(this);
        delAudio.setVisibility(View.GONE);

        // location text
        locationView = (TextView) findViewById(R.id.locationText);
        locationView.setVisibility(View.GONE);

        delLoc = (ImageButton) findViewById(R.id.locDelete);
        delLoc.setOnClickListener(this);
        delLoc.setVisibility(View.GONE);

        // fragment
        frag = (EditButtonFragment) getFragmentManager().findFragmentById(R.id.buttonsFrag);



    }


}
