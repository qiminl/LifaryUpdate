package qiminl.lifaryupdate;

/**
 * Created by User on 2015/12/22.
 */
public interface MediaCommunication {

    void audioCom(String mFilename);
     void  imgCom(String imgFile);
    void locCom(double lat, double lon, String locName);
    void submitCom();
}
