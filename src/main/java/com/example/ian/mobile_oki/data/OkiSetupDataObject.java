package com.example.ian.mobile_oki.data;

import java.util.ArrayList;

/**
 *
 * Created by Ian on 8/16/2017.
 */

public class OkiSetupDataObject {
    private String     kdMove;
    private String[] okiMoves;
    private int[]     okiRows;

    public OkiSetupDataObject(){}

    public OkiSetupDataObject(String kdMove, ArrayList<OkiMoveListItem> okiMoves, int[] okiRows) {
        this.kdMove = kdMove;
        setOkiMoves(okiMoves);
        this.okiRows = okiRows;
    }

    public String getKdMove() {
        return kdMove;
    }

    public void setKdMove(String kdMove) {
        this.kdMove = kdMove;
    }

    public String[] getOkiMoves() {
        return okiMoves;
    }

    public void setOkiMoves(String[] okiMoves) {
        this.okiMoves = okiMoves;
    }

    private void setOkiMoves(ArrayList<OkiMoveListItem> okiMoves) {
        this.okiMoves = new String[7];
        OkiMoveListItem item;

        for(int i = 0; i < okiMoves.size(); i++) {
            item = okiMoves.get(i);
            this.okiMoves[i] = item != null ? item.getMove() : null;
        }
    }

    public int[] getOkiRows() {
        return okiRows;
    }

    public void setOkiRows(int[] okiRows) {
        this.okiRows = okiRows;
    }
}
