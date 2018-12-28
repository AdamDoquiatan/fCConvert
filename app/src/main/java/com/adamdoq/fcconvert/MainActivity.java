package com.adamdoq.fcconvert;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private GridLayout selectedLine;
    private TextView selectedText;
    private String selectedTag;
    private String selectedCurrency;

    //private ArrayList<String> setCurrencies;

    private HashMap<String, Float> currencyRates;

    public void selectLine(View view) {
        if(!view.getTag().toString().equals(selectedTag)) {
            selectedLine = (GridLayout) view.getParent();
            selectedText = (TextView) view;
            selectedTag = view.getTag().toString();
            //selectedCurrency = selectedLine.getTag().toString();

            GridLayout grandparentLayout = (GridLayout) view.getParent().getParent();

            for(int i = 0; i < 4; i++) {
                GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
                TextView textView = (TextView) parentLayout.getChildAt(1);
                if(parentLayout.getChildAt(1).getTag().equals(selectedTag)) {
                    parentLayout.setBackgroundColor(Color.DKGRAY);
                    textView.setTextColor(Color.WHITE);
                } else {
                    parentLayout.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(Color.BLACK);
                }
            }
        }
    }

    public void enterVal(View view) {
        //DecimalFormat formatter = new DecimalFormat();
        //formatter.applyPattern("0.##");

        if(selectedText.getText().toString().equals("0")) {
            selectedText.setText("" + view.getTag().toString());
        } else if(view.getTag().toString().equals("dec")){
            selectedText.append(".");
        } else if(view.getTag().toString().equals("back") && Float.parseFloat(selectedText.getText().toString()) != 0){
            selectedText.setText(selectedText.getText().toString()
                    .substring(0, selectedText.getText().toString().length() - 1));
            if(selectedText.getText().toString().length() == 0 || Float.parseFloat(selectedText.getText().toString()) == 0) {
                selectedText.setText("" + 0);
            }
        } else {
            selectedText.append("" + view.getTag().toString());
        }

        float numVal = Float.parseFloat(selectedText.getText().toString());

        GridLayout grandparentLayout = findViewById(R.id.convLines);
        for(int i = 0; i < 4; i++) {
            GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
            if(parentLayout != selectedLine && parentLayout.getChildAt(0).getTag() != null) {
                TextView textView = (TextView) parentLayout.getChildAt(1);
                textView.setText(String.valueOf(numVal * currencyRates.get(parentLayout.getChildAt(0).getTag().toString())));
            }
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedLine = findViewById(R.id.line0);
        selectedText = (TextView) selectedLine.getChildAt(1);
        selectedTag = selectedText.getTag().toString();

        selectedLine.setBackgroundColor(Color.DKGRAY);
        selectedText.setTextColor(Color.WHITE);

        currencyRates = new HashMap<>();
        currencyRates.put("USD", 1f);
        currencyRates.put("FF", 15f);

        //setCurrencies = new ArrayList<String>(Arrays.asList("USD", "FF"));


        selectedLine.getChildAt(0).setTag("USD");

        GridLayout line1 = findViewById(R.id.line1);
        line1.getChildAt(0).setTag("FF");

        selectedCurrency = "USD";



        Log.i("text", selectedText.getText().toString());

    }
}
