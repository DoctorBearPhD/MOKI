package com.example.ian.mobile_oki.data;

/**
 *
 * Created by Ian on 8/16/2017.
 */

public class OkiSetupDataObject {
    private String   charCode;
    private String     kdMove;
    private String[] okiMoves;
    private int[]     okiRows;

    public OkiSetupDataObject(){}

    public OkiSetupDataObject(String charCode, String kdMove, String[] okiMoves, int[] okiRows) {
        this.charCode = charCode;
        this.kdMove = kdMove;
        this.okiMoves = okiMoves;
        this.okiRows = okiRows;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
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

    public int[] getOkiRows() {
        return okiRows;
    }

    public void setOkiRows(int[] okiRows) {
        this.okiRows = okiRows;
    }
}
