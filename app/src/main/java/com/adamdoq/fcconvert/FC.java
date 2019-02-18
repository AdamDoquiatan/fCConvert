package com.adamdoq.fcconvert;

public class FC extends Currency {

    public FC() {
        super();
        exchangeRate = 1.19f;
        iconId = R.drawable.fc_icon;
        fullCurName = "Federation Credits";
        description = "Replicators have tanked the exchange rate.";
        drawerBgId = R.drawable.fc_bg;
    }

}
