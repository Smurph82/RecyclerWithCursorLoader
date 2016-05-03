package com.smurph.recyclerwithloader;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Ben on 11/16/2015.
 *
 */
public abstract class BaseCursorAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private final DataSetObserver mObserver = new AdapterDataSetObserver();

    private Cursor cursor;
    private boolean isValid;

    public BaseCursorAdapter() { this(null); }

    public BaseCursorAdapter(@Nullable Cursor c) { init(c); }

    private void init(@Nullable Cursor cursor) {
        this.cursor = cursor;
        if (this.cursor !=null) {
            isValid =true;
            this.cursor.registerDataSetObserver(mObserver);
        }
        setHasStableIds(true);
    }

    @SuppressWarnings("unused") @Nullable
    public Cursor getCursor() { return cursor; }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(@Nullable Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old!=null && !old.isClosed()) { old.close(); }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there was not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    @Nullable
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        if (newCursor == cursor) { return null; }

        final Cursor oldCursor = cursor;
        if (oldCursor != null) { oldCursor.unregisterDataSetObserver(mObserver); }

        cursor = newCursor;
        if (cursor != null) { cursor.registerDataSetObserver(mObserver); }

        isValid = cursor != null;
        notifyDataSetChanged();
        return oldCursor;
    }

    @Override
    public int getItemCount() { return isActiveCursor() ? cursor.getCount() : 0; }

    @Override
    public long getItemId(int position) {
        return isActiveCursor() && cursor.moveToPosition(position)
                ? cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                : 0;
    }

    @Override
    public void onBindViewHolder(VH h, int position) {
        if (!isActiveCursor()) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(h, cursor);
    }

    public void onBindViewHolder(VH h, Cursor cursor) { /* Space to rent*/ }

    protected boolean isActiveCursor() { return isValid && cursor !=null; }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            isValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            isValid = false;
            notifyDataSetChanged();
        }
    }
}