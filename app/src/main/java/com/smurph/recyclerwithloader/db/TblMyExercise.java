package com.smurph.recyclerwithloader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Created by Ben on 5/9/2016.
 * This is a second table added to the project.
 */
public class TblMyExercise implements BaseColumns {

    public static final String TABLE_NAME = "TblMyObject2";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" +
            MyProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String EXERCISE = "Exercise";
    public static final String DIFFICULTY = "Difficulty";
    public static final String DATE_AND_TIME = "DateAndTime";

    protected void createTable(@NonNull SQLiteDatabase db) { db.execSQL(createTbl); }

    private static final String createTbl = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
            + _ID +           " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EXERCISE +      " TEXT, "
            + DIFFICULTY +    " TEXT, "
            + DATE_AND_TIME + " NUMERIC);";

    public interface CursorAllObjects {
        Uri URI = BASE_CONTENT_URI;

        String[] PROJECTION = new String[] { _ID, EXERCISE, DIFFICULTY, DATE_AND_TIME };

        int Id = 0;
        int Exercise = 1;
        int Difficulty = 2;
        int DateTime = 3;
    }
}
