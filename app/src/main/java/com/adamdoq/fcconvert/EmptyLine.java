package com.adamdoq.fcconvert;

public class EmptyLine extends Currency {

    public EmptyLine() {
        exchangeRate = 0;
        iconId = R.drawable.line_background_transparant;
        fullCurName = "EMPTY";
        description = "";
        drawerBgId = R.drawable.remove_bg;
    }

}
