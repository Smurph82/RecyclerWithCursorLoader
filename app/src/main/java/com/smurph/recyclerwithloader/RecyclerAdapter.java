package com.smurph.recyclerwithloader;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smurph.recyclerwithloader.model.MyObject;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class RecyclerAdapter extends BaseCursorAdapter<RecyclerAdapter.RecyclerViewHolder> {

    public interface AdapterListener {
        /** @param v The view that was clicked. */
        void onItemClicked(View v, @NonNull MyObject obj);
        /** @param v The view that was long clicked. */
        boolean onItemLongClicked(View v, @NonNull MyObject obj);
    }
    private AdapterListener listener;

    public RecyclerAdapter() { this(null); }

    public RecyclerAdapter(@Nullable Cursor c) { super(c); }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_object, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder h, Cursor cursor) { h.setItem(cursor); }

    @Nullable
    public MyObject getItemAt(int position) {
        if (getCursor()==null || getCursor().getCount()<=position) {
            // Either cursor is null or the position is out of bounds.
            return null;
        }
        Cursor c = getCursor();
        c.moveToPosition(position);
        return new MyObject(c);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtName, txtDate;
        private MyObject object;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            txtName = (TextView) itemView.findViewById(android.R.id.text1);
            txtDate = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void onClick(View v) {
            if (listener!=null) { listener.onItemClicked(v, this.object); }
        }

        public void setItem(@NonNull Cursor cursor) {
            this.object = new MyObject(cursor);
            this.txtName.setText(object.getName());
            this.txtDate.setText(object.getDateTimeFormatted("MM/dd/yyyy h:mm:ss a"));
        }

        @Override
        public boolean onLongClick(View v) {
            return listener != null && listener.onItemLongClicked(v, this.object);
        }
    }

    public void setAdapterListener(@Nullable AdapterListener l) { this.listener = l; }
}
