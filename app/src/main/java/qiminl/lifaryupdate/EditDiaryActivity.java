package qiminl.lifaryupdate;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
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
import java.util.List;


public class EditDiaryActivity extends Activity implements AdapterView.OnItemSelectedListener, MediaCommunication, View.OnClickListener, View.OnLongClickListener {


    private static final String LOG_TAG = "AudioRecordTest";
    public static final int PRIVATE = 0;
    public static final int PUBLIC = 1;
    private static final String[] delDialogItems = {"delete", "cancel"};
    private  static final int DELETE = 0;
    private static final int CANCEL = 1;
    ImageView imgView;
    EditText dateEdit;
    Spinner shareSpinner;
    EditText textEdit;
    Button playButton;
    TextView locationView;
    EditButtonFragment frag;


    // Diary attr
    Diary currentDiary;
    String dateTime;
    int shareSelection;
    String diaryText;
    String audioFileName;
    MediaPlayer mPlayer = null;
    boolean mStartPlaying = true;

    private int dialogSelection = -1;

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
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if (v == imgView || v == playButton || v == locationView){
            delDialog("Delete?", delDialogItems, v);
            return true;
        }

        return false;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Edit", "Select: " + position );
        shareSelection = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // select private
        shareSelection = PRIVATE;
    }

    @Override
    public void audioCom(String mFilename) {

        audioFileName = mFilename;
        // display play button
        playButton.setVisibility(View.VISIBLE);

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
        imgView.setOnLongClickListener(this);
        imgView.setVisibility(View.GONE);

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
        playButton.setOnLongClickListener(this);
        playButton.setVisibility(View.GONE);


        // location text
        locationView = (TextView) findViewById(R.id.locationText);
        playButton.setOnLongClickListener(this);
        locationView.setVisibility(View.GONE);

        // fragment
        frag = (EditButtonFragment) getFragmentManager().findFragmentById(R.id.buttonsFrag);



    }

    /*********************  pop up dialog  *****************************/
    /*
    *   Pop up a  dialog which can select among a list of items
    *   @param: title: the title of the dialog
    *   @param items: the items listed in the list view dialog
    *   @param view: the parent view that generates the dialog
    *   @return which item is selected
    * */
    private void delDialog(String title, String[] items, final View parent){
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DELETE) {
                    // image view
                    if (parent == imgView) {
                        imgView.setVisibility(View.GONE);
                        // TODO: set image string to null
                    }
                    // play button
                    else if (parent == playButton) {
                        playButton.setVisibility(View.GONE);
                        // TODO: set image string to null

                    }
                    // location View
                    else {
                        locationView.setVisibility(View.GONE);
                        // TODO: set location to null

                    }
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
