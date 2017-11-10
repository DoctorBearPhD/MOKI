package com.example.ian.mobile_oki.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data object for the KD Move Select screen's list items.
 * Created by Ian on 7/7/2017.
 */

public class KDMoveListItem implements Parcelable {


    private String moveName;
    private String command;
    private int kda;
    private int kdra;
    private int kdbra;
    private int startup;
    private int active;
    private int recovery;

    public KDMoveListItem(String moveName, String command,
                          int kda, int kdra, int kdbra,
                          int startup, int active, int recovery) {
        this.moveName = moveName;
        this.command = command;
        this.kda = kda;
        this.kdra = kdra;
        this.kdbra = kdbra;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    private KDMoveListItem(Parcel in) {
        moveName = in.readString();
        command = in.readString();
        kda = in.readInt();
        kdra = in.readInt();
        kdbra = in.readInt();
        startup = in.readInt();
        active = in.readInt();
        recovery = in.readInt();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KDMoveListItem)
            return getMoveName().equals(((KDMoveListItem) (obj)).getMoveName());
        else return super.equals(obj);
    }

    public String getMoveName() {
        return moveName;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setMoveName(String moveName) {
//        this.moveName = moveName;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public String getCommand() {
        return command;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setCommand(String command) {
//        this.command = command;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public int getKda() {
        return kda;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setKda(int kda) {
//        this.kda = kda;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public int getKdra() {
        return kdra;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setKdra(int kdra) {
//        this.kdra = kdra;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public int getKdbra() {
        return kdbra;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setKdbra(int kdbra) {
//        this.kdbra = kdbra;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public int getStartup() {
        return startup;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setStartup(int startup) {
//        this.startup = startup;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public int getActive() {
        return active;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setActive(int active) {
//        this.active = active;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    public int getRecovery() {
        return recovery;
    }

// --Commented out by Inspection START (11/8/2017 2:24 PM):
//    public void setRecovery(int recovery) {
//        this.recovery = recovery;
//    }
// --Commented out by Inspection STOP (11/8/2017 2:24 PM)

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(moveName);
        out.writeInt(kda);
        out.writeInt(kdra);
        out.writeInt(kdbra);
        out.writeInt(startup);
        out.writeInt(active);
        out.writeInt(recovery);
    }

    public static final Creator<KDMoveListItem> CREATOR = new Creator<KDMoveListItem>() {
        @Override
        public KDMoveListItem createFromParcel(Parcel in) {
            return new KDMoveListItem(in);
        }

        @Override
        public KDMoveListItem[] newArray(int size) {
            return new KDMoveListItem[size];
        }
    };
}
