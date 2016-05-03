package com.smurph.recyclerwithloader;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.smurph.recyclerwithloader.db.TblMyObject;
import com.smurph.recyclerwithloader.db.TblMyObject.CursorAllObjects;
import com.smurph.recyclerwithloader.model.MyObject;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        root = findViewById(R.id.root);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //noinspection ConstantConditions
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NOTE: 5/3/2016 This will create a new object and add it to the local SQLite db.
                new MyObject(-1L, "User " + adapter.getItemCount(), System.currentTimeMillis())
                        .save(view.getContext());
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerAdapter();
        adapter.setAdapterListener(adapterListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
    }

    /**
     * This is set to the {@link RecyclerAdapter} so when the objects are clicked they are returned
     * for processing.
     * @see RecyclerAdapter#setAdapterListener(RecyclerAdapter.AdapterListener)
     */
    private RecyclerAdapter.AdapterListener adapterListener =
            new RecyclerAdapter.AdapterListener() {
                @Override
                public void onItemClicked(View v, @NonNull MyObject obj) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    if (position == RecyclerView.NO_POSITION) {
                        Log.e(TAG, "getChildAdapterPosition returned NO_POSITION");
                        return;
                    }
                    Toast.makeText(v.getContext(),
                            "Position clicked " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean onItemLongClicked(View v, final @NonNull MyObject obj) {
                    final int position = recyclerView.getChildAdapterPosition(v);
                    if (position == RecyclerView.NO_POSITION) {
                        Log.e(TAG, "getChildAdapterPosition returned NO_POSITION");
                        return false;
                    }

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this item? " +
                                    "It cannot be undone.")
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    obj.delete(MainActivity.this);
                                }
                            })
                            .show();

                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
         getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CursorAllObjects.URI,
                CursorAllObjects.PROJECTION,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) { adapter.changeCursor(data); }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { adapter.changeCursor(null); }
}
