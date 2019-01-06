package com.adamdoq.fcconvert;

import android.widget.ImageView;

abstract class Currency {

    protected float exchangeRate;
    protected int iconId;
    protected  String description;
    protected int drawerBgId;


    public float getExchangeRate() {
        return exchangeRate;
    }

    public int getIconId() {
        return iconId;
    }

    public String getDescription() { return description; }

}
