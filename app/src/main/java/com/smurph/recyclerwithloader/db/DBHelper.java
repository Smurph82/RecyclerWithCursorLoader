package com.smurph.recyclerwithloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Config;
import android.util.Log;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    private static final int CURRENT_VERSION = 1;
    private static final String DB_NAME = "mydb.db";

    public DBHelper(Context context) { super(context, DB_NAME, null, CURRENT_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new TblMyObject().createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            Log.i(TAG, "DB is current version.");
            return;
        }

        db.beginTransaction();
        try {
            Log.i(TAG, "Starting DB update. From: " + oldVersion + " to: " + newVersion);
            // NOTE: 5/3/2016 That ++i is used instead of i++. This is because we want the i value
            // to increment before we start the loop.
            for (int i = oldVersion; i < newVersion; ++i) {
                int nextVersion = i + 1;
                switch (nextVersion) {
                    default:
                        Log.i(TAG, "No DB updates");
                        break;
                }
            }
            db.setTransactionSuccessful();
            Log.i(TAG, "DB update successful.");
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
    }
}
