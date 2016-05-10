package com.smurph.recyclerwithloader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Config;
import android.util.Log;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        if (args!=null && args.containsKey(KEY_MY_OBJECT)) {
            this.myObject = args.getParcelable(KEY_MY_OBJECT);

            Log.d(TAG, "onCreate: MyObject: " + myObject);
        }
    }
}
