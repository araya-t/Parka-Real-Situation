package com.iplds.minimintji.iplds.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iplds.minimintji.iplds.R;


public class AccelerometerDataViewGroup extends FrameLayout {

    private TextView tvAccel_x;
    private TextView tvAccel_y;
    private TextView tvAccel_z;
    private EditText editTextListenerSampling;
    private Button btnEnter;

    public AccelerometerDataViewGroup(@NonNull Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public AccelerometerDataViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
    }

    public AccelerometerDataViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
    }

    @TargetApi(21)
    public AccelerometerDataViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
    }

    private void initInflate() {
        // Inflate Layout here
        inflate(getContext(), R.layout.layout_accelerometer_data, this);

    }

    private void initInstances() {
        // findViewById of item in ViewGroup here

        tvAccel_x = (TextView) findViewById(R.id.tvAccel_x);
        tvAccel_y = (TextView) findViewById(R.id.tvAccel_y);
        tvAccel_z = (TextView) findViewById(R.id.tvAccel_z);

        editTextListenerSampling = (EditText) findViewById(R.id.editTextListenerSampling);
        btnEnter = (Button) findViewById(R.id.btnEnter);

    }

    public TextView getTvAccel_x() {
        return tvAccel_x;
    }

    public void setTvAccel_x_text(String text) {
        this.tvAccel_x.setText(text);
    }

    public TextView getTvAccel_y() {
        return tvAccel_y;
    }

    public void setTvAccel_y_text(String text) {
        this.tvAccel_y.setText(text);
    }

    public TextView getTvAccel_z() {
        return tvAccel_z;
    }

    public void setTvAccel_z_text(String text) {
        this.tvAccel_z.setText(text);
    }

    public EditText getEditTextListenerSampling() {
        return editTextListenerSampling;
    }

    public void setEditTextListenerSampling(String text) {
        this.editTextListenerSampling.setText(text);
    }

    public Button getBtnEnter() {
        return btnEnter;
    }

    public void setBtnEnter(String text) {
        btnEnter.setText(text);
    }
}
