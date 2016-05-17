package com.smurph.recyclerwithloader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.smurph.recyclerwithloader.db.TblObjectToExercise;
import com.smurph.recyclerwithloader.model.MyExercise;
import com.smurph.recyclerwithloader.model.MyObject;

/**
 * Created by Ben on 5/9/2016.
 * This is a second {@link Activity} to show calling it from {@link MainActivity} and passing data
 * between.
 */
public class ExerciseActivity extends AppCompatActivity {

    private static final String TAG = ExerciseActivity.class.getSimpleName();

    public static final String KEY_MY_OBJECT = "MY_OBJECT";

    private MyObject myObject;

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private View root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        root = findViewById(R.id.root);

        Bundle args = getIntent().getExtras();
        if (args!=null && args.containsKey(KEY_MY_OBJECT)) {
            this.myObject = args.getParcelable(KEY_MY_OBJECT);

            Log.d(TAG, "onCreate: MyObject: " + myObject);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //noinspection ConstantConditions
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openMyExerciseDialog(null); }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        adapter = new ExerciseAdapter();
        adapter.setAdapterListener(adapterListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(1, null, loader);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(ExerciseActivity.this,
                    TblObjectToExercise.CursorObjectToExercise.URI,
                    TblObjectToExercise.CursorObjectToExercise.PROJECTION,
                    TblObjectToExercise.CursorObjectToExercise.SELECTION,
                    new String[] { Long.toString(myObject.getId()) },
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { adapter.changeCursor(null); } };

    /**
     * This is set to the {@link ExerciseAdapter} so when the objects are clicked they are returned
     * for processing.
     * @see ExerciseAdapter#setAdapterListener(ExerciseAdapter.AdapterListener)
     */
    private ExerciseAdapter.AdapterListener adapterListener =
            new ExerciseAdapter.AdapterListener() {
                @Override
                public void onItemClicked(View v, @NonNull MyExercise obj) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    if (position == RecyclerView.NO_POSITION) {
                        Log.e(TAG, "getChildAdapterPosition returned NO_POSITION");
                        return;
                    }

                    Log.d(TAG, "onItemClicked: position " + position);

                    openMyExerciseDialog(adapter.getItemAt(position));
                }

                @Override
                public boolean onItemLongClicked(View v, final @NonNull MyExercise obj) {
                    final int position = recyclerView.getChildAdapterPosition(v);
                    if (position == RecyclerView.NO_POSITION) {
                        Log.e(TAG, "getChildAdapterPosition returned NO_POSITION");
                        return false;
                    }

                    Log.d(TAG, "onItemLongClicked: position " + position);

                    new AlertDialog.Builder(ExerciseActivity.this)
                            .setTitle("Deleting")
                            .setMessage("Are you sure you want to remove this exercise from this " +
                                    "user? This action cannot be undone.")
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteExerciseFromObject(obj);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();

                    return true;
                }
            };

    /**
     * This will check for a previous fragment and if one exists remove it. Then create a new
     * {@link NewExerciseDialogFragment} to show to the user.
     *
     * @param exercise The {@code MyExercise} object if you have one to edit.
     */
    private void openMyExerciseDialog(@Nullable MyExercise exercise) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("new_exercise");
        if (prev!=null) { ft.remove(prev); }

        NewExerciseDialogFragment frag = NewExerciseDialogFragment
                .createDialog(new NewExerciseDialogFragment.OnNewExerciseCreatedListener() {
                    @Override
                    public void onCreatedComplete(@NonNull MyExercise exercise) {
                        Log.d(TAG, "onCreatedComplete: MyExercise " + exercise.toString());
                        saveExercise(exercise);
                        getSupportLoaderManager().restartLoader(1, null, loader);
                    }

                    @Override
                    public void onDismissed() {
                        // Space to rent
                    }
                }, "New exercise", exercise);

        frag.show(ft, "new_exercise");
    }

    /**
     * This will save the {@link MyExercise} object and create the joins between it and the
     * selected {@link #myObject}.
     *
     * @param exercise The {@code MyExercise} object to save.
     */
    private void saveExercise(@NonNull MyExercise exercise) {
        if (exercise.save(this)) {
            ContentValues values = new ContentValues(3);
            values.put(TblObjectToExercise.OBJECT_ID, this.myObject.getId());
            values.put(TblObjectToExercise.EXERCISE_ID, exercise.getId());
            values.put(TblObjectToExercise.DATE_AND_TIME, System.currentTimeMillis());

            getContentResolver().insert(TblObjectToExercise.BASE_CONTENT_URI, values);
        }
    }

    /**
     * This will delete the record from {@link TblObjectToExercise} for this {@code MyExercise} and
     * {@link #myObject}.
     */
    private void deleteExerciseFromObject(@NonNull MyExercise exercise) {
        getContentResolver().delete(TblObjectToExercise.BASE_CONTENT_URI,
                TblObjectToExercise.EXERCISE_ID + "=? AND " + TblObjectToExercise.OBJECT_ID + "=?",
                new String[] { Long.toString(exercise.getId()), Long.toString(myObject.getId()) });
    }
}
