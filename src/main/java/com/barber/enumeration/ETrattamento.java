package com.barber.enumeration;

public enum ETrattamento {
    TAGLIO_CAPELLI(40),
    TAGLIO_BARBA(40),
    COMBO(60);
    private final int durataMinuti;

    ETrattamento(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    public int getDurataMinuti() {
        return durataMinuti;
    }
}
