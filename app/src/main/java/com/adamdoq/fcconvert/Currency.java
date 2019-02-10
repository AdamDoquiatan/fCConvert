package com.adamdoq.fcconvert;

import android.widget.ImageView;

abstract class Currency {

    protected float exchangeRate;
    protected int iconId;
    protected String fullCurName;
    protected String description;
    protected int drawerBgId;
    protected int currencyId;


    public float getExchangeRate() {
        return exchangeRate;
    }

    public int getIconId() {
        return iconId;
    }

    public String getDescription() { return description; }

    public String getFullCurName() { return fullCurName; }

    public int getCurrencyId() { return currencyId; }

    public void setCurrencyId(int id) {  }
}
