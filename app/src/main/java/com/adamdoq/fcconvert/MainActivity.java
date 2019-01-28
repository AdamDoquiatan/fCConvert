package com.adamdoq.fcconvert;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    GridLayout grandparentLayout;
    GridLayout selectedLine;
    Currency selectedCurrency;
    TextView selectedText;
    String selectedTag;
    ScrollView currencyDrawer;
    GridLayout changeLine;
    ImageView closeDrawerTrigger;

    private ArrayList<Currency> setCurrencies;
    private ArrayList<Currency> currencyList;
    private ArrayList<Integer> emptyTags;

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
                    if(!emptyTags.contains(Integer.valueOf(i))) {
                        parentLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_grey);
                        Drawable background = parentLayout.getChildAt(1).getBackground();
                        background.setAlpha(200);
                        textView.setTextColor(Color.WHITE);

                        parentLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_grey);
                        background = parentLayout.getChildAt(0).getBackground();
                        background.setAlpha(200);
                        parentLayout.getChildAt(0).setPadding(0, 0, 0, 0);
                    } else {
                        parentLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_transparant_border);
                        parentLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant_border);
                        parentLayout.getChildAt(0).setPadding(0, 0, 0, 0);
                    }
                } else {
                    if(!emptyTags.contains(Integer.valueOf(i))) {
                        parentLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_white);
                        textView.setTextColor(Color.BLACK);

                        parentLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant);
                        parentLayout.getChildAt(0).setPadding(0, 0, 0, 0);
                    } else {
                        parentLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_transparant_border);
                        parentLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant_border);
                        parentLayout.getChildAt(0).setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
    }

    public void selectNewCurrency(View view) {
        Log.i("New Cur", view.getTag().toString());

        Currency newCurrency = currencyList.get(Integer.parseInt(view.getTag().toString()));

        setCurrencies.set(Integer.parseInt(changeLine.getTag().toString()), newCurrency);

        Log.i("this", setCurrencies.toString());

        ImageView icon = (ImageView) changeLine.getChildAt(0);
        icon.setImageResource(newCurrency.getIconId());

        if(newCurrency instanceof EmptyLine) {
            icon.setImageResource(R.drawable.line_background_transparant_border);
            changeLine.getChildAt(1).setBackgroundResource(R.drawable.line_background_transparant_border);
            TextView changeText = (TextView) changeLine.getChildAt(1);
            changeText.setText("0");

            selectedText.setTextColor(Color.BLACK);

            changeLine.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant_border);
            changeLine.getChildAt(0).setPadding(0,0,0,0);
            if(!emptyTags.contains(Integer.parseInt(changeLine.getTag().toString()))) {
                emptyTags.add(Integer.parseInt(changeLine.getTag().toString()));
                Log.i("emptyTags", String.valueOf(emptyTags));
            }
        } else if(emptyTags.contains(Integer.parseInt(changeLine.getTag().toString()))) {
            emptyTags.remove(Integer.valueOf(Integer.parseInt(changeLine.getTag().toString())));
            TextView changeText = (TextView) changeLine.getChildAt(1);
            changeText.setText("0");

            if(selectedLine.getTag().toString().equals(changeLine.getTag().toString())) {
                changeLine.getChildAt(1).setBackgroundResource(R.drawable.line_background_grey);
                Drawable background = selectedLine.getChildAt(1).getBackground();
                background.setAlpha(200);

                changeLine.getChildAt(0).setBackgroundResource(R.drawable.line_background_grey);
                selectedText.setTextColor(Color.WHITE);
                background = selectedLine.getChildAt(0).getBackground();
                background.setAlpha(200);
            } else {
                changeLine.getChildAt(1).setBackgroundResource(R.drawable.line_background_white);
                changeLine.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant);
            }
        }

        if(changeLine.getTag().toString().equals(selectedLine.getTag().toString())) {
            updateSelectedCurrency(selectedLine.getChildAt(1));
        }
        updateUnselectedCurrencies();

        closeCurrencyDrawer(view);
    }

    public void openCurrencyDrawer(View view) {
        changeLine = (GridLayout) view.getParent();

        currencyDrawer.animate().translationXBy(-currencyDrawer.getWidth()).setDuration(300);
        Log.i("Drawer Width", String.valueOf(currencyDrawer.getWidth()));
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

        View parent = (View) selectedText.getParent();
        if(!emptyTags.contains(Integer.valueOf(parent.getTag().toString()))) {
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

            updateUnselectedCurrencies();
        }
    }

    public void updateUnselectedCurrencies() {
        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern("0.##");

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

    public void updateSelectedCurrency(View view) {
        newVal = true;
        selectedLine = (GridLayout) view.getParent();
        selectedText = (TextView) view;
        selectedTag = view.getTag().toString();
        selectedCurrency = setCurrencies.get(Integer.parseInt(view.getTag().toString()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTags = new ArrayList<Integer>();

        // Set starting state
        grandparentLayout = findViewById(R.id.convLines);
        selectedLine = findViewById(R.id.line0);
        selectedText = (TextView) selectedLine.getChildAt(1);
        selectedTag = selectedText.getTag().toString();

        selectedLine.getChildAt(1).setBackgroundResource(R.drawable.line_background_grey);
        Drawable background = selectedLine.getChildAt(1).getBackground();
        background.setAlpha(200);
        selectedText.setTextColor(Color.WHITE);


        selectedLine.getChildAt(0).setBackgroundResource(R.drawable.line_background_grey);
        selectedLine.getChildAt(0).setPadding(0,0,0,0);
        background = selectedLine.getChildAt(0).getBackground();
        background.setAlpha(200);

        // Set starting loaded currencies
        setCurrencies = new ArrayList<>(Arrays.asList(new USD(), new FFGIL(), new SWIC(), new ZHR()));

        ImageView icon = findViewById(R.id.icon0);
        icon.setImageResource(setCurrencies.get(0).getIconId());

        icon = findViewById(R.id.icon1);
        icon.setImageResource(setCurrencies.get(1).getIconId());

        icon = findViewById(R.id.icon2);
        icon.setImageResource(setCurrencies.get(2).getIconId());

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
                Arrays.asList(new FFGIL(), new SWIC(), new ZHR(), new EmptyLine(), new USD())
        );

        DownloadTask task = new DownloadTask();
        try {
            String json = task.execute("https://api.exchangeratesapi.io/latest?base=USD").get();
            Log.i("json", json);

            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonChildObject = (JSONObject) jsonObject.get("rates");
            Iterator<String> iterator = jsonChildObject.keys();

            while(iterator.hasNext()) {
                String key = iterator.next();

                GenericCurrency genericCurrency = new GenericCurrency(
                        Float.parseFloat(String.valueOf(jsonChildObject.get(key))),
                        R.drawable.generic_icon,
                        key,
                        R.color.colorPrimaryDark
                );
                currencyList.add(genericCurrency);
                //Log.i("main", key);
                //Log.i("main", String.valueOf(jsonChildObject.get(key)));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout currencyDrawerLayout = findViewById(R.id.currencyDrawerLayout);

        int listedCurrencies = 0;

        for(Currency currency : currencyList) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setTag(listedCurrencies);

            LinearLayout.LayoutParams drawerLineParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            drawerLineParams.height = 300;
            item.setLayoutParams(drawerLineParams);

            if(currency.drawerBgId != 0 && currency.drawerBgId != R.drawable.remove_bg) {
                item.setBackgroundResource(currency.drawerBgId);
                background = item.getBackground();
                background.setAlpha(150);
            } else if (currency.drawerBgId == R.drawable.remove_bg) {
                    item.setBackgroundResource(currency.drawerBgId);
            } else {
                    item.setBackgroundResource(R.drawable.line_background_grey);
            }

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectNewCurrency(v);
                }
            });

            LinearLayout.LayoutParams iconParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            iconParams.height = 300;
            iconParams.width = 250;
            iconParams.setMargins(20, 25, 20 ,25);
            iconParams.setMargins(5, 2,2,2);


            icon = new ImageView(this);
            icon.setImageResource(currency.getIconId());
            icon.setLayoutParams(iconParams);
            item.addView(icon);


            TextView text = new TextView(this);
            text.setText(currency.getDescription());
            text.setTextColor(Color.WHITE);
            text.setPadding(20, 25, 20, 25);
            text.setTextSize(18);
            text.setHeight(300);
            text.setGravity(Gravity.CENTER_VERTICAL);
            item.addView(text);

            currency.setCurrencyId(listedCurrencies);
            listedCurrencies++;
            currencyDrawerLayout.addView(item);
        }
    }
}
