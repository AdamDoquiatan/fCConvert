package com.adamdoq.fcconvert;

import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    GridLayout grandparentLayout;
    GridLayout selectedLine;
    Currency selectedCurrency;
    TextView selectedText;
    String selectedTag;
    ScrollView currencyDrawer;
    ImageView closeDrawerTrigger;

    private ArrayList<Currency> setCurrencies;
    private ArrayList<Currency> currencyList;

    private boolean newVal = true;

    public void selectLine(View view) {
        if(!view.getTag().toString().equals(selectedTag)) {
            newVal = true;
            selectedLine = (GridLayout) view.getParent();
            selectedText = (TextView) view;
            selectedTag = view.getTag().toString();
            selectedCurrency = setCurrencies.get(Integer.parseInt(view.getTag().toString()));

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

    public void openCurrencyDrawer(View view) {
        currencyDrawer.animate().translationXBy(-currencyDrawer.getWidth()).setDuration(300);
        closeDrawerTrigger.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDrawerTrigger.setEnabled(true);
            }
        }, 300);
    }

    public void closeCurrencyDrawer(View view) {
        currencyDrawer.animate().translationXBy(currencyDrawer.getWidth()).setDuration(300);
        closeDrawerTrigger.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDrawerTrigger.setVisibility(View.GONE);
            }
        }, 300);
    }

    public void enterVal(View view) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern("0.##");

        if(view.getTag().toString().equals("back")){
            if(selectedText.getText().toString().length() < 2
                    || selectedText.getText().toString().equals("0.0")
                    || newVal == true) {
                if(newVal == true) {
                    newVal = false;
                }
                selectedText.setText("" + 0);
            } else {
                selectedText.setText(selectedText.getText().toString()
                        .substring(0, selectedText.getText().toString().length() - 1));
            }
        } else if(newVal || selectedText.getText().toString().equals("0")) {
            selectedText.setText("" + view.getTag().toString());
            newVal = false;
        } else if(selectedText.getText().toString().equals("0.0")) {
            if(view.getTag().toString().equals("0")) {
                selectedText.append("0");
            } else {
                selectedText.setText("0." + view.getTag().toString());
            }
        } else {
            selectedText.append("" + view.getTag().toString());
        }

        if(selectedText.getText().toString().length() - selectedText.getText().toString().replaceAll("\\.", "").length() > 1) {
            selectedText.setText(selectedText.getText().toString().substring(0, selectedText.getText().toString().length() - 1));
        }

        if(selectedText.getText().toString().equals(".") || selectedText.getText().toString().equals("0.")) {
            selectedText.setText("0.0");
        }


        float numVal = selectedCurrency.getExchangeRate() * Float.parseFloat(selectedText.getText().toString());

        for(int i = 0; i < setCurrencies.size(); i++) {
            GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
            if(parentLayout != selectedLine && (!(setCurrencies.get(i) instanceof EmptyLine))) {
                TextView textView = (TextView) parentLayout.getChildAt(1);
                textView.setText(String.valueOf(formatter.format(numVal
                        / setCurrencies.get(i).getExchangeRate())));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set starting state
        grandparentLayout = findViewById(R.id.convLines);
        selectedLine = findViewById(R.id.line0);
        selectedText = (TextView) selectedLine.getChildAt(1);
        selectedTag = selectedText.getTag().toString();

        selectedLine.setBackgroundColor(Color.DKGRAY);
        selectedText.setTextColor(Color.WHITE);

        // Set starting loaded currencies
        setCurrencies = new ArrayList<>(Arrays.asList(new USD(), new FFGIL(), new EmptyLine(), new ZHR()));

        ImageView icon = findViewById(R.id.icon0);
        icon.setImageResource(setCurrencies.get(0).getIconId());

        icon = findViewById(R.id.icon1);
        icon.setImageResource(setCurrencies.get(1).getIconId());

        icon = findViewById(R.id.icon3);
        icon.setImageResource(setCurrencies.get(3).getIconId());

        selectedCurrency = setCurrencies.get(0);


        // Set up currency drawer
        currencyDrawer = findViewById(R.id.currencyDrawer);
        currencyDrawer.setVisibility(View.VISIBLE);

        currencyDrawer.post(new Runnable() {
            @Override
            public void run() {
                currencyDrawer.setTranslationX(currencyDrawer.getWidth());
            }
        });

        // Populate currency drawer
        closeDrawerTrigger = findViewById(R.id.closeDrawerTrigger);
        closeDrawerTrigger.setVisibility(View.GONE);
        closeDrawerTrigger.setEnabled(false);

        currencyList = new ArrayList<>(
                Arrays.asList(new FFGIL(), new SWIC(), new ZHR(), new USD())
        );

        LinearLayout currencyDrawerLayout = findViewById(R.id.currencyDrawerLayout);

        for(Currency currency : currencyList) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams iconParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            iconParams.height = 250;
            iconParams.width = 250;
            iconParams.setMargins(20, 25, 20 ,25);


            icon = new ImageView(this);
            icon.setImageResource(currency.getIconId());
            icon.setLayoutParams(iconParams);
            item.addView(icon);

            TextView text = new TextView(this);
            text.setText(currency.getDescription());
            text.setPadding(20, 25, 20, 25);
            text.setTextSize(18);
            text.setHeight(250);
            text.setGravity(Gravity.CENTER_VERTICAL);
            item.addView(text);

            currencyDrawerLayout.addView(item);
        }
    }
}
