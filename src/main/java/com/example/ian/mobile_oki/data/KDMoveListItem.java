package com.example.ian.mobile_oki.data;

/**
 * Created by Ian on 7/7/2017.
 */

public class KDMoveListItem {


    private String moveName;
    private int KDA;
    private int KDRA;
    private int KDBRA;
    private int startup;
    private int active;
    private int recovery;

    public KDMoveListItem(String moveName,
                          int KDA, int KDRA, int KDBRA,
                          int startup, int active, int recovery) {
        this.moveName = moveName;
        this.KDA = KDA;
        this.KDRA = KDRA;
        this.KDBRA = KDBRA;
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

    public int getKDA() {
        return KDA;
    }

    public void setKDA(int KDA) {
        this.KDA = KDA;
    }

    public int getKDRA() {
        return KDRA;
    }

    public void setKDRA(int KDRA) {
        this.KDRA = KDRA;
    }

    public int getKDBRA() {
        return KDBRA;
    }

    public void setKDBRA(int KDBRA) {
        this.KDBRA = KDBRA;
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
