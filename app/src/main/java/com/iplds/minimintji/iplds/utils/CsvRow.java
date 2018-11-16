package com.iplds.minimintji.iplds.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class CsvRow implements Parcelable {
    private int countRow;
    private long millisec;
    private String timeStamp;
    private long timeStampLong;
    private double acce_x;
    private double acce_y;
    private double acce_z;
    private boolean is_still;
    private boolean is_stop_engine;
    private double x_position;
    private double y_position;
    private boolean isChangeGmsStatus;

    public CsvRow(){

    }

    public CsvRow(int countRow, String millisec, String timeStamp, long timeStamplong,
                  String acce_x, String acce_y, String acce_z, String is_still,
                  String is_stop_engine, String x_position, String y_position, String isChangeGmsStatus){
        this.countRow = countRow;
        this.millisec = Long.parseLong(millisec);
        this.timeStamp = timeStamp;
        this.timeStampLong = timeStamplong;
        this.acce_x = Double.parseDouble(acce_x);
        this.acce_y = Double.parseDouble(acce_y);
        this.acce_z = Double.parseDouble(acce_z);
        this.is_still = Boolean.parseBoolean(is_still);
        this.is_stop_engine = Boolean.parseBoolean(is_stop_engine);
        this.x_position = Double.parseDouble(x_position);
        this.y_position = Double.parseDouble(y_position);
        this.isChangeGmsStatus = Boolean.parseBoolean(isChangeGmsStatus);
    }

    protected CsvRow(Parcel in) {
        countRow = in.readInt();
        millisec = in.readLong();
        timeStamp = in.readString();
        timeStampLong = in.readLong();
        acce_x = in.readDouble();
        acce_y = in.readDouble();
        acce_z = in.readDouble();
        is_still = in.readByte() != 0;
        is_stop_engine = in.readByte() != 0;
        x_position = in.readDouble();
        y_position = in.readDouble();
        isChangeGmsStatus = in.readByte() != 0;
    }

    public static final Creator<CsvRow> CREATOR = new Creator<CsvRow>() {
        @Override
        public CsvRow createFromParcel(Parcel in) {
            return new CsvRow(in);
        }

        @Override
        public CsvRow[] newArray(int size) {
            return new CsvRow[size];
        }
    };

    public int getCountRow(){
        return countRow;
    }

    public void setCountRow(int countRow){
        this.countRow = countRow;
    }

    public long getMillisec() {
        return millisec;
    }

    public void setMillisec(long millisec) {
        this.millisec = millisec;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStampLong() {
        return timeStampLong;
    }

    public void setTimeStampLong(long timeStampLong) {
        this.timeStampLong = timeStampLong;
    }

    public double getAcce_x() {
        return acce_x;
    }

    public void setAcce_x(double acce_x) {
        this.acce_x = acce_x;
    }

    public double getAcce_y() {
        return acce_y;
    }

    public void setAcce_y(double acce_y) {
        this.acce_y = acce_y;
    }

    public double getAcce_z() {
        return acce_z;
    }

    public void setAcce_z(double acce_z) {
        this.acce_z = acce_z;
    }

    public boolean is_stop_engine() {
        return is_stop_engine;
    }

    public void setIs_stop_engine(boolean is_stop_engine) {
        this.is_stop_engine = is_stop_engine;
    }

    public double getX_position() {
        return x_position;
    }

    public void setX_position(double x_position) {
        this.x_position = x_position;
    }

    public double getY_position() {
        return y_position;
    }

    public void setY_position(double y_position) {
        this.y_position = y_position;
    }

    public boolean is_still() {
        return is_still;
    }

    public void setIs_still(boolean is_still) {
        this.is_still = is_still;
    }

    public boolean isChangeGmsStatus() {
        return isChangeGmsStatus;
    }

    public void setChangeGmsStatus(boolean changeGmsStatus) {
        isChangeGmsStatus = changeGmsStatus;
    }

    @Override
    public String toString() {
        return "CsvRow{" +
                "countRow=" + countRow +
                ", millisec=" + millisec +
                ", timeStamp=" + timeStamp +
                ", timeStampLong=" + timeStampLong +
                ", acce_x=" + acce_x +
                ", acce_y=" + acce_y +
                ", acce_z=" + acce_z +
                ", is_stop_engine=" + is_stop_engine +
                ", x_position=" + x_position +
                ", y_position=" + y_position +
                '}';
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(countRow);
        dest.writeLong(millisec);
        dest.writeString(timeStamp);
        dest.writeLong(timeStampLong);
        dest.writeDouble(acce_x);
        dest.writeDouble(acce_y);
        dest.writeDouble(acce_z);
        dest.writeByte((byte) (is_still ? 1 : 0));
        dest.writeByte((byte) (is_stop_engine ? 1 : 0));
        dest.writeDouble(x_position);
        dest.writeDouble(y_position);
        dest.writeByte((byte) (isChangeGmsStatus ? 1 : 0));

    }
}
