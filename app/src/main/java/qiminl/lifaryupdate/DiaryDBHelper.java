package qiminl.lifaryupdate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liuqimin on 2016-01-06.
 */
public class DiaryDBHelper extends SQLiteOpenHelper{


    private static final String DEBUG = "DiaryDB";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "diaryDB.db";
    private static final String TABLE_NAME = "diaries";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_SHARE = "share";
    public static final String COLUMN_IMAGE = "img";
    public static final String COLUMN_SOUND = "sound";
    public static final String COLUMN_DATE = "date";



    public DiaryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TEXT
                + " TEXT," + COLUMN_LATITUDE + " REAL, " + COLUMN_LONGITUDE
                +" REAL,"+ COLUMN_SHARE + " INTEGER," + COLUMN_DATE + " Text, "+ COLUMN_IMAGE+" BLOB,"+ COLUMN_SOUND + " BLOB " +")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addDiary(Diary diary){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, diary.getDate());
        values.put(COLUMN_TEXT, diary.getText());
        values.put(COLUMN_IMAGE, diary.getImgByte());
        values.put(COLUMN_SOUND, diary.getAudioByte());
        values.put(COLUMN_LATITUDE, diary.getLatitude());
        values.put(COLUMN_LONGITUDE, diary.getLongitude());
        values.put(COLUMN_SHARE, diary.getShare());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();

        Log.d(DEBUG, "Diary Database added");

        Log.d("fb", "=============DEBUG ============");
        // debug, get first diary
        Diary debugD;
        diary.print();
        debugD = findDiaryByTime(diary.getDate());
        if(debugD != null){
            debugD.print();
        }
        else{
            Log.d("fb", "no diary");
        }
    }

    public Diary findDiaryByID(int id){
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Diary diary = new Diary();

        if (cursor.moveToFirst()) {
            Log.d(DEBUG, "cursor success: ");
            cursor.moveToFirst();
            Log.d(DEBUG, "moves to first: ");
            diary.setId(Integer.parseInt(cursor.getString(0)));

            Log.d(DEBUG, "set id ");
            diary.setContents(cursor.getString(1));
            Log.d(DEBUG, "add contents ");
            diary.setLocation(cursor.getFloat(2), cursor.getFloat(3));
            Log.d(DEBUG, "add location ");
            diary.setShare(cursor.getInt(4));
            Log.d(DEBUG, "set share ");
            diary.setDate(cursor.getString(5));
            Log.d(DEBUG, "set date ");
            diary.setImageByByte(cursor.getBlob(6));

            Log.d(DEBUG, "add image: ");
            diary.setSoundByByte(cursor.getBlob(7));
            Log.d(DEBUG, "add audio");
            cursor.close();
            Log.d(DEBUG, "close");
        } else {
            Log.d(DEBUG, "no diary found");
            diary = null;
        }
        db.close();
        return diary;
    }

    public Diary findDiaryByTime(String da){
        Log.d(DEBUG, "diary date = " + da);
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = \"" + da + "\"";

        Log.d(DEBUG, "SQL db");
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(DEBUG, "SQL cursor");
        Cursor cursor = db.rawQuery(query, null);
        if(cursor == null){
            Log.d(DEBUG, "cursor = null");
        }
        Log.d(DEBUG, "SQL cursor done");
        Diary diary = new Diary();
        if ( cursor.moveToFirst()) {

            Log.d(DEBUG,"cursor success: ");
            cursor.moveToFirst();
            Log.d(DEBUG, "moves to first: ");
            diary.setId(Integer.parseInt(cursor.getString(0)));

            Log.d(DEBUG, "set id , id = " + diary.getId());
            diary.setContents(cursor.getString(1));
            Log.d(DEBUG, "add contents, contents =   " + diary.getText());
            diary.setLocation(cursor.getFloat(2), cursor.getFloat(3));
            Log.d(DEBUG, "add location ");
            diary.setShare(cursor.getInt(4));
            Log.d(DEBUG, "set share ");
            diary.setDate(cursor.getString(5));
            Log.d(DEBUG, "set date , date == " + diary.getDate());
            diary.setImageByByte(cursor.getBlob(6));
            diary.setSoundByByte(cursor.getBlob(7));
            cursor.close();
            Log.d(DEBUG, "cursor close");
        } else {
            diary = null;
            Log.d(DEBUG, "cannot find diary by time");
        }
        db.close();
        return diary;
    }

    public  boolean deleteDiary(int id){
        boolean result = false;
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Diary d= new Diary();

        if (cursor.moveToFirst()) {
            do {
                d.setId(Integer.parseInt(cursor.getString(0)));
                db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                        new String[]{String.valueOf(d.getId())});
                cursor.close();
                result = true;
            }while (cursor.moveToNext());
        }
        db.close();
        return result;
    }
}
