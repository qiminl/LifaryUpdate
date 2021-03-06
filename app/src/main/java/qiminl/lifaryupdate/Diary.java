package qiminl.lifaryupdate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * This is a class operates on Diary
 * Created by liuqimin on 15-07-07.
 */
public class Diary {

    private double id;
    private String date;
    private String text;
    private float latitude, longitude;
    private int share;
    private String image;
    private String imageurl;
    private String sound;
    private String userid;

    //todo modify init stat - create a method to full init with content
    public Diary(){
        // get date and time
        Calendar c= Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = month + "-" + day + "-" + year + "  " +
                hour + ":" + minute + ":" + seconds;

        image = "";
        imageurl = "";
        sound = "";
        text = "";
        latitude = 0;
        longitude = 0;
        share = 0;
        userid = "";

    }

    // public setters
    public void setContents(String contents){
        text = contents;
    }

    public void setLocation(float lat, float lon){
        latitude = lat;
        longitude = lon;
    }

    //  set sound functions \
    public void setSound(String sound){
        this.sound = sound;
    }
    public boolean setSoundByFileName(String mFileName){
        boolean result = false;
        try{
            FileInputStream is = new FileInputStream(mFileName);
            Log.d("Lifary", "Method 2, fileinputstream");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Log.d("Lifary", "Byte array output stream");
            byte[] b = new byte[1024];
            Log.d("Lifary", "new byte b");
            int bytesRead = is.read(b);
            Log.d("Lifary", "read byte");
            while (bytesRead != -1) {
                bos.write(b, 0, bytesRead);
                bytesRead = is.read(b);
            }
            Log.d("Lifary", "Read byte done");
            byte[] audioByte = bos.toByteArray();
            Log.d("Lifary", "convert to byte array");
            Log.d("Lifary", "Edit Diary: Audio byte = " + Arrays.toString(audioByte));
            setSoundByByte(audioByte);
            Log.d("Lifary", "add Sound");
            result = true;
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    public void setSoundByByte(byte[] audioByte){
        if(audioByte != null) {
            sound = Base64.encodeToString(audioByte, Base64.DEFAULT);
        }
        //  Log.d("Lifary", "sound.size = " + sound.length);
    }

    // set image functions

    public void setImage(String image){
        this.image = image;
    }
    public void setImageByBitmap(Bitmap bmp){

        byte[] img = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        img = os.toByteArray();
        if(img == null)
            image = "";
        else    image = Base64.encodeToString(img, Base64.DEFAULT);
        //Log.d("Lifary", "Diary: img == " + Arrays.toString(img));
    }

    public void setImageByByte(byte[] imgByte){
        if(imgByte != null)
           image = Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public boolean setImageByFileName(String mFileName){
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(mFileName);
            bitmap = compressImgBitmap(bitmap, 512);
            setImageByBitmap(bitmap);
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void setImageByCamera(){
        File f = new File(Environment)
    }

    public void setImageUrl(String url){
        imageurl = url;
    }


    // set share
    public void setShare(int s){
        share = s;
    }

    // set date functions
    public void setDate(String date){this.date = date;}
    public void setDate(){
        Calendar c= Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = month + "-" + day + "-" + year + "  " +
                hour + ":" + minute + ":" + seconds;
    }


    public void setId(double i){this.id = i;}

    // set user id
    public void setUserid(String diaryid){this.userid = diaryid;}

    // --------- public getters ---------------------
    public double getId(){ return id;}
    public String getDate(){return date;}

    // get location functions
    public float getLatitude(){return latitude;}
    public float getLongitude(){return longitude;}
    // get share
    public int getShare(){return share;}

    // get image functions
    public byte[] getImgByte(){
            byte[] img = null;
            img = Base64.decode(image,Base64.DEFAULT);
        return img;
    }

    public Bitmap getImgBitmap(){
        byte[] img = getImgByte();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

        return bitmap;
    }

    public String getImageUrl(){ return imageurl;}
    public String getImage(){return image;}

    // get sound functions
    public byte[] getAudioByte(){
        byte[] soundByte = null;
        soundByte = Base64.decode(sound,Base64.DEFAULT);
        return soundByte;
    }
    public String getSound(){return  sound;}

    // get user id
    public String getUserid(){return userid;}
    // get content
    public String getText(){
        return text;
    }

    // ------------------delete Functions---------------------------------
    public void deleteImge(){
        image = "";
    }
    public void deleteSound(){
        sound = "";
    }
    public void deleteLoc(){
        latitude = 0;
        longitude = 0;
    }

    // for debug
    public void print() {
        //Log.d("fb", "pringting diar:");
        Log.d("fb", "image: " + image);
        Log.d("fb", "id" + id);
        Log.d("fb", "date" + date);
        Log.d("fb", "text" + text);
        Log.d("fb", "lat" + latitude + " long:" + longitude);
        Log.d("fb", "share" + share);
        //private byte[]img;
        //private byte[] sound;
        Log.d("fb", "sound" + sound);
        System.out.println("id:" + id);
        System.out.println("imageurl:" + imageurl);
    }



    public HashMap<String, String> toHashMap(){
        HashMap<String, String> map = new HashMap<>();

        Log.d("fb","hashmap");
        map.put("id", Double.toString(this.id));
        map.put("date", this.date);
        map.put("text", this.text);
        map.put("latitude", Float.toString(this.latitude));
        map.put("longitude", Float.toString(this.longitude));
        map.put("share", Integer.toString(this.share));
        map.put("image", this.image);
        map.put("imageurl", this.imageurl);
        map.put("sound", this.sound);
        map.put("userid", this.userid);
        Log.d("fb", "hash done");
        return map;
    }

    /*********************** Helper Functions **************************/
    private Bitmap compressImgBitmap(Bitmap bitmap, float factor){
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        Bitmap result = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        return result;
    }

}
