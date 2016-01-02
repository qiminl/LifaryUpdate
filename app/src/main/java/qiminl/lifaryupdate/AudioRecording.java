package qiminl.lifaryupdate;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by liuqimin on 2015-12-23.
 */
public class AudioRecording {

    private static final int MIN_RECORD_TIME = 700;
    private static final int MAX_RECORD_TIME = 18000;
    private static final String LOG_TAG = "AudioRecordTest";

    ImageButton recordFab;
    Context context;
    long mStartTime;
    MediaRecorder mRecorder = null;
    ObtainDecibelThread mThread;

    private static String mFileName = null;


    public AudioRecording(Context con, ImageButton v){
        context = con;
        recordFab = v;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        recordInitial();

    }
    /************** Audio Recording Functions ****************************/

    public void onRecord(boolean start){
        if(start){
            startRecording();
            // turn the record button img to recording img
            recordFab.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_notification_overlay));
        }
        else{
            stopRecording();
            // turn the record button img to not recording img
            recordFab.setImageDrawable(context.getResources().getDrawable(R.drawable.rec96));

            //  send audio file to Edit Diary Activity only if the recorded time greater than min time
            long intervalTime = System.currentTimeMillis() - mStartTime;
            if (intervalTime >    MIN_RECORD_TIME) {
                MediaCommunication mediaCom = (MediaCommunication) context;
                mediaCom.audioCom(mFileName);
            }
            else{
                Toast.makeText(context, "Record Time Too Short", Toast.LENGTH_LONG).show();

            }

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
        mThread = new ObtainDecibelThread();
        mThread.start();
    }

    //    To stop audio capture, call MediaRecorder.stop().
    //    When you are done with the MediaRecorder instance, call MediaRecorder.release() on it.
    //        Calling MediaRecorder.release() is always recommended to free the resource immediately.
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if(mThread != null){
            mThread.exit();
            mThread = null;
        }
    }

    //    Create a new instance of android.media.MediaRecorder.
    //    Set the audio source using MediaRecorder.setAudioSource(). You will probably want to
    //                use MediaRecorder.AudioSource.MIC.
    //    Set output file format using MediaRecorder.setOutputFormat().
    //    Set output file name using MediaRecorder.setOutputFile().
    //    Set the audio encoder using MediaRecorder.setAudioEncoder().
    private void recordInitial(){
        mStartTime = System.currentTimeMillis();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    /******************************* inner class ****************************************/

    /*
    * The Thread is for checking recording time
    * TODO: If recording time exceed maximum quota, it should stop recording
    * */
    private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;
        public boolean getRunning(){
            return running;
        }
        public void exit() {
            running = false;

        }
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//TODO:
//                if (System.currentTimeMillis() - mStartTime >= MAX_RECORD_TIME) {
//                    // if exceed max record time
//                    // stop recording
//                    exit();
//
//                }
            }
        }
    }
}
