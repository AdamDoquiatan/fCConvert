package com.adamdoq.fcconvert;

import android.widget.ImageView;

abstract class Currency {

    protected float exchangeRate;
    protected int iconId;


    public float getExchangeRate() {
        return exchangeRate;
    }

    public int getIconId() {
        return iconId;
    }
}
