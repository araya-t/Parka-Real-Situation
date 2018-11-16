package com.iplds.minimintji.iplds.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.iplds.minimintji.iplds.BeaconApplication;
import com.iplds.minimintji.iplds.R;
import com.iplds.minimintji.iplds.manager.ChangeFloorIdToFloorName;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class ShowAvailableListItem extends BaseCustomViewGroup{

    TextView Floor,Available,Use;

    public ShowAvailableListItem(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public ShowAvailableListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public ShowAvailableListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ShowAvailableListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.zone_status, this);
    }

    private void initInstances() {
        // findViewById here
        Floor = (TextView) findViewById(R.id.Floor);
        Available = (TextView) findViewById(R.id.Available);
        Use = (TextView) findViewById(R.id.Use);
    }

    private void initWithAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /*
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleableName,
                defStyleAttr, defStyleRes);

        try {

        } finally {
            a.recycle();
        }
        */
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
    }

    public void setFloor(int floorId) {
        String name = ChangeFloorIdToFloorName.changeName(floorId);
        Floor.setText(""+name);
    }

    public void setAvailable(int available) {
        Available.setText(""+available);
    }

    public void setUnavailable(int use){
        Use.setText(""+use);
    }
}
