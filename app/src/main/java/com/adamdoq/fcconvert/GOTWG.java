package com.adamdoq.fcconvert;

import android.text.Editable;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import static com.adamdoq.fcconvert.MainActivity.USD;

public class GOTWG extends Currency {

    public GOTWG() {
        super();
        exchangeRate = 5.93f; //Coppers per US Dollar
        iconId = R.drawable.gotwc_icon;
        fullCurName = "Dragons - Stags - Coppers";
        description = "Westerosi Coinage";
        drawerBgId = R.drawable.gotwc_bg;
    }

    @Override
    public void updateCurrency(TextView textView, float numVal, ArrayList<Currency> setCurrencies) {

        int coppers;
        int stags;
        int dragons;

        int totalcoppers = Math.round(numVal * exchangeRate);

        stags = totalcoppers / 56;
        coppers = totalcoppers % 56;

        dragons = stags / 210;
        stags = stags % 210;

        if(dragons == 0) {
            if(stags == 0) {
                textView.setText("" + coppers);
            } else {
                textView.setText(stags + "." + coppers);
            }
        } else {
            textView.setText(dragons + "." + stags + "." + coppers);
        }
    }

    public void handleCustomDecimals(TextView selectedText){
        selectedText.setText(selectedText.getText(), TextView.BufferType.EDITABLE);
        selectedText.setText(selectedText.getText().toString().replaceAll("\\.", ""));

        int valueLength = selectedText.getText().toString().length();

        if (selectedText.getText().toString().length() >= 3) {
            ((Editable) selectedText.getText()).insert(valueLength - 2, ".");
        }

        valueLength = selectedText.getText().toString().length();

        // Above added decimal impacts this one
        if (selectedText.getText().toString().length() >= 7) {
            ((Editable) selectedText.getText()).insert(valueLength - 6, ".");
        }
    }

    @Override
    public float processNumVal(Currency selectedCurrency, TextView selectedText) {

        float numVal;

        int coppers;
        int stags;
        int dragons;

        int valueLength = selectedText.getText().toString().length();
        Log.i("Selected Text Length", String.valueOf(selectedText.getText().toString().length()));
        Log.i("Selected Text", selectedText.getText().toString());

        switch(valueLength) {
            case 1:
                coppers = Integer.parseInt(selectedText.getText().toString().substring(0, 1));
                stags = 0;
                dragons = 0;
                break;
            case 2:
                coppers = Integer.parseInt(selectedText.getText().toString().substring(0, 2));
                stags = 0;
                dragons = 0;
                break;
            case 4:
                coppers = Integer.parseInt(selectedText.getText().toString().substring(2,4));
                stags = Integer.parseInt(selectedText.getText().toString().substring(0, 1));
                dragons = 0;
                break;
            case 5:
                coppers = Integer.parseInt(selectedText.getText().toString().substring(3,5));
                stags = Integer.parseInt(selectedText.getText().toString().substring(0, 2));
                dragons = 0;
                break;
            case 6:
                coppers = Integer.parseInt(selectedText.getText().toString().substring(4,6));
                stags = Integer.parseInt(selectedText.getText().toString().substring(0, 3));
                dragons = 0;
                break;
            default:
                int dragonLength = selectedText.getText().toString().substring(0, selectedText.getText().toString().length() - 7).length();

                Log.i("dragon length", String.valueOf(dragonLength));

                coppers = Integer.parseInt(selectedText.getText().toString().substring(dragonLength + 5, valueLength));
                stags = Integer.parseInt(selectedText.getText().toString().substring(dragonLength  + 1, valueLength - 3));
                dragons = Integer.parseInt(selectedText.getText().toString().substring(0, dragonLength));
                break;
        }

        //value in coppers
        numVal = coppers + (stags * 210) + (dragons * 11760);

        //value after conversion
        numVal = USD / selectedCurrency.getExchangeRate() * numVal;

        return numVal;
    }
}
