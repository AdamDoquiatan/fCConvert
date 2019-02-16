package com.adamdoq.fcconvert;

public class SWIC extends Currency {

    public SWIC() {
        exchangeRate = 0.90f;
        iconId = R.drawable.swic_icon;
        fullCurName = "Imperial Credits";
        description = "Because Rep Creds are no good here.";
        drawerBgId = R.drawable.swic_bg;
    }
}
