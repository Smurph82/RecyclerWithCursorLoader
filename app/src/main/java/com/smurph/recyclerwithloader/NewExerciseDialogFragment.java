package com.smurph.recyclerwithloader;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.smurph.recyclerwithloader.model.MyExercise;

/**
 * Created by ben on 5/16/16.
 * This class opens a custom {@link DialogFragment}.
 */
public class NewExerciseDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_EXERCISE = "EXERCISE";

    public interface OnNewExerciseCreatedListener {
        void onCreatedComplete(@NonNull MyExercise exercise);

        void onDismissed();
    }
    private OnNewExerciseCreatedListener listener;

//    private TextInputLayout tilTitle;
    private TextInputEditText edtTitle;
    private RadioGroup radGrpDifficulty;

    private MyExercise exercise;

    /** Blank constructor */
    public NewExerciseDialogFragment() {}

    public static NewExerciseDialogFragment createDialog(@Nullable OnNewExerciseCreatedListener l,
                                                         @NonNull String title,
                                                         @Nullable MyExercise exercise) {
        NewExerciseDialogFragment frag = new NewExerciseDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        if (exercise!=null) { args.putParcelable(KEY_EXERCISE, exercise); }
        frag.setArguments(args);

        frag.setOnNewExerciseCreatedListener(l);

        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener!=null) {
                            if (exercise==null) {
                                exercise = new MyExercise(-1L,
                                        edtTitle.getText().toString().trim(), getDifficulty(),
                                        System.currentTimeMillis());
                            } else {
                                exercise.setExercise(edtTitle.getText().toString().trim());
                                exercise.setDifficulty(getDifficulty());
                            }

                            listener.onCreatedComplete(exercise);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener!=null) { listener.onDismissed(); }
                    }
                });

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_frag_new_exercise, null);
//        tilTitle = (TextInputLayout) v.findViewById(R.id.til_title);
        edtTitle = (TextInputEditText) v.findViewById(R.id.edt_exercise_title);
        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (getDialog()!=null) {
                    ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(s.length() > 0);
                }
            }
        });
        radGrpDifficulty = (RadioGroup) v.findViewById(R.id.rag_grp_difficulty);

        builder.setView(v);
        ((TextView)v.findViewById(R.id.txt_title)).setText(getArguments().getString(KEY_TITLE, ""));

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments().containsKey(KEY_EXERCISE)) {
            this.exercise = getArguments().getParcelable(KEY_EXERCISE);
            //noinspection ConstantConditions
            edtTitle.setText(this.exercise.getExercise());
            setDifficulty(this.exercise.getDifficulty());
        } else {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setEnabled(edtTitle.getText().length() > 0);
        }
    }

    /** @return The String of what difficulty is selected. */
    @NonNull private String getDifficulty() {
        switch (radGrpDifficulty.getCheckedRadioButtonId()) {
            case R.id.rad_easy: return "Easy";
            case R.id.rad_medium: return "Medium";
            case R.id.rad_hard: return "Hard";
            default: return "";
        }
    }

    /**
     * Set which {@link RadioButton} is checked in the {@link RadioGroup}.
     *
     * @param difficulty The currently set difficulty level.
     */
    private void setDifficulty(@NonNull String difficulty) {
        switch (difficulty) {
            case "Easy": radGrpDifficulty.check(R.id.rad_easy); break;
            case "Medium": radGrpDifficulty.check(R.id.rad_medium); break;
            case "Hard": radGrpDifficulty.check(R.id.rad_hard); break;
            default: radGrpDifficulty.check(R.id.rad_easy);
        }
    }

    /**
     * Set the listener so that the class that created this dialog will get the callbacks.
     *
     * @param l Your listener
     */
    public void setOnNewExerciseCreatedListener(@Nullable OnNewExerciseCreatedListener l) {
        this.listener = l;
    }
}
