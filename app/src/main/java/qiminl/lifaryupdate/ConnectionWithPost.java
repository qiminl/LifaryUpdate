package qiminl.lifaryupdate;

import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handle communication with target server using HTTP connection
 *      1. upload single Diary object to server
 *      2. upload file to target server
 *      3. download Diary/Diaries of user
 *
 * Created by gsn  on 2015-11-11.
 */
public class ConnectionWithPost {
    int serverResponseCode = 0;
    //todo static the link
    public final static String DEBUG_HTTP = "http";
    private final String UPLOAD_FILE_URL = "http://192.168.1.71:8080/wala/UploadToServer.php";
    private final String UPLOAD_DIARY_URL = "http://192.168.1.71:8080/wala/upload_diary.php";
    private final String DOWNLOAD_ALL_DIARIES_URL = "http://192.168.1.71:8080/wala/get_all_diaries.php";
    private final String DOWNLOAD_USER_DIARIES_URL="http://192.168.1.71:8080/wala/get_diary.php";
    private String static_url = "error";

    /**********  File Path *************/
    //todo change file path
    //final String uploadFilePath = "/mnt/sdcard/";
    private final String uploadFilePath = "C:\\Users\\liuqi\\Desktop\\";
    private final String uploadFileName = "2.png";

    /**********  Responds *************/
    private HttpURLConnection conn;
    qiminl.lifaryupdate.Message object = null;

    /**
     * This method is used to upload single Diary object to server
     *      by sending HTTP POST request to target php script
     *      with Diary data as url parameter.
     * It also returns the respond message from server.
     *
     * @param values This is a HashMap<String, String> array contains data to be sent;
     *               mostly json objects
     * @return String This returns the responds from server
     */
    public String uploadDiary( HashMap<String, String> values, String urlString) {
        Log.d(DEBUG_HTTP, "using uploadDiary");
        try {
            //todo static url changed to login
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            Log.d(DEBUG_HTTP, "connection connect made");
            if (values != null) {
                Log.d(DEBUG_HTTP, "value not empty");
                OutputStream os = conn.getOutputStream();
                //todo handle php server error
                OutputStreamWriter osWriter = new OutputStreamWriter(os,
                        "UTF-8");
                BufferedWriter writer = new BufferedWriter(osWriter);
                writer.write(getPostData(values));

                System.out.println("writer post data: " +getPostData(values));
                writer.flush();
                writer.close();
                os.close();
            }

            Log.d(DEBUG_HTTP, "response code: " + Integer.toString(conn.getResponseCode()));
            //Log.d(DEBUG_HTTP, "response message: : " + conn.getResponseMessage());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("connect http ok");
                Log.d(DEBUG_HTTP, "connection http ok");
                InputStream is = conn.getInputStream();
                InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isReader);

                String result = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                    System.out.println("Result: " + result);
                }

                if (result.trim().length() > 2) {
                    Gson gson = new Gson();
                    try{
                        object = gson.fromJson(result, qiminl.lifaryupdate.Message.class);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d(DEBUG_HTTP, "json error: " + "\t" + e.getLocalizedMessage());
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(DEBUG_HTTP, "MalformedURLException: " + "\t" + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_HTTP, "IOException: " + "\t" + e.getLocalizedMessage());
        }

        if (object.toString().isEmpty())
            return "NO RESPONSE";
        else
            return object.toString();
    }

