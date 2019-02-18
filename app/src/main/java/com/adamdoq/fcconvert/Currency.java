package com.adamdoq.fcconvert;

import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.adamdoq.fcconvert.MainActivity.USD;

abstract class Currency {

    protected float exchangeRate;
    protected int iconId;
    protected String fullCurName;
    protected String description;
    protected int drawerBgId;
    protected int currencyId;

    public void updateCurrency(TextView textView, float numVal, ArrayList<Currency> setCurrencies) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern("0.##");

        textView.setText(String.valueOf(formatter.format(numVal
                * exchangeRate)));
    }

    // Getting the single dec version of the selected currency
    public float processNumVal(Currency selectedCurrency, TextView selectedText) {
        float numVal = USD / selectedCurrency.getExchangeRate() * Float.parseFloat(selectedText.getText().toString());
        return numVal;
    }

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
