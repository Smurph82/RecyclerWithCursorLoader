package com.smurph.recyclerwithloader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class TblMyObject implements BaseColumns {

    public static final String TABLE_NAME = "TblMyObject";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" +
            MyProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String NAME = "Name";
    public static final String DATE_AND_TIME = "DateAndTime";

    protected void createTable(@NonNull SQLiteDatabase db) { db.execSQL(createTbl); }

    private static final String createTbl = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
            + _ID +           " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME +          " TEXT, "
            + DATE_AND_TIME + " NUMERIC);";

    public interface CursorAllObjects {
        Uri URI = BASE_CONTENT_URI;

        String[] PROJECTION = new String[] { _ID, NAME, DATE_AND_TIME };

        int Id = 0;
        int Name = 1;
        int DateTime = 2;
    }
}
