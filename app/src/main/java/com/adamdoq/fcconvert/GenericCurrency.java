package com.adamdoq.fcconvert;

public class GenericCurrency extends Currency {

    public GenericCurrency(float exchangeRate, int iconId, String fullCurName, String description,  int drawerBgId) {
        super();
        this.exchangeRate = exchangeRate;
        this.iconId = iconId;
        this.fullCurName = fullCurName;
        this.description = description;
        this.drawerBgId = drawerBgId;
    }

}

