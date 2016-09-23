package pl.eoferty.blc.enums;

/**
 * Created by Damian on 2015-04-18.
 */
public enum Balkon {

    BRAK("brak"),
    DUZY("duży"),
    TARAS("taras"),
    LOGGIA("loggia"),
    MALY("mały"),
    OKNO_BALKONOWE("okno balkonowe");

    private String balkon;

    Balkon(String balkon) {
        this.balkon = balkon;
    }

    @Override
    public String toString() {
        return balkon;
    }
}
