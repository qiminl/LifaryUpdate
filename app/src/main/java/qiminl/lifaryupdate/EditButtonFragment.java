package qiminl.lifaryupdate;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.media.MediaRecorder;
import android.media.MediaPlayer;

import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditButtonFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    FloatingActionButton recordFab;
    FloatingActionButton cameraFab;
    FloatingActionButton locFab;

    FloatingActionButton sendFab;

    MediaRecorder mRecorder = null;

    boolean mStartRecording;

    public EditButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_button, container, false);
        recordFab = (FloatingActionButton) v.findViewById(R.id.recordFab);
        cameraFab = (FloatingActionButton) v.findViewById(R.id.cameraFab);
        locFab = (FloatingActionButton) v.findViewById(R.id.locationFab);
        sendFab = (FloatingActionButton) v.findViewById(R.id.sendFab);
        recordFab.setOnClickListener(this);
        cameraFab.setOnClickListener(this);
        locFab.setOnClickListener(this);
        sendFab.setOnClickListener(this);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        recordInitial();


        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recordFab:
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                break;
            case R.id.cameraFab:
                break;
            case R.id.locationFab:
                break;
            case R.id.sendFab:
                break;
        }
    }


    /************** Audio Recording Functions ****************************/

    private void onRecord(boolean start){
        if(start){
            startRecording();
            // turn the record button img to recording img
            recordFab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_notification_overlay));
        }
        else{
            stopRecording();
            Log.d(LOG_TAG, "stop recording");
            // turn the record button img to not recording img
            recordFab.setImageDrawable(getResources().getDrawable(R.drawable.rec96));
            //  send audio file to Edit Diary Activity
            Log.d(LOG_TAG, "stop recording");

            MediaCommunication mediaCom = (MediaCommunication) getActivity();
            mediaCom.audioCom(mFileName);
            Log.d(LOG_TAG, "stop recording");

        }

    }

    //    Call MediaRecorder.prepare() on the MediaRecorder instance.
    //    To start audio capture, call MediaRecorder.start().
    private void startRecording(){
        recordInitial();
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    //    To stop audio capture, call MediaRecorder.stop().
    //    When you are done with the MediaRecorder instance, call MediaRecorder.release() on it.
    //        Calling MediaRecorder.release() is always recommended to free the resource immediately.
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    //    Create a new instance of android.media.MediaRecorder.
    //    Set the audio source using MediaRecorder.setAudioSource(). You will probably want to
    //                use MediaRecorder.AudioSource.MIC.
    //    Set output file format using MediaRecorder.setOutputFormat().
    //    Set output file name using MediaRecorder.setOutputFile().
    //    Set the audio encoder using MediaRecorder.setAudioEncoder().
    private void recordInitial(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mStartRecording = true;
    }


}
