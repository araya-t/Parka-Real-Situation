package com.iplds.minimintji.iplds.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.iplds.minimintji.iplds.R;

public class ChangeGmsStatusViewGroup extends BaseCustomViewGroup{
    private Button btnChangeGmsStatus;

    public ChangeGmsStatusViewGroup(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public ChangeGmsStatusViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
    }

    public ChangeGmsStatusViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
    }

    @TargetApi(21)
    public ChangeGmsStatusViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
    }

    private void initInflate() {
        inflate(getContext(), R.layout.layout_change_gms_status_button, this);
    }

    private void initInstances() {
        btnChangeGmsStatus = findViewById(R.id.btnChangeGmsStatus);
    }

    public Button getBtnChangeGmsStatus() {
        return btnChangeGmsStatus;
    }
}
