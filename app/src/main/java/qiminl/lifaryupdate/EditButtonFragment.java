package qiminl.lifaryupdate;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditButtonFragment extends Fragment implements View.OnClickListener {


    public static final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
    private static final int TAKE_PHOTO = 0;
    private static final int SELECT_IMAGE = 1;
    private static final int CANCEL = 2;
    private static final String DEBUG = "CameraDebug";

    public final static int RESULT_TAKE_PHOTO = 1;
    public final static int RESULT_LOAD_IMG = 2;


    FloatingActionButton recordFab;
    FloatingActionButton cameraFab;
    FloatingActionButton locFab;

    FloatingActionButton sendFab;


    boolean mStartRecording;

    AudioRecording audioRec;

    String imgDecodableString;
    String camPhotoPath;


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

        audioRec = new AudioRecording(getActivity(), recordFab);
        mStartRecording = true;


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
                audioRec.onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                break;
            case R.id.cameraFab:
                cameraDialog();
                break;
            case R.id.locationFab:
                LocationData locData = new LocationData(getActivity());
                // read location name by geoname
                locData.getLocNameByGeoname();
                // TODO: show location from google map
                break;
            case R.id.sendFab:
                // get back to activity
                MediaCommunication mediaCom = (MediaCommunication) getActivity();
                mediaCom.submitCom();
                break;
        }
    }

    /*
    * POP UP A dialog about whether take photo or select image from gallary
    * */

    private void cameraDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case TAKE_PHOTO: // take phote

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                        camPhotoPath  = f.getPath();
                        Log.d(DEBUG, "take photo path = " + camPhotoPath);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, RESULT_TAKE_PHOTO);
                        break;
                    case SELECT_IMAGE: // choose image from gallery

                        Intent imgIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(imgIntent, RESULT_LOAD_IMG);
                        break;
                    case CANCEL: // cancel
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{

            do{
                if(resultCode != getActivity().RESULT_OK){
                    Log.d(DEBUG, "resultCode = " + resultCode + "\tresultOK = " + getActivity().RESULT_OK);
                    break;
                }

                if(requestCode == RESULT_TAKE_PHOTO){
                    Log.d(DEBUG, "take photo requested code = " + requestCode);
                    takePhote();
                    break;
                }
                if(data == null){
                    Log.d(DEBUG, "data = null");
                    break;
                }
                if(requestCode == RESULT_LOAD_IMG){
                    Log.d(DEBUG, "load image requested code = " + requestCode);
                    loadImg(data);
                    break;
                }
                break;
            }while(true);

        }catch (Exception e){
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Load img from data to the activiy
    * @param: data: returned data from Gallery
    * */
    private void loadImg(Intent data){
        //
        Uri selectedImg = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = getActivity().getContentResolver().query(selectedImg,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        // get the img string
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imgDecodableString = cursor.getString(columnIndex);
        Log.d(DEBUG, "imDecodableString = " + imgDecodableString);
        cursor.close();

        //  pass the img string to the activity
        MediaCommunication mediaCom = (MediaCommunication) getActivity();

        mediaCom.imgCom(imgDecodableString);

    }

    private void takePhote(){

        Log.d(DEBUG, "take photo result paht = " + camPhotoPath);
        // send the path to edit diary activity
        MediaCommunication mediaCom = (MediaCommunication) getActivity();
        mediaCom.imgCom(camPhotoPath);
    }




}