    /**
     * This method convert HashMap data to be sent into url parameter structure.
     *
     * @param values This is a HashMap<String, String> stores data using in POST request
     * @return String This is a String contains the parameter of url POST request
     */
    private String getPostData(HashMap<String, String> values) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (first)
                first = false;
            else
                builder.append("&");

            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }

        }
        return builder.toString();
    }

    /**
     * This is a method that uploads file to target server location.
     *
     * @param sourceFileUri This is the absolute path of the file that need to be uploaded
     * @return Strng This is static url
     */
    public String uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        /********** Test for file existence *********/
        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);
            return "error";
        }
        else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_FILE_URL);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
                        + fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necessary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                if (serverResponseCode == 200) {
                    String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                            +uploadFileName;
                    System.out.println("filepath: " + serverResponseMessage);

                    InputStream is = conn.getInputStream();
                    InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
                    BufferedReader reader = new BufferedReader(isReader);

                    String result = "";
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                    //System.out.println("result: " +result);
                    if (result.trim().length() > 2) {
                        Gson gson = new Gson();
                        object = gson.fromJson(result, qiminl.lifaryupdate.Message.class);
                        //System.out.println("object message:" + object.getMessage());
                    }
                    if (object.getSuccess().equals("1"))
                        static_url = object.getMessage();
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return static_url;
    }

    /**
     * This method get all diaries of the user
     * todo use only for debug purpose, should remove
     * @return Diaries a class contains arrays of Diary objects
     * @throws IOException
     */
