package com.adamdoq.fcconvert;

import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import static com.adamdoq.fcconvert.MainActivity.USD;

public class HPWC extends Currency {

    public HPWC() {
        super();
        exchangeRate = 125f; //Knuts
        iconId = R.drawable.bc_icon;
        fullCurName = "Gallions - Sickles - Knuts";
        description = "";
        drawerBgId = R.drawable.bc_bg;
    }

    @Override
    public void updateCurrency(TextView textView, float numVal, ArrayList<Currency> setCurrencies) {

        int knuts;
        int sickles;
        int gallions;

        int totalKnuts = Math.round(numVal * exchangeRate);

        sickles = totalKnuts / 29;
        knuts = totalKnuts % 29;

        gallions = sickles / 17;
        sickles = sickles % 17;

        if(gallions == 0) {
            if(sickles == 0) {
                textView.setText("" + knuts);
            } else {
                textView.setText(sickles + "." + knuts);
            }
        } else {
            textView.setText(gallions + "." + sickles + "." + knuts);
        }
    }

    @Override
    // Getting the three dec version of the selected currency

    public float processNumVal(Currency selectedCurrency, TextView selectedText) {

        float numVal;

        int knuts;
        int sickles;
        int gallions;

        int valueLength = selectedText.getText().toString().length();
        Log.i("Selected Text Length", String.valueOf(selectedText.getText().toString().length()));
        Log.i("Selected Text", selectedText.getText().toString());

        switch(valueLength) {
            case 1:
                knuts = Integer.parseInt(selectedText.getText().toString().substring(0, 1));
                sickles = 0;
                gallions = 0;
                break;
            case 2:
                knuts = Integer.parseInt(selectedText.getText().toString().substring(0, 2));
                sickles = 0;
                gallions = 0;
                break;
            case 4:
                knuts = Integer.parseInt(selectedText.getText().toString().substring(2,4));
                sickles = Integer.parseInt(selectedText.getText().toString().substring(0, 1));
                gallions = 0;
                break;
            case 5:
                knuts = Integer.parseInt(selectedText.getText().toString().substring(3,5));
                sickles = Integer.parseInt(selectedText.getText().toString().substring(0, 2));
                gallions = 0;
                break;
            default:
                int gallionLength = selectedText.getText().toString().substring(0, selectedText.getText().toString().length() - 6).length();

                Log.i("gallion length", String.valueOf(gallionLength));

                knuts = Integer.parseInt(selectedText.getText().toString().substring(gallionLength + 4, valueLength));
                sickles = Integer.parseInt(selectedText.getText().toString().substring(gallionLength  + 1, valueLength - 3));
                gallions = Integer.parseInt(selectedText.getText().toString().substring(0, gallionLength));
                break;
        }

        //value in knuts
        numVal = knuts + (sickles * 17) + (gallions * 493);

        //value after conversion
        numVal = USD / selectedCurrency.getExchangeRate() * numVal;

        //numVal = USD / selectedCurrency.getExchangeRate() * Float.parseFloat(selectedText.getText().toString());
        return numVal;
    }
}
