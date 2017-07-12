package com.example.ian.mobile_oki.data;

/**
 * Created by Ian on 7/7/2017.
 */

public class KDMoveListItem {


    private String moveName;
    private int kda;
    private int kdra;
    private int kdbra;
    private int startup;
    private int active;
    private int recovery;

    public KDMoveListItem(String moveName,
                          int kda, int kdra, int kdbra,
                          int startup, int active, int recovery) {
        this.moveName = moveName;
        this.kda = kda;
        this.kdra = kdra;
        this.kdbra = kdbra;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public int getKda() {
        return kda;
    }

    public void setKda(int kda) {
        this.kda = kda;
    }

    public int getKdra() {
        return kdra;
    }

    public void setKdra(int kdra) {
        this.kdra = kdra;
    }

    public int getKdbra() {
        return kdbra;
    }

    public void setKdbra(int kdbra) {
        this.kdbra = kdbra;
    }

    public int getStartup() {
        return startup;
    }

    public void setStartup(int startup) {
        this.startup = startup;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getRecovery() {
        return recovery;
    }

    public void setRecovery(int recovery) {
        this.recovery = recovery;
    }
}
