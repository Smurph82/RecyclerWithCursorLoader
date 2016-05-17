package com.smurph.recyclerwithloader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ben on 5/10/16.
 * This is an example of a one to many SQLite table
 */
public class TblObjectToExercise implements BaseColumns {

    public static final String TABLE_NAME = "TblObjectToExercise";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" +
            MyProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String OBJECT_ID = "OId";
    public static final String EXERCISE_ID = "EId";
    public static final String DATE_AND_TIME = "DT";

    protected void createTable(@NonNull SQLiteDatabase db) { db.execSQL(createTbl); }

    private static final String createTbl = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
            + _ID +           " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OBJECT_ID +     " INTEGER, "
            + EXERCISE_ID +   " INTEGER, "
            + DATE_AND_TIME + " NUMERIC);";

    public interface CursorObjectToExercise {
        Uri URI = TblObjectToExercise.BASE_CONTENT_URI;

        String TABLES = TblObjectToExercise.TABLE_NAME + " AS A LEFT JOIN " +
                TblMyExercise.TABLE_NAME + " AS B ON B." +
                TblMyExercise._ID + " = A." + TblObjectToExercise.EXERCISE_ID;

        String[] PROJECTION = new String[] {"B." + TblMyExercise._ID, "B." + TblMyExercise.EXERCISE,
                "B." + TblMyExercise.DIFFICULTY, "B." + TblMyExercise.DATE_AND_TIME };

        String SELECTION = "A." + TblObjectToExercise.OBJECT_ID + "=?";

        int Id = 0;
        int Exercise = 1;
        int Difficulty = 2;
        int DateTime = 3;
    }
}
