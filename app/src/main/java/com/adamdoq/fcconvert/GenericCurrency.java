package com.adamdoq.fcconvert;

public class GenericCurrency extends Currency {

    public GenericCurrency(float exchangeRate, int iconId, String description, int drawerBgId) {
        this.exchangeRate = exchangeRate;
        this.iconId = iconId;
        this.description = description;
        this.drawerBgId = drawerBgId;
    }

}

