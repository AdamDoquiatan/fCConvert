package com.adamdoq.fcconvert;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    static final float USD = 1;
    static final long TWELVEHOURS = 43200000;
    static final Currency[] defaultCurrencies = new Currency[]{new FFGIL(), new ZHR(), new GOTWG(), new EmptyLine()};

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

    SharedPreferences saveFile;

    LocationManager locationManager;
    LocationListener locationListener;
    String userCountryName = "Unknown";
    String userCountryCode = "Unknown";
    Context context = this;

    private ArrayList<Currency> setCurrencies;
    private ArrayList<Currency> currencyList;
    private ArrayList<Integer> emptyTags;
    private ArrayList<String> threeDecCurrencies;

    private boolean firstSelect = true;
    private boolean newVal = true;
    private boolean userLocationFound = false;

    public void selectLine(View view) {
        // Line is not already selected and not run from onCreate
        if (!view.getTag().toString().equals(selectedTag) || firstSelect) {
            newVal = true;

            selectedLinear = (LinearLayout) view.getParent();
            selectedLine = (GridLayout) selectedLinear.getParent();
            selectedText = (TextView) selectedLinear.getChildAt(0);
            selectedCurrencyView = (TextView) selectedLinear.getChildAt(1);
            selectedTag = view.getTag().toString();
            selectedCurrency = setCurrencies.get(Integer.parseInt(view.getTag().toString()));

            if(firstSelect) {
                for (int i = 0; i < 4; i++) {
                    GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
                    LinearLayout linearLayout = (LinearLayout) parentLayout.getChildAt(1);

                    TextView currencyTextView = (TextView) linearLayout.getChildAt(1);
                    currencyTextView.setText(setCurrencies.get(i).getFullCurName());
                    currencyTextView.setTypeface(null, Typeface.BOLD);
                }
            }

            firstSelect = false;

            for (int i = 0; i < 4; i++) {
                GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
                LinearLayout linearLayout = (LinearLayout) parentLayout.getChildAt(1);
                ImageView icon = (ImageView) parentLayout.getChildAt(0);

                // Line is selected and...
                if (parentLayout.getChildAt(1).getTag().equals(selectedTag)) {
                    // Line is NOT empty
                    if (!emptyTags.contains(Integer.valueOf(i))) {
                        setLineLayoutSelected(linearLayout, i);
                        setLineIconSelected(icon);
                    } else {
                        setLineLayoutSelectedEmpty(linearLayout, i);
                        setLineIconEmpty(icon);
                    }
                    // Line is NOT selected and...
                } else {
                    // Line is NOT empty
                    if (!emptyTags.contains(Integer.valueOf(i))) {
                        setLineLayoutUnselected(linearLayout, i);
                        setLineIconUnSelected(icon);
                    } else {
                        setLineLayoutUnselectedEmpty(linearLayout, i);
                        setLineIconEmpty(icon);
                    }
                }
            }
        }
    }

    public void selectNewCurrency(View view) {
        Log.i("New Cur", view.getTag().toString());

        int currencyPosition = Integer.parseInt(changeLine.getTag().toString());
        Currency newCurrency = currencyList.get(Integer.parseInt(view.getTag().toString()));

        setCurrencies.set(currencyPosition, newCurrency);

        Log.i("setCurrencies", setCurrencies.toString());

        ImageView icon = (ImageView) changeLine.getChildAt(0);
        icon.setImageResource(newCurrency.getIconId());

        LinearLayout linearLayout = (LinearLayout) changeLine.getChildAt(1);

        // Inserting Empty Currency and...
        if (newCurrency instanceof EmptyLine) {
            // Line isn't already empty
            if (!emptyTags.contains(Integer.parseInt(changeLine.getTag().toString()))) {
                emptyTags.add(Integer.parseInt(changeLine.getTag().toString()));
                Log.i("emptyTags", String.valueOf(emptyTags));
            }

            if (changeLine.getTag().toString().equals(selectedLine.getTag().toString())) {
                setLineLayoutSelectedEmpty(linearLayout, currencyPosition);
            } else {
                setLineLayoutUnselectedEmpty(linearLayout, currencyPosition);
            }

                setLineIconEmpty(icon);

            // Inserting non-empty currencyLine and...
        } else {

            // ...line was previously empty
            if (emptyTags.contains(Integer.parseInt(changeLine.getTag().toString()))) {
                emptyTags.remove(Integer.valueOf(Integer.parseInt(changeLine.getTag().toString())));
            }

            TextView changeText = (TextView) linearLayout.getChildAt(0);
            changeText.setText("0");

            // ...line is selected.
            if (selectedLine.getTag().toString().equals(changeLine.getTag().toString())) {
                setLineLayoutSelected(linearLayout, currencyPosition);
                setLineIconSelected(icon);

                // ...line is NOT selected.
            } else {
                setLineLayoutUnselected(linearLayout, currencyPosition);
                setLineIconUnSelected(icon);
            }
        }

        // If line is selected, update the currencies
        if (changeLine.getTag().toString().equals(selectedLine.getTag().toString())) {
            updateSelectedCurrency(selectedLine.getChildAt(1));

        }
        updateUnselectedCurrencies(selectedCurrency.processNumVal(selectedCurrency, selectedText));
        closeCurrencyDrawer(view);
    }

    private void setLineLayoutSelected(LinearLayout linearLayout, int currencyPosition) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_white);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_grey);

        setStandardTextFieldPaddingAndAlpha(linearLayout);

        TextView curLabel = (TextView) linearLayout.getChildAt(1);
        Log.i("currentPosition1", setCurrencies.get(currencyPosition).getFullCurName());
        curLabel.setText(setCurrencies.get(currencyPosition).getFullCurName());
        curLabel.setTypeface(null, Typeface.BOLD);
        curLabel.setTextColor(Color.WHITE);
    }

    private void setLineLayoutUnselected(LinearLayout linearLayout, int currencyPosition) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_white);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_white);

        setStandardTextFieldPaddingAndAlpha(linearLayout);

        TextView curLabel = (TextView) linearLayout.getChildAt(1);
        Log.i("currentPosition2", setCurrencies.get(currencyPosition).getFullCurName());
        curLabel.setText(setCurrencies.get(currencyPosition).getFullCurName());
        curLabel.setTypeface(null, Typeface.BOLD);
        curLabel.setTextColor(Color.BLACK);
    }

    private void setLineLayoutSelectedEmpty(LinearLayout linearLayout, int currencyPosition) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant_border);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_grey);

        setStandardTextFieldPaddingAndAlpha(linearLayout);

        TextView curLabel = (TextView) linearLayout.getChildAt(0);
        curLabel.setText("0");
        curLabel = (TextView) linearLayout.getChildAt(1);
        curLabel.setText(setCurrencies.get(currencyPosition).getFullCurName());
        curLabel.setTypeface(null, Typeface.BOLD);
        curLabel.setTextColor(Color.WHITE);
    }

    private void setLineLayoutUnselectedEmpty(LinearLayout linearLayout, int currencyPosition) {
        linearLayout.getChildAt(0).setBackgroundResource(R.drawable.line_background_transparant_border);
        linearLayout.getChildAt(1).setBackgroundResource(R.drawable.line_background_transparant_border);

        setStandardTextFieldPaddingAndAlpha(linearLayout);

        TextView curLabel = (TextView) linearLayout.getChildAt(0);
        curLabel.setText("0");
        curLabel = (TextView) linearLayout.getChildAt(1);
        curLabel.setText(setCurrencies.get(currencyPosition).getFullCurName());
        curLabel.setTypeface(null, Typeface.BOLD);
        curLabel.setTextColor(Color.BLACK);
    }

    private void setStandardTextFieldPaddingAndAlpha(LinearLayout linearLayout) {
        linearLayout.getChildAt(0).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(1).setPadding(0, 0, 15, 0);
        linearLayout.getChildAt(0).setAlpha(0.8f);
        linearLayout.getChildAt(1).setAlpha(0.8f);
    }

    private void setLineIconSelected(ImageView icon) {
        icon.setBackgroundResource(R.drawable.line_background_transparant);
        icon.setPadding(0, 0, 0, 0);
    }

    private void setLineIconUnSelected(ImageView icon) {
        icon.setBackgroundResource(R.drawable.line_background_transparant);
        icon.setPadding(0, 0, 0, 0);
    }

    private void setLineIconEmpty(ImageView icon) {
        icon.setImageResource(R.drawable.empty_icon_border);
    }

    public void openInfoPane(View view) {
        ImageView closeInfoTrigger = findViewById(R.id.closeInfoTrigger);
        closeInfoTrigger.setVisibility(View.VISIBLE);
        closeInfoTrigger.setEnabled(true);

        TextView infoPane = findViewById(R.id.infoPane);
        infoPane.setVisibility(View.VISIBLE);
    }

    public void closeInfoPane(View view) {
        ImageView closeInfoTrigger = findViewById(R.id.closeInfoTrigger);
        closeInfoTrigger.setVisibility(View.GONE);
        closeInfoTrigger.setEnabled(false);

        TextView infoPane = findViewById(R.id.infoPane);
        infoPane.setVisibility(View.GONE);
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

        if(threeDecCurrencies.contains(String.valueOf(selectedCurrency.getClass().getSimpleName()))) {
            threeCurEnterVal(view);
        } else {
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
            }

            updateUnselectedCurrencies(selectedCurrency.processNumVal(selectedCurrency, selectedText));

        }
    }

    private void threeCurEnterVal(View view) {

        View parent = (View) selectedText.getParent();

        // Not Empty
        if (!emptyTags.contains(Integer.valueOf(parent.getTag().toString()))) {
            // Back selected
            if (view.getTag().toString().equals("back")) {
                // Value approaching zero
                if (selectedText.getText().toString().length() < 2
                        || newVal == true) {
                    if (newVal == true) {
                        newVal = false;
                    }
                    // Set to default value
                    selectedText.setText("" + 0);
                } else {
                    // Pop last digit
                    selectedText.setText(selectedText.getText().toString()
                            .substring(0, selectedText.getText().toString().length() - 1));
                }
                // Disable decimal button
            } else if (view.getTag().toString().equals(".")) {
                Toast.makeText(this, "Decimals Automatic", Toast.LENGTH_SHORT).show();
                // Text is 0 or default -- Replace value with selected number
            } else if (newVal || selectedText.getText().toString().equals("0")) {
                selectedText.setText("" + view.getTag().toString());
                newVal = false;
                // Text is not 0
            } else {
                // Append text with selected value
                selectedText.append("" + view.getTag().toString());
            }
        }

        // Handle Decimals
        selectedCurrency.handleCustomDecimals(selectedText);

        // Update Currencies
        updateUnselectedCurrencies(selectedCurrency.processNumVal(selectedCurrency, selectedText));
    }

    public void updateUnselectedCurrencies(float numVal) {

        if (Float.isNaN(numVal)) {
            numVal = 0;
        }

        for (int i = 0; i < setCurrencies.size(); i++) {
            GridLayout parentLayout = (GridLayout) grandparentLayout.getChildAt(i);
            if (parentLayout != selectedLine && (!(setCurrencies.get(i) instanceof EmptyLine))) {
                LinearLayout linearLayout = (LinearLayout) parentLayout.getChildAt(1);
                TextView textView = (TextView) linearLayout.getChildAt(0);
                setCurrencies.get(i).updateCurrency(textView, numVal, setCurrencies);
            }
        }
    }

    public void updateSelectedCurrency(View view) {
        newVal = true;
        selectedLine = (GridLayout) view.getParent();
        selectedLinear = (LinearLayout) view;
        selectedText = (TextView) selectedLinear.getChildAt(0);
        selectedTag = view.getTag().toString();
        selectedCurrency = setCurrencies.get(Integer.parseInt(selectedLine.getTag().toString()));
    }

    public ArrayList buildGenericIcon(String key, ArrayList arr) {
    switch (key) {
        case "AUD":
            arr.add(0, R.drawable.australia);
            arr.add(1, "Australian Dollar");
            return arr;
        case "BGN":
            arr.add(0, R.drawable.bulgaria);
            arr.add(1, "Bulgarian Lev");
            return arr;
        case "BRL":
            arr.add(0, R.drawable.brazil);
            arr.add(1, "Brazilian Real");
            return arr;
        case "CAD":
            arr.add(0, R.drawable.canada);
            arr.add(1, "Canadian Dollar");
            return arr;
        case "CHF":
            arr.add(0, R.drawable.switzerland);
            arr.add(1, "Swiss Franc");
            return arr;
        case "CNY":
            arr.add(0, R.drawable.china);
            arr.add(1, "Chinese Yuan Renminbi");
            return arr;
        case "CZK":
            arr.add(0, R.drawable.czec_republic);
            arr.add(1, "Czech Koruna");
            return arr;
        case "DKK":
            arr.add(0, R.drawable.denmark);
            arr.add(1, "Danish Krone");
            return arr;
        case "EUR":
            arr.add(0, R.drawable.european_union);
            arr.add(1, "Euro");
            return arr;
        case "GBP":
            arr.add(0, R.drawable.united_kingdom);
            arr.add(1, "British Pound Sterling");
            return arr;
        case "HKD":
            arr.add(0, R.drawable.hong_kong);
            arr.add(1, "Hong Kong Dollar");
            return arr;
        case "HRK":
            arr.add(0, R.drawable.croatia);
            arr.add(1, "Croatian Kuna");
            return arr;
        case "HUF":
            arr.add(0, R.drawable.hungary);
            arr.add(1, "Hungarian Forint");
            return arr;
        case "IDR":
            arr.add(0, R.drawable.indonesia);
            arr.add(1, "Indonesian Rupiah");
            return arr;
        case "ILS":
            arr.add(0, R.drawable.israel);
            arr.add(1, "Israeli Shekel");
            return arr;
        case "INR":
            arr.add(0, R.drawable.india);
            arr.add(1, "Indian Rupee");
            return arr;
        case "ISK":
            arr.add(0, R.drawable.iceland);
            arr.add(1, "Icelandic Krona");
            return arr;
        case "JPY":
            arr.add(0, R.drawable.japan);
            arr.add(1, "Japanese Yen");
            return arr;
        case "KRW":
            arr.add(0, R.drawable.south_korea);
            arr.add(1, "South Korean Won");
            return arr;
        case "MXN":
            arr.add(0, R.drawable.mexico);
            arr.add(1, "Mexican Peso");
            return arr;
        case "MYR":
            arr.add(0, R.drawable.malasya);
            arr.add(1, "Malaysian Ringgit");
            return arr;
        case "NOK":
            arr.add(0, R.drawable.norway);
            arr.add(1, "Norwegian Krone");
            return arr;
        case "NZD":
            arr.add(0, R.drawable.new_zealand);
            arr.add(1, "New Zealand Dollar");
            return arr;
        case "PHP":
            arr.add(0, R.drawable.philippines);
            arr.add(1, "Philippine Peso");
            return arr;
        case "PLN":
            arr.add(0, R.drawable.poland);
            arr.add(1, "Polish Zloty");
            return arr;
        case "RON":
            arr.add(0, R.drawable.romania);
            arr.add(1, "Romanian Leu");
            return arr;
        case "RUB":
            arr.add(0, R.drawable.russia);
            arr.add(1, "Russian Rouble");
            return arr;
        case "SEK":
            arr.add(0, R.drawable.sweden);
            arr.add(1, "Swedish Krona");
            return arr;
        case "SGD":
            arr.add(0, R.drawable.singapore);
            arr.add(1, "Singapore Dollar");
            return arr;
        case "THB":
            arr.add(0, R.drawable.thailand);
            arr.add(1, "Thai Baht");
            return arr;
        case "TRY":
            arr.add(0, R.drawable.turkey);
            arr.add(1, "Turkish Lira");
            return arr;
        case "USD":
            arr.add(0, R.drawable.united_states);
            arr.add(1, "US Dollar");
            return arr;
        case "ZAR":
            arr.add(0, R.drawable.south_africa);
            arr.add(1, "South African Rand");
            return arr;
    }
    return arr;
}

    @Override
    public void onPause() {
        super.onPause();

            long lastUpdateTime;

            try {
                lastUpdateTime = saveFile.getLong("lastUpdateTime", 0);

                if (lastUpdateTime + TWELVEHOURS < System.currentTimeMillis()) {
                    saveFile.edit().putLong("lastUpdateTime", System.currentTimeMillis()).apply();
                    populateGenericCurrencyList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                saveFile.edit().putLong("lastUpdateTime", System.currentTimeMillis()).apply();
                populateGenericCurrencyList();
            }
        saveState();
    }

    private void saveState() {
        try {
            saveFile.edit().putString("setCurrencies", ObjectSerializer.serialize(setCurrencies)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grandparentLayout = findViewById(R.id.convLines);

        showLoadingScreen();

        saveFile = this.getSharedPreferences("package com.adamdoq.fcconvert", Context.MODE_PRIVATE);

        if(saveFile.contains("initialized")) {
            loadData();
            createFuncsWithoutDelay();
        } else {
            saveFile.edit().putBoolean("initialized", true).apply();
            saveFile.edit().putLong("lastUpdateTime", System.currentTimeMillis()).apply();

            setCurrencies = new ArrayList<>(Arrays.asList(defaultCurrencies[0], defaultCurrencies[1],
                    defaultCurrencies[2], defaultCurrencies[3]));

            setupLocationServices();
            createFuncsWithDelay();
        }
    }

    private void showLoadingScreen() {
        View loadingScreen = findViewById(R.id.loadingScreen);

        loadingScreen.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        setCurrencies = new ArrayList<>();

        try{
            setCurrencies = (ArrayList<Currency>) ObjectSerializer.deserialize
                    (saveFile.getString("setCurrencies", ObjectSerializer.serialize(new ArrayList<>())));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setupLocationServices() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if(!userLocationFound) {
                    Log.i("Here4", "Here4");
                    Log.i("User Location", location.toString());

                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        userCountryName = addresses.get(0).getCountryName();

                        if(!userCountryName.equals("Unknown")) {
                            CountryToCodeConverter converter = new CountryToCodeConverter();
                            userCountryCode = converter.convert(userCountryName);
                        }
                    } catch (IOException e) {
                    }
                    userLocationFound = true;
               }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void setupKeypadOnTouchListeners(){

        GridLayout keypadLayout = findViewById(R.id.keypadLayout);

        for (int i = 0; i < keypadLayout.getChildCount(); i++) {

            final View button = keypadLayout.getChildAt(i);

            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // Using onTouch removes button's onClick -- put enterVal back here
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        enterVal(button);
                        button.setBackgroundTintList(getResources().getColorStateList(R.color.OffGrey));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        button.setBackgroundTintList(getResources().getColorStateList(R.color.OffWhite));
                    }

                    return true;
                }
            });
        }
    }

    private void setLoadedCurrencies() {
        try {
            ImageView icon = findViewById(R.id.icon0);
            icon.setImageResource(setCurrencies.get(0).getIconId());

            icon = findViewById(R.id.icon1);
            icon.setImageResource(setCurrencies.get(1).getIconId());

            icon = findViewById(R.id.icon2);
            icon.setImageResource(setCurrencies.get(2).getIconId());

            icon = findViewById(R.id.icon3);
            icon.setImageResource(setCurrencies.get(3).getIconId());
        } catch(Exception e) {
            setCurrencies = new ArrayList<>(Arrays.asList(defaultCurrencies[0], defaultCurrencies[1],
                    defaultCurrencies[2], defaultCurrencies[3]));

            ImageView icon = findViewById(R.id.icon0);
            icon.setImageResource(setCurrencies.get(0).getIconId());

            icon = findViewById(R.id.icon1);
            icon.setImageResource(setCurrencies.get(1).getIconId());

            icon = findViewById(R.id.icon2);
            icon.setImageResource(setCurrencies.get(2).getIconId());

            icon = findViewById(R.id.icon3);
            icon.setImageResource(setCurrencies.get(3).getIconId());
        }
    }

    private void createFuncsWithoutDelay() {
        setLoadedCurrencies();
        setupKeypadOnTouchListeners();
        setThreeDecCurrencies(new HPWC(), new GOTWG());
        trackEmptyLines();
        configureInfoPane();
        configureCurrencyDrawer();
        populateCurrencyLists();
        populateCurrencyDrawer();
        setInitialSelectedLine();
        endLoadingScreen();
    }

    private void createFuncsWithDelay() {
        setLoadedCurrencies();
        setupKeypadOnTouchListeners();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setThreeDecCurrencies(new HPWC(), new GOTWG());
                trackEmptyLines();
                configureInfoPane();
                configureCurrencyDrawer();
                populateCurrencyLists();
                populateCurrencyDrawer();
                setInitialSelectedLine();
                endLoadingScreen();
            }
        }, 2000);
    }

    private void setThreeDecCurrencies(Currency ...currencies) {

        threeDecCurrencies = new ArrayList<>();

        for (Currency currency : currencies) {
            threeDecCurrencies.add(String.valueOf(currency.getClass().getSimpleName()));
        }


    }

    private void trackEmptyLines() {
        emptyTags = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if (setCurrencies.get(i) instanceof EmptyLine) {
                emptyTags.add(i);
            }
        }
    }

    private void configureInfoPane() {
        ImageView closeInfoTrigger = findViewById(R.id.closeInfoTrigger);
        closeInfoTrigger.setVisibility(View.GONE);
        closeInfoTrigger.setEnabled(false);

        TextView infoPane = findViewById(R.id.infoPane);
        infoPane.setBackgroundResource(R.drawable.infopane_background);
        infoPane.setText("Don't sue me!");
        infoPane.setVisibility(View.GONE);
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

        currencyDrawer.setOnTouchListener(new OnSwipeTouchListener(this) {
                       public void onSwipeRight() {
                closeCurrencyDrawer(currencyDrawer);
            }
        });

        closeDrawerTrigger = findViewById(R.id.closeDrawerTrigger);
        closeDrawerTrigger.setVisibility(View.GONE);
        closeDrawerTrigger.setEnabled(false);

        closeDrawerTrigger.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                closeCurrencyDrawer(closeDrawerTrigger);
            }
        });


    }

    private void populateCurrencyLists() {
        populateCustomCurrencyList();
        populateGenericCurrencyList();
    }

    private void populateCustomCurrencyList() {
        currencyList = new ArrayList<>(
                Arrays.asList(new BC(), new FFGIL(), new GOTWG(), new ZHR(), new HPWC(), new BL(),
                        new SWIC(), new FC(), new EmptyLine())
        );
    }

    private void populateGenericCurrencyList() {
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

                ArrayList<?> arr = new ArrayList<>();

                arr = buildGenericIcon(key, arr);

                if(arr.size() == 2) {

                    GenericCurrency genericCurrency = new GenericCurrency(
                            Float.parseFloat(String.valueOf(jsonChildObject.get(key))),
                            (Integer) arr.get(0),
                            (String) arr.get(1),
                            key + " - " + arr.get(1),
                            R.drawable.generic_bg
                    );

                    // Changes first currency to user's country (should stick in different method)
                    if(userCountryCode.equals(key)) {
                        loadHomeCurrency(key, genericCurrency);
                    }

                    genericCurrencyList.add(genericCurrency);
                    Log.i("main", key);
                    Log.i("main", String.valueOf(jsonChildObject.get(key)));
                }
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

    private void loadHomeCurrency(String key, Currency currency) {
            setCurrencies.set(0, currency);

            ImageView icon = findViewById(R.id.icon0);
            icon.setImageResource(setCurrencies.get(0).getIconId());
        }

    private void populateCurrencyDrawer() {
        LinearLayout currencyDrawerLayout = findViewById(R.id.currencyDrawerLayout);

        int listedCurrencies = 0;

        for (Currency currency : currencyList) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setTag(listedCurrencies);

            //Defines drawer line params
            LinearLayout.LayoutParams drawerLineParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            drawerLineParams.height = 300;
            item.setLayoutParams(drawerLineParams);

            //Sets backgrounds
            if (currency.drawerBgId != 0 && currency.drawerBgId != R.drawable.remove_bg) {
                item.setBackgroundResource(currency.drawerBgId);
                Drawable background = item.getBackground();
                background.setAlpha(150);
            } else if (currency.drawerBgId == R.drawable.remove_bg) {
                item.setBackgroundResource(currency.drawerBgId);
            } else {
                item.setBackgroundResource(R.drawable.line_background_grey);
            }


            //Sets onClick listener
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectNewCurrency(v);
                }
            });

            //Defines icon params
            LinearLayout.LayoutParams iconParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            iconParams.height = 300;
            iconParams.width = 250;

            if (listedCurrencies <= currencyList.size() - 1) {
                iconParams.setMargins(5, 2, 2, 2);
            } else {
                iconParams.setMargins(30, 2, 2, 2);
            }


            //Sets icons
            ImageView icon = new ImageView(this);
            icon.setImageResource(currency.getIconId());
            icon.setLayoutParams(iconParams);
            item.addView(icon);

            //Sets Text
            TextView text = new TextView(this);
            text.setText(currency.getDescription());
            text.setTextColor(Color.WHITE);
            text.setPadding(20, 25, 20, 25);
            text.setTextSize(19);
            text.setHeight(300);
            text.setGravity(Gravity.CENTER_VERTICAL);
            item.addView(text);

            //Adds line to drawer
            currency.setCurrencyId(listedCurrencies);
            listedCurrencies++;
            currencyDrawerLayout.addView(item);
        }
    }

    private void setInitialSelectedLine() {
        selectedLine = findViewById(R.id.line0);
        selectedLinear = (LinearLayout) selectedLine.getChildAt(1);
        selectedText = (TextView) selectedLinear.getChildAt(0);
        selectedTag = selectedText.getTag().toString();
        selectLine(selectedText);
    }

    private void endLoadingScreen() {
        final View loadingScreen = findViewById(R.id.loadingScreen);

        loadingScreen.animate().alpha(0).setDuration(1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingScreen.setVisibility(View.GONE);
            }
        }, 1500);
    }
}

class sortCurrencies implements Comparator<Currency> {

    @Override
    public int compare(Currency cur1, Currency cur2) {
        return cur1.getDescription().compareTo(cur2.getDescription());
    }
}