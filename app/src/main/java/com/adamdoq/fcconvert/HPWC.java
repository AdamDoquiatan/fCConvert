package com.adamdoq.fcconvert;

import android.text.Editable;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import static com.adamdoq.fcconvert.MainActivity.USD;

public class HPWC extends Currency {

    public HPWC() {
        super();
        exchangeRate = 74.246f; //Knuts per US Dollar
        iconId = R.drawable.hpwc_icon;
        fullCurName = "Gallions - Sickles - Knuts";
        description = "Wizard Wealth";
        drawerBgId = R.drawable.hpwc_bg;
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

    public void handleCustomDecimals(TextView selectedText){
        selectedText.setText(selectedText.getText(), TextView.BufferType.EDITABLE);
        selectedText.setText(selectedText.getText().toString().replaceAll("\\.", ""));

        int valueLength = selectedText.getText().toString().length();

        if (selectedText.getText().toString().length() >= 3) {
            ((Editable) selectedText.getText()).insert(valueLength - 2, ".");
        }

        valueLength = selectedText.getText().toString().length();

        // Above added decimal impacts this one
        if (selectedText.getText().toString().length() >= 6) {
            ((Editable) selectedText.getText()).insert(valueLength - 5, ".");
        }
    }

    @Override
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

        return numVal;
    }


}
