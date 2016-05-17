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

    private static final int CURRENT_VERSION = 4;
    private static final String DB_NAME = "mydb.db";

    /** Used when adding a column to an existing db table */
    private final String ADD_COLUMN = "ALTER TABLE %1$s ADD COLUMN %2$s %3$s %4$s;";

    public DBHelper(Context context) { super(context, DB_NAME, null, CURRENT_VERSION); }

    /**
     * This is called the first time and only the first time the local SQLite is created.
     *
     * @param db The local SQLite instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        new TblMyObject().createTable(db);
        new TblMyExercise().createTable(db);
        new TblObjectToExercise().createTable(db);
    }

    /**
     * This is called every time the DB is accessed. And if the local SQLite db has a lower version
     * number than the one set in {@link #DBHelper(Context)} then you will need to update. However
     * be careful as you can run into instances that can cause problem. For example. If you release
     * the app you cannot ask your users to uninstall and start all over entering their data. So
     * lets say that you add a new table to the local SQLite db. This is shown by my case statement
     * on line 60ish. Now you update that app push it to the store and hope everyone updates. Now
     * shortly after that update you add something to the {@link TblMyExercise} that you forgot
     * when you created that last update. Lets add a new column {@link TblMyExercise#DIFFICULTY}
     * and push the {@link #CURRENT_VERSION} to 3. You update the app and push it to the store. Now
     * you have a problem. See any user that did not update when you pushed the the second release
     * but did update when you pushed the third release Will never get the local SQLite updated
     * because the update statement will crash every time it runs. This is because the
     * {@link TblMyExercise#createTable(SQLiteDatabase)} will create the whole table. But then when
     * the update goes from 2 to 3 the {@code TblMyExercise} would have already been created
     * including the column you are adding in the the update from 2 to 3. This will cause the
     * statement to crash and the transaction to roll back. To work around this there are different
     * ways to go about it but I just increment {@code nextVersion} in case 2 because if case 2
     * runs then case 3 does not.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
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
                    case 2:
                        new TblMyExercise().createTable(db);
                        //noinspection UnusedAssignment
                        nextVersion++;
                        break;
                    case 3:
                        db.execSQL(String.format(ADD_COLUMN,
                                TblMyExercise.TABLE_NAME, TblMyExercise.DIFFICULTY, "TEXT", ""));
                        break;
                    case 4:
                        new TblObjectToExercise().createTable(db);
                        break;
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