//    public Diaries getAllDiaries() throws IOException {
//        //string that read in server response;
//        String tmp = "";
//        /*
//        String jsonStr = "{\"phonetype\":\"N95\",\"cat\":\"WP\"}";
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonStr);
//        System.out.println(json);
//        */
//        //URL url = new URL("http://192.168.1.71:8080/wala/get_all_products.php");
//        URL url = new URL(DOWNLOAD_ALL_DIARIES_URL);
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        try {
//            if (urlConnection.getResponseCode() ==urlConnection.HTTP_OK)
//                System.out.println(urlConnection.getResponseMessage());
//
//            DataInputStream dis =new DataInputStream(urlConnection.getInputStream());
//            StringBuffer inputLine = new StringBuffer();
//
//            while ((tmp = dis.readLine()) != null) {
//                inputLine.append(tmp);
//                //System.out.println(tmp);
//            }
//            String rs = inputLine.toString();
//
//            /*
//             *Using Gson builder with deserializer
//             */
//            final GsonBuilder gsonBuilder = new GsonBuilder();
//            //gsonBuilder.registerTypeAdapter(Product.class, new ProductDeserializer());
//            //gsonBuilder.registerTypeAdapter(Products.class, new ProductsDeserializer());
//
//            gsonBuilder.registerTypeAdapter(Diary.class, new DiaryDeserializer());
//            gsonBuilder.registerTypeAdapter(Diaries.class, new DiariesDeserializer());
//            final Gson gson = gsonBuilder.create();
//            //final Products products = gson.fromJson(rs, Products.class);
//            final Diaries diaries = gson.fromJson(rs, Diaries.class);
//            //System.out.println("Convert json string into object: \n" + diaries);
//            String temp_p = gson.toJson(diaries);
//            //System.out.println("Convert object into json format: \n" + temp_p);
//
//            return diaries;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * This method gets all diary under same user
     *
     * @param user_id This is the unique id of a user
     * @return Diaries This is an object contains list of Diary
     * @throws IOException
     */

//
//    public Diaries getUserDiaries(String user_id) throws IOException {
//        //string that read in server response;
//        String tmp = "";
//        /*
//        String jsonStr = "{\"phonetype\":\"N95\",\"cat\":\"WP\"}";
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonStr);
//        System.out.println(json);
//        */
//        //URL url = new URL("http://192.168.1.71:8080/wala/get_all_products.php");
//        URL url = new URL(DOWNLOAD_USER_DIARIES_URL);
//        conn = (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(10000);
//        conn.setConnectTimeout(15000);
//        conn.setRequestMethod("POST");
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//        conn.connect();
//        try {
//
//            /******* send parameter**/
//            OutputStream os = conn.getOutputStream();
//            //todo handle php server error
//            OutputStreamWriter osWriter = new OutputStreamWriter(os,
//                        "UTF-8");
//            BufferedWriter writer = new BufferedWriter(osWriter);
//            writer.write("userid=" + user_id);
//
//            //System.out.println("writer: " +getPostData(values));
//            writer.flush();
//            writer.close();
//            os.close();
//            if (conn.getResponseCode() ==conn.HTTP_OK){
//                System.out.println(conn.getResponseMessage());
//                /******* Read server respond data *****/
//                DataInputStream dis =new DataInputStream(conn.getInputStream());
//                StringBuffer inputLine = new StringBuffer();
//
//                while ((tmp = dis.readLine()) != null) {
//                    inputLine.append(tmp);
//                    System.out.println(tmp);
//                }
//                String rs = inputLine.toString();
//
//                /*
//                 *Using Gson builder with deserializer
//                 */
//                final GsonBuilder gsonBuilder = new GsonBuilder();
//                //gsonBuilder.registerTypeAdapter(Product.class, new ProductDeserializer());
//                //gsonBuilder.registerTypeAdapter(Products.class, new ProductsDeserializer());
//
//                gsonBuilder.registerTypeAdapter(Diary.class, new DiaryDeserializer());
//                gsonBuilder.registerTypeAdapter(Diaries.class, new DiariesDeserializer());
//                final Gson gson = gsonBuilder.create();
//                //final Products products = gson.fromJson(rs, Products.class);
//                final Diaries diaries = gson.fromJson(rs, Diaries.class);
//                //System.out.println("Convert json string into object: \n" + diaries);
//                String temp_p = gson.toJson(diaries);
//                //System.out.println("Convert object into json format: \n" + temp_p);
//
//                return diaries;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     *Class Message is created to handle php response message
     *      contains structure:
     *              [success]: 1 & 0 indicate success or fail
     *              [message]: String of message

    private class Message {
        private String message = "";
        private String success = "";
        Message(){}
        public String getMessage(){
            return message;
        }
        public String getSuccess(){
            return success;
        }
        public String toString(){
            String result = "{\"success\":"+success+",\"message\":\""+message+"\"}";
            return result;
        }
    }
     */

    /**
     * This method is used to upload single Diary object to server
     *      by sending HTTP POST request to target php script
     *      with Diary data as url parameter.
     * It also returns the respond message from server.
     *
     * @param values This is a HashMap<String, String> array contains data to be sent;
     *               mostly json objects
     * @return Message This returns the responds from server as Message object
     */
    public qiminl.lifaryupdate.Message uploadDiary( HashMap<String, String> values, String urlString, boolean flag) {
        Log.d(DEBUG_HTTP, "using uploadDiary");
        try {
            //todo static url changed to login
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            Log.d(DEBUG_HTTP, "connection connect made");
            if (values != null) {
                Log.d(DEBUG_HTTP, "value not empty");
                OutputStream os = conn.getOutputStream();
                //todo handle php server error
                OutputStreamWriter osWriter = new OutputStreamWriter(os,
                        "UTF-8");
                BufferedWriter writer = new BufferedWriter(osWriter);
                writer.write(getPostData(values));

                System.out.println("writer post data: " +getPostData(values));
                writer.flush();
                writer.close();
                os.close();
            }

            Log.d(DEBUG_HTTP, "response code: " + Integer.toString(conn.getResponseCode()));
            //Log.d(DEBUG_HTTP, "response message: : " + conn.getResponseMessage());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("connect http ok");
                Log.d(DEBUG_HTTP, "connection http ok");
                InputStream is = conn.getInputStream();
                InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isReader);

                String result = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                    System.out.println("Result: " + result);
                }

                if (result.trim().length() > 2) {
                    Gson gson = new Gson();
                    try{
                        object = gson.fromJson(result, qiminl.lifaryupdate.Message.class);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d(DEBUG_HTTP, "json error: " + "\t" + e.getLocalizedMessage());
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(DEBUG_HTTP, "MalformedURLException: " + "\t" + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_HTTP, "IOException: " + "\t" + e.getLocalizedMessage());
        }

        Log.d(DEBUG_HTTP, "responds" + object.toString());
        if (object.toString().isEmpty())
            return null;
        else
            return object;
    }


}
