package com.smurph.recyclerwithloader.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class MyProvider extends ContentProvider {

    private static final String TAG = MyProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.smurph.recyclerwithloader.db";

    private static final int TBL_MY_OBJECT = 1;
    private static final int TBL_MY_EXERCISE = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TblMyObject.TABLE_NAME, TBL_MY_OBJECT);
        uriMatcher.addURI(AUTHORITY, TblMyExercise.TABLE_NAME, TBL_MY_EXERCISE);
    }

    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;

    @Override
    public boolean onCreate() { dbHelper = new DBHelper(getContext()); return true; }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (!isDBOpen()) {
            logDBClosed();
            return null;
        }

        // Prevent bad String later
        if (selection==null) { selection = ""; }

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        String[] defaultProjection = null;
        String groupBy = null;
        String having = null;

        String tblPath = uri.getPathSegments().get(0);

        int uriMatch = uriMatcher.match(uri);
        switch (uriMatch) {
            case TBL_MY_OBJECT:
            case TBL_MY_EXERCISE:
                qBuilder.setTables(tblPath);
                break;
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // TODO: Check columns in incoming projection to make sure they're valid
        projection = (projection == null) ? defaultProjection : projection;

        Cursor c = null;
        try {
            c = qBuilder.query(db,projection,selection,selectionArgs,groupBy,having,sortOrder);
        } catch (SQLiteException e) {
            Log.e(TAG, "Query failed, returning null cursor.", e);
        }
        if (c!=null && getContext()!=null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (!isDBOpenWritable()) {
            logDBClosed();
            return null;
        }

        long rowId = 0L;
        Uri returnUri = null;
        if (values == null) { values = new ContentValues(); }

        db.beginTransaction();
        try {
            String tblPath = uri.getPathSegments().get(0);

            switch (uriMatcher.match(uri)) {
                case TBL_MY_OBJECT:
                case TBL_MY_EXERCISE:
                    rowId = db.insert(tblPath, null, values);
                    returnUri = Uri.withAppendedPath(uri, Long.toString(rowId));
                    break;
                default: throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) { e.printStackTrace();
        } finally { db.endTransaction();
        }
        if (rowId > -1 && getContext()!=null) {
            ContentResolver cr = getContext().getContentResolver();
            cr.notifyChange(uri, null);
            cr.notifyChange(returnUri, null);
            return returnUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (!isDBOpenWritable()) {
            logDBClosed();
            return -1;
        }

        // Prevent bad String later
        if (selection==null) { selection = ""; }

        int count = 0;
        db.beginTransaction();
        try {
            String tblPath = uri.getPathSegments().get(0);

            switch (uriMatcher.match(uri)) {
                case TBL_MY_OBJECT:
                case TBL_MY_EXERCISE:
                    count = db.delete(tblPath, selection, selectionArgs);
                    break;
                default: throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { db.endTransaction(); }

        if (getContext()!=null) { getContext().getContentResolver().notifyChange(uri, null); }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (!isDBOpenWritable()) {
            logDBClosed();
            return -1;
        }

        // Prevent bad String later
        if (selection==null) { selection = ""; }

        int count = 0;
        db.beginTransaction();
        try {
            String tblPath = uri.getPathSegments().get(0);

            switch (uriMatcher.match(uri)) {
                case TBL_MY_OBJECT:
                case TBL_MY_EXERCISE:
                    count = db.update(tblPath, values, selection, selectionArgs);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { db.endTransaction(); }

        if (getContext()!=null) { getContext().getContentResolver().notifyChange(uri, null); }
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // TODO Add all Uri to match
        switch (uriMatcher.match(uri)) {
            case TBL_MY_OBJECT:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd." + AUTHORITY + ".MyProvider" + TblMyObject.TABLE_NAME;
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /** Log that the DB is closed or not writable */
    private void logDBClosed() { Log.e(TAG, "DB is closed or not writable."); }

    /** Check to see if the db is open */
    private boolean isDBOpen() {
        if (db==null) { db=dbHelper.getWritableDatabase(); }
        return !db.isReadOnly();

    }

    /** Check to see if the db is open in read/write mode */
    private boolean isDBOpenWritable() {
        boolean isOpen = isDBOpen();
        return isOpen && !db.isReadOnly();
    }
}
