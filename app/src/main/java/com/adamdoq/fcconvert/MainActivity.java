package com.adamdoq.fcconvert;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    GridLayout grandparentLayout;
    GridLayout selectedLine;
    Currency selectedCurrency;
    LinearLayout selectedLinear;
    TextView selectedText;
    TextView selectedCurrencyView;
    String selectedTag;
    ScrollView currencyDrawer;
    GridLayout changeLine;
    ImageView closeDrawerTrigger;

    private ArrayList<Currency> setCurrencies;
    private ArrayList<Currency> currencyList;
    private ArrayList<Integer> emptyTags;

    private boolean firstSelect = true;
    private boolean newVal = true;

    public void selectLine(View view) {
        // Line is not already selected and not run from onCreate
        if (!view.getTag().toString().equals(selectedTag) || firstSelect) {
            firstSelect = false;
            newVal = true;

            selectedLinear = (LinearLayout) view.getParent();
            selectedLine = (GridLayout) selectedLinear.getParent();
            selectedText = (TextView) selectedLinear.getChildAt(0);
            selectedCurrencyView = (TextView) selectedLinear.getChildAt(1);
            selectedTag = view.getTag().toString();
            selectedCurrency = setCurrencies.get(Integer.parseInt(view.getTag().toString()));

            for (int i = 0; i < 4; i++) {
                GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
                LinearLayout linearLayout = (LinearLayout) parentLayout.getChildAt(1);
                ImageView icon = (ImageView) parentLayout.getChildAt(0);

                // Line is selected and...
                if (parentLayout.getChildAt(1).getTag().equals(selectedTag)) {
                    // Line is NOT empty
                    if (!emptyTags.contains(Integer.valueOf(i))) {
                        setLineLayoutSelected(linearLayout);
                        setLineIconSelected(icon);
                    } else {
                        setLineLayoutEmpty(linearLayout);
                        setLineIconEmpty(icon);
                    }
                    // Line is NOT selected and...
                } else {
                    // Line is NOT empty
                    if (!emptyTags.contains(Integer.valueOf(i))) {
                        setLineLayoutUnselected(linearLayout);
                        setLineIconUnSelected(icon);
                    } else {
                        setLineLayoutEmpty(linearLayout);
                        setLineIconEmpty(icon);
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

        LinearLayout linearLayout = (LinearLayout) changeLine.getChildAt(1);

        // Inserting Empty Currency and...
        if (newCurrency instanceof EmptyLine) {
            setLineLayoutEmpty(linearLayout);
            setLineIconEmpty(icon);

            // Line isn't already empty
            if (!emptyTags.contains(Integer.parseInt(changeLine.getTag().toString()))) {
                emptyTags.add(Integer.parseInt(changeLine.getTag().toString()));
                Log.i("emptyTags", String.valueOf(emptyTags));
            }

            // Inserting non-empty currencyLine and...
        } else if (emptyTags.contains(Integer.parseInt(changeLine.getTag().toString()))) {
            Log.i("here", "3");
            emptyTags.remove(Integer.valueOf(Integer.parseInt(changeLine.getTag().toString())));
            TextView changeText = (TextView) linearLayout.getChildAt(0);
            changeText.setText("0");

            // ...line is selected.
            if (selectedLine.getTag().toString().equals(changeLine.getTag().toString())) {
                setLineLayoutSelected(linearLayout);
                setLineIconSelected(icon);

                // ...line is NOT selected.
            } else {
                setLineLayoutUnselected(linearLayout);
                setLineIconUnSelected(icon);
            }
        }

        if (changeLine.getTag().toString().equals(selectedLine.getTag().toString())) {
            updateSelectedCurrency(selectedLine.getChildAt(1));
        }
        updateUnselectedCurrencies();

        closeCurrencyDrawer(view);
    }

    private void setLineLayoutSelected(LinearLayout linearLayout) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_white);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_grey);
        linearLayout.getChildAt(0).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(1).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(0).setAlpha(0.8f);
        linearLayout.getChildAt(1).setAlpha(0.8f);

        TextView currencyTextView = (TextView) linearLayout.getChildAt(1);
        currencyTextView.setText("SELECTED");
        currencyTextView.setTextColor(Color.WHITE);
    }

    private void setLineLayoutUnselected(LinearLayout linearLayout) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_white);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_white);
        linearLayout.getChildAt(0).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(1).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(0).setAlpha(0.8f);
        linearLayout.getChildAt(1).setAlpha(0.8f);

        TextView currencyTextView = (TextView) linearLayout.getChildAt(1);
        currencyTextView.setText("UNSELECTED");
        currencyTextView.setTextColor(Color.BLACK);
    }

    private void setLineLayoutEmpty(LinearLayout linearLayout) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant_border);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_transparant_border);
        linearLayout.getChildAt(0).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(1).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(0).setAlpha(0.8f);
        linearLayout.getChildAt(1).setAlpha(0.8f);

        TextView curLabel = (TextView) linearLayout.getChildAt(0);
        curLabel.setText("0");
        curLabel = (TextView) linearLayout.getChildAt(1);
        curLabel.setText("BLANK");
        curLabel.setTextColor(Color.BLACK);
    }

    private void setLineIconSelected(ImageView icon) {
        icon.setBackgroundResource(R.drawable.line_background_transparant_border);
        icon.setPadding(0, 0, 0, 0);
    }

    private void setLineIconUnSelected(ImageView icon) {
        icon.setBackgroundResource(R.drawable.line_background_transparant);
        icon.setPadding(0, 0, 0, 0);
    }

    private void setLineIconEmpty(ImageView icon) {
        icon.setImageResource(R.drawable.line_background_transparant_border);
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
        if (!emptyTags.contains(Integer.valueOf(parent.getTag().toString()))) {
            if (view.getTag().toString().equals("back")) {
                if (selectedText.getText().toString().length() < 2
                        || selectedText.getText().toString().equals("0.0")
                        || newVal == true) {
                    if (newVal == true) {
                        newVal = false;
                    }
                    selectedText.setText("" + 0);
                } else {
                    selectedText.setText(selectedText.getText().toString()
                            .substring(0, selectedText.getText().toString().length() - 1));
                }
            } else if (newVal || selectedText.getText().toString().equals("0")) {
                selectedText.setText("" + view.getTag().toString());
                newVal = false;
            } else if (selectedText.getText().toString().equals("0.0")) {
                if (view.getTag().toString().equals("0")) {
                    selectedText.append("0");
                } else {
                    selectedText.setText("0." + view.getTag().toString());
                }
            } else {
                selectedText.append("" + view.getTag().toString());
            }

            if (selectedText.getText().toString().length() - selectedText.getText().toString().replaceAll("\\.", "").length() > 1) {
                selectedText.setText(selectedText.getText().toString().substring(0, selectedText.getText().toString().length() - 1));
            }

            if (selectedText.getText().toString().equals(".") || selectedText.getText().toString().equals("0.")) {
                selectedText.setText("0.0");
            }

            updateUnselectedCurrencies();
        }
    }

    public void updateUnselectedCurrencies() {
        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern("0.##");

        float numVal = selectedCurrency.getExchangeRate() * Float.parseFloat(selectedText.getText().toString());

        for (int i = 0; i < setCurrencies.size(); i++) {
            GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
            if (parentLayout != selectedLine && (!(setCurrencies.get(i) instanceof EmptyLine))) {
                LinearLayout linearLayout = (LinearLayout) parentLayout.getChildAt(1);
                TextView textView = (TextView) linearLayout.getChildAt(0);
                textView.setText(String.valueOf(formatter.format(numVal
                        / setCurrencies.get(i).getExchangeRate())));
            }
        }
    }

    public void updateSelectedCurrency(View view) {
        newVal = true;
        selectedLine = (GridLayout) view.getParent();
        selectedLinear = (LinearLayout) view;
        selectedText = (TextView) selectedLinear.getChildAt(0);
        selectedTag = view.getTag().toString();
        selectedCurrency = setCurrencies.get(Integer.parseInt(view.getTag().toString()));
    }

    public int getGenericIcon(String key) {
        switch (key) {
            case "AUD":
                return R.drawable.australia;
            case "BGN":
                return R.drawable.bulgaria;
            case "BRL":
                return R.drawable.brazil;
            case "CAD":
                return R.drawable.canada;
            case "CHF":
                return R.drawable.switzerland;
            case "CNY":
                return R.drawable.china;
            case "CZK":
                return R.drawable.czec_republic;
            case "DKK":
                return R.drawable.denmark;
            case "EUR":
                return R.drawable.european_union;
            case "GBP":
                return R.drawable.united_kingdom;
            case "HKD":
                return R.drawable.hong_kong;
            case "HRK":
                return R.drawable.croatia;
            case "HUF":
                return R.drawable.hungary;
            case "IDR":
                return R.drawable.indonesia;
            case "ILS":
                return R.drawable.israel;
            case "INR":
                return R.drawable.india;
            case "ISK":
                return R.drawable.iceland;
            case "JPY":
                return R.drawable.japan;
            case "KRW":
                return R.drawable.south_korea;
            case "MXN":
                return R.drawable.mexico;
            case "MYR":
                return R.drawable.malasya;
            case "NOK":
                return R.drawable.norway;
            case "NZD":
                return R.drawable.new_zealand;
            case "PHP":
                return R.drawable.philippines;
            case "PLN":
                return R.drawable.poland;
            case "RON":
                return R.drawable.romania;
            case "RUB":
                return R.drawable.russia;
            case "SEK":
                return R.drawable.sweden;
            case "SGD":
                return R.drawable.singapore;
            case "THB":
                return R.drawable.thailand;
            case "TRY":
                return R.drawable.turkey;
            case "USD":
                return R.drawable.united_states;
            case "ZAR":
                return R.drawable.south_africa;
        }
        return R.drawable.generic_icon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grandparentLayout = findViewById(R.id.convLines);

        setLoadedCurrencies(new FFGIL(), new SWIC(), new ZHR(), new EmptyLine());
        trackEmptyLines();
        setInitialSelectedLine();
        configureCurrencyDrawer();
        populateCurrencyLists();
        populateCurrencyDrawer();
    }

    private void setLoadedCurrencies(Currency cur0, Currency cur1, Currency cur2, Currency cur3) {
        setCurrencies = new ArrayList<>(Arrays.asList(cur0, cur1, cur2, cur3));

        ImageView icon = findViewById(R.id.icon0);
        icon.setImageResource(setCurrencies.get(0).getIconId());

        icon = findViewById(R.id.icon1);
        icon.setImageResource(setCurrencies.get(1).getIconId());

        icon = findViewById(R.id.icon2);
        icon.setImageResource(setCurrencies.get(2).getIconId());

        icon = findViewById(R.id.icon3);
        icon.setImageResource(setCurrencies.get(3).getIconId());
    }

    private void trackEmptyLines() {
        emptyTags = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if (setCurrencies.get(i) instanceof EmptyLine) {
                emptyTags.add(i);
            }
        }
    }

    private void setInitialSelectedLine() {
        selectedLine = findViewById(R.id.line0);
        selectedLinear = (LinearLayout) selectedLine.getChildAt(1);
        selectedText = (TextView) selectedLinear.getChildAt(0);
        selectedTag = selectedText.getTag().toString();
        selectLine(selectedText);
    }

    private void configureCurrencyDrawer() {
        currencyDrawer = findViewById(R.id.currencyDrawer);
        currencyDrawer.setVisibility(View.VISIBLE);

        currencyDrawer.post(new Runnable() {
            @Override
            public void run() {
                currencyDrawer.setTranslationX(currencyDrawer.getWidth());
            }
        });

        closeDrawerTrigger = findViewById(R.id.closeDrawerTrigger);
        closeDrawerTrigger.setVisibility(View.GONE);
        closeDrawerTrigger.setEnabled(false);
    }

    private void populateCurrencyLists() {
        // Populates custom currencies
        currencyList = new ArrayList<>(
                Arrays.asList(new FFGIL(), new SWIC(), new ZHR(), new EmptyLine())
        );

        // Populates generic currencies
        DownloadTask task = new DownloadTask();
        ArrayList<Currency> genericCurrencyList = new ArrayList<>();

        try {
            String json = task.execute("https://api.exchangeratesapi.io/latest?base=USD").get();
            Log.i("json", json);

            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonChildObject = (JSONObject) jsonObject.get("rates");
            Iterator<String> iterator = jsonChildObject.keys();

            while (iterator.hasNext()) {
                String key = iterator.next();

                GenericCurrency genericCurrency = new GenericCurrency(
                        Float.parseFloat(String.valueOf(jsonChildObject.get(key))),
                        getGenericIcon(key),
                        key,
                        R.color.colorPrimaryDark
                );
                genericCurrencyList.add(genericCurrency);
                //currencyList.add(genericCurrency);
                Log.i("main", key);
                Log.i("main", String.valueOf(jsonChildObject.get(key)));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(genericCurrencyList, new sortCurrencies());

        for (Currency currency : genericCurrencyList) {
            currencyList.add(currency);
        }
    }

    private void populateCurrencyDrawer() {
        LinearLayout currencyDrawerLayout = findViewById(R.id.currencyDrawerLayout);

        int listedCurrencies = 0;

        for (Currency currency : currencyList) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setTag(listedCurrencies);

            LinearLayout.LayoutParams drawerLineParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            drawerLineParams.height = 300;
            item.setLayoutParams(drawerLineParams);

            if (currency.drawerBgId != 0 && currency.drawerBgId != R.drawable.remove_bg) {
                item.setBackgroundResource(currency.drawerBgId);
                Drawable background = item.getBackground();
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

            if (listedCurrencies <= 4 - 1) {
                iconParams.setMargins(5, 2, 2, 2);
            } else {
                iconParams.setMargins(30, 2, 2, 2);
            }


            ImageView icon = new ImageView(this);
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

class sortCurrencies implements Comparator<Currency> {

    @Override
    public int compare(Currency cur1, Currency cur2) {
        return cur1.getDescription().compareTo(cur2.getDescription());
    }
}