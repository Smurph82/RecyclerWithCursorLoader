package com.smurph.recyclerwithloader.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.smurph.recyclerwithloader.db.TblMyExercise;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ben on 5/9/2016.
 * The POJO class for the MyExercise object
 */
public class MyExercise implements Parcelable {

    private long id;
    private String exercise = "";
    private String difficulty = "";
    private long dateTime = -1L;

    public MyExercise() { }

    public MyExercise(@NonNull Cursor c) {
        long id = -1L;
        if (c.getColumnIndex(TblMyExercise._ID) > -1) {
            id = c.getLong(c.getColumnIndex(TblMyExercise._ID));
        }
        init(id, c.getString(c.getColumnIndex(TblMyExercise.EXERCISE)),
                c.getString(c.getColumnIndex(TblMyExercise.DIFFICULTY)),
                c.getLong(c.getColumnIndex(TblMyExercise.DATE_AND_TIME)));
    }

    public MyExercise(long id, @NonNull String exercise, @NonNull String difficulty,
                      long dateTime) {
        init(id, exercise, difficulty, dateTime);
    }

    private void init(long id, @NonNull String exercise, @NonNull String difficulty,
                      long dateTime) {
        this.id = id;
        this.exercise = exercise;
        this.difficulty = difficulty;
        this.dateTime = dateTime;
    }

    public long getId() { return this.id; }

    @NonNull
    public String getExercise() { return this.exercise; }

    @NonNull
    public String getDifficulty() { return this.difficulty; }

    public void setDifficulty(@NonNull String difficulty) { this.difficulty = difficulty; }

    public long getDateTime() { return this.dateTime; }

    public String getDateTimeFormatted(@NonNull String format) {
        if (dateTime < 0) { return "Unknown!"; }
        return new SimpleDateFormat(format, Locale.US).format(new Date(this.dateTime));
    }

    /**
     * Called to save this object to the local {@code SQLite} db.
     *
     * @param context The App {@code Context} used to to get the {@link ContentResolver}.
     */
    public void save(@NonNull final Context context) {
        Cursor c = null;
        ContentResolver cr = context.getContentResolver();
        try {
            c = cr.query(TblMyExercise.BASE_CONTENT_URI,
                    new String[]{TblMyExercise._ID},
                    TblMyExercise._ID + "=?",
                    new String[]{Long.toString(this.id)},
                    null);

            if (c == null) {
                return;
            }

            ContentValues values = new ContentValues(3);
            values.put(TblMyExercise.EXERCISE, this.exercise);
            values.put(TblMyExercise.DIFFICULTY, this.difficulty);
            values.put(TblMyExercise.DATE_AND_TIME, this.dateTime);

            if (c.getCount() > 0) { // Record exists update
                cr.update(TblMyExercise.BASE_CONTENT_URI, values, TblMyExercise._ID + "=?",
                        new String[]{Long.toString(id)});
            } else { // Record does not exists insert
                Uri record = cr.insert(TblMyExercise.BASE_CONTENT_URI, values);
                if (record != null) { id = Long.parseLong(record.getLastPathSegment()); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) { c.close(); }
            //noinspection UnusedAssignment
            c = null;
            //noinspection UnusedAssignment
            cr = null;
        }
    }

    /**
     * Called when you want to delete the item from the local {@code SQLite} db.
     *
     * @param context The App {@code Context} used to to get the {@link ContentResolver}.
     */
    public void delete(final Context context) {
        context.getContentResolver().delete(TblMyExercise.BASE_CONTENT_URI, TblMyExercise._ID + "=?",
                new String[]{Long.toString(this.id)});
    }

    public MyExercise(Parcel in) {
        this.id = in.readLong();
        this.exercise = in.readString();
        this.difficulty = in.readString();
        this.dateTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.exercise);
        dest.writeString(this.difficulty);
        dest.writeLong(this.dateTime);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (o == null || getClass() != o.getClass()) { return false; }

        MyExercise exercise = (MyExercise) o;

        if (this.id != exercise.id) { return false; }
        if (!this.exercise.equals(exercise.exercise)) { return false; }
        if (!this.difficulty.equals(exercise.difficulty)) { return false; }
        //noinspection RedundantIfStatement
        if (this.dateTime != exercise.dateTime) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.exercise.hashCode();
        result = 31 * result + this.difficulty.hashCode();
        return result;
    }

    public final static Parcelable.Creator<MyExercise> CREATOR =
            new Parcelable.Creator<MyExercise>() {
                public MyExercise createFromParcel(Parcel in) { return new MyExercise(in); }

                public MyExercise[] newArray(int size) { return new MyExercise[size]; }
            };
}
