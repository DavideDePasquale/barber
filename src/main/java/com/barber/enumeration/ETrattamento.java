package com.barber.enumeration;

public enum ETrattamento {
    TAGLIO_CAPELLI(39),
    TAGLIO_BARBA(39),
    COMBO(59);
    private final int durataMinuti;

    ETrattamento(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    public int getDurataMinuti() {
        return durataMinuti;
    }
}
