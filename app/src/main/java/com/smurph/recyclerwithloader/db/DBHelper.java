package com.smurph.recyclerwithloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class DBHelper extends SQLiteOpenHelper {


    private static final int CURRENT_VERSION = 1;
    private static final String DB_NAME = "mydb.db";

    public DBHelper(Context context) { super(context, DB_NAME, null, CURRENT_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new TblMyObject().createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
