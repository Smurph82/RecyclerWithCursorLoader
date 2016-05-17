package com.smurph.recyclerwithloader;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smurph.recyclerwithloader.model.MyExercise;

/**
 * Created by Ben on 5/3/2016.
 *
 */
public class ExerciseAdapter extends BaseCursorAdapter<ExerciseAdapter.RecyclerViewHolder> {

    public interface AdapterListener {
        /** @param v The view that was clicked. */
        void onItemClicked(View v, @NonNull MyExercise obj);
        /** @param v The view that was long clicked. */
        boolean onItemLongClicked(View v, @NonNull MyExercise obj);
    }
    private AdapterListener listener;

    public ExerciseAdapter() { this(null); }

    public ExerciseAdapter(@Nullable Cursor c) { super(c); }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_exercise, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder h, Cursor cursor) { h.setItem(cursor); }

    @Nullable
    public MyExercise getItemAt(int position) {
        if (getCursor()==null || getCursor().getCount()<=position) {
            // Either cursor is null or the position is out of bounds.
            return null;
        }
        Cursor c = getCursor();
        c.moveToPosition(position);
        return new MyExercise(c);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtExercise, txtDifficulty, txtDate;
        private MyExercise object;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            txtExercise = (TextView) itemView.findViewById(android.R.id.text1);
            txtDifficulty = (TextView) itemView.findViewById(android.R.id.text2);
            txtDate = (TextView) itemView.findViewById(R.id.text3);
        }

        @Override
        public void onClick(View v) {
            if (listener!=null) { listener.onItemClicked(v, this.object); }
        }

        public void setItem(@NonNull Cursor cursor) {
            this.object = new MyExercise(cursor);
            this.txtExercise.setText(object.getExercise());
            this.txtDifficulty.setText(object.getDifficulty());
            this.txtDate.setText(object.getDateTimeFormatted("MM/dd/yyyy h:mm:ss a"));
        }

        @Override
        public boolean onLongClick(View v) {
            return listener != null && listener.onItemLongClicked(v, this.object);
        }
    }

    public void setAdapterListener(@Nullable AdapterListener l) { this.listener = l; }
}
