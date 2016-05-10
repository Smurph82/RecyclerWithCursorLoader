package com.smurph.recyclerwithloader.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.smurph.recyclerwithloader.db.TblMyObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class MyObject implements Parcelable {

    private long id;
    private String name="";
    private long dateTime=-1L;

    public MyObject() {}

    public MyObject(@NonNull Cursor c) {
        long id = -1L;
        if (c.getColumnIndex(TblMyObject._ID)>-1) {
            id = c.getLong(c.getColumnIndex(TblMyObject._ID));
        }
        init(id, c.getString(c.getColumnIndex(TblMyObject.NAME)),
                c.getLong(c.getColumnIndex(TblMyObject.DATE_AND_TIME)));
    }

    public MyObject(long id, @NonNull String name, long dateTime) { init(id, name, dateTime); }

    private void init(long id, @NonNull String name, long dateTime) {
        this.id = id;
        this.name=name;
        this.dateTime=dateTime;
    }

    public long getId() { return this.id; }

    @NonNull public String getName() { return this.name; }

    public long getDateTime() { return this.dateTime; }

    public String getDateTimeFormatted(@NonNull String format) {
        if (dateTime<0) { return "Unknown!"; }
        return new SimpleDateFormat(format, Locale.US).format(new Date(this.dateTime));
    }

    /**
     * Called to save this object to the local {@code SQLite} db.
     *
     * @param context The App {@code Context} used to to get the {@link ContentResolver}.
     */
    public void save(@NonNull final Context context) {
        Cursor c=null;
        ContentResolver cr = context.getContentResolver();
        try {
            c = cr.query(TblMyObject.BASE_CONTENT_URI,
                    new String[] { TblMyObject._ID },
                    TblMyObject._ID + "=?",
                    new String[] { Long.toString(this.id) },
                    null);

            if (c==null) { return; }

            ContentValues values = new ContentValues(2);
            values.put(TblMyObject.NAME, this.name);
            values.put(TblMyObject.DATE_AND_TIME, this.dateTime);

            if (c.getCount()>0) { // Record exists update
                cr.update(TblMyObject.BASE_CONTENT_URI, values, TblMyObject._ID + "=?",
                        new String[] { Long.toString(id) });
            } else { // Record does not exists insert
                Uri record = cr.insert(TblMyObject.BASE_CONTENT_URI, values);
                if (record!=null) { id = Long.parseLong(record.getLastPathSegment()); }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            if (c!=null && !c.isClosed()) { c.close(); }
            //noinspection UnusedAssignment
            c=null;
            //noinspection UnusedAssignment
            cr=null;
        }
    }

    /**
     * Called when you want to delete the item from the local {@code SQLite} db.
     *
     * @param context The App {@code Context} used to to get the {@link ContentResolver}.
     */
    public void delete(final Context context) {
        context.getContentResolver().delete(TblMyObject.BASE_CONTENT_URI, TblMyObject._ID + "=?",
                new String[] { Long.toString(this.id) });
    }

    @Override
    public String toString() {
        return "{" +
                " id: " + this.id +
                ", name: " + this.name +
                ", dateTime: " + this.dateTime +
                "}";
    }

    public MyObject(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.dateTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.dateTime);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public boolean equals(Object o) {
        if (this==o) { return true; }

        if (o==null || getClass()!=o.getClass()) { return false; }

        MyObject object = (MyObject) o;

        if (this.id!=object.id) { return false; }
        if (!this.name.equals(object.name)) { return false; }
        //noinspection RedundantIfStatement
        if (this.dateTime!=object.dateTime) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        //noinspection UnnecessaryLocalVariable
        int result = this.name.hashCode();
        return result;
    }

    public final static Parcelable.Creator<MyObject> CREATOR =
            new Parcelable.Creator<MyObject>() {
                public MyObject createFromParcel(Parcel in) { return new MyObject(in); }

                public MyObject[] newArray(int size) { return new MyObject[size]; }
            };
}
