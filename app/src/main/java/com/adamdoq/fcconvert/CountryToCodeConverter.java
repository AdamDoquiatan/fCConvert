package com.adamdoq.fcconvert;

import java.util.HashMap;

public class CountryToCodeConverter {

    private HashMap<String, String> map;

    public CountryToCodeConverter() {
        map = new HashMap<>();
        map.put("Australia","AUD");
        map.put("Bulgaria","BGN");
        map.put("Brazil","BRL");
        map.put("Canada", "CAD");
        map.put("Switzerland","CHF");
        map.put("China","CNY");
        map.put("Czechia","CZK");
        map.put("Denmark", "DKK");
        map.put("United Kingdom","GBP");
        map.put("Hong Kong", "HKD");
        map.put("Croatia","HRK");
        map.put("Hungary","HUF");
        map.put("Indonesia","IDR");
        map.put("Israel","ILS");
        map.put("India","INR");
        map.put("Iceland","ISK");
        map.put("Japan","JPY");
        map.put("South Korea","KRW");
        map.put("Mexico","MXN");
        map.put("Malaysia","MYR");
        map.put("Norway","NOK");
        map.put("New Zealand","NZD");
        map.put("Philippines","PHP");
        map.put("Poland","PLN");
        map.put("Romania","RON");
        map.put("Russia","RUB");
        map.put("Sweden","SEK");
        map.put("Singapore","SGD");
        map.put("Thailand","THB");
        map.put("Turkey","TRY");
        map.put("United States", "USD");
        map.put("South Africa","ZAR");
        map.put("Austria","Euro");
        map.put("Belgium","Euro");
        map.put("Cyprus","Euro");
        map.put("Estonia","Euro");
        map.put("Finland","Euro");
        map.put("France","Euro");
        map.put("Germany","Euro");
        map.put("Greece","Euro");
        map.put("Ireland","Euro");
        map.put("Italy","Euro");
        map.put("Latvia","Euro");
        map.put("Lithuania","Euro");
        map.put("Luxembourg","Euro");
        map.put("Malta","Euro");
        map.put("Netherlands","Euro");
        map.put("Portugal","Euro");
        map.put("Slovakia","Euro");
        map.put("Slovenia","Euro");
        map.put("Spain","Euro");
    }

    public String convert(String country) {
        return map.get(country);
    }
}
