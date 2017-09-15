package com.example.ian.mobile_oki.data;

/**
 * Created by Ian on 8/5/2017.
 */

public class OkiMoveListItem {


    private final String mMove;
    private final String mCommand;
    private final int mTotal;
    private final int mStartup;
    private final String mActive;
    private final int mRecovery;

    public OkiMoveListItem(String move, String command,
                           int total, int startup, String active, int recovery){
        mMove = move;
        mCommand = command;
        mTotal = total;
        mStartup = startup;
        mActive = active;
        mRecovery = recovery;
    }

    public String getMove() {
        return mMove;
    }

    public String getCommand() {
        return mCommand;
    }

    public int getTotal() {
        return mTotal;
    }

    public int getStartup() {
        return mStartup;
    }

    public String getActive() {
        return mActive;
    }

    public int getRecovery() {
        return mRecovery;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OkiMoveListItem) {
            if (getMove().equals(    ((OkiMoveListItem) obj).getMove()    ) &&
                getCommand().equals( ((OkiMoveListItem) obj).getCommand() )     ) {

                return true;
            }
        }
        // else...
        return super.equals(obj);

    }
}
