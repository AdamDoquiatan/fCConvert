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


public class MainActivity extends AppCompatActivity {

    private GridLayout selectedLine;
    private TextView selectedText;
    private String selectedTag;

    public void selectLine(View view) {
        if(!view.getTag().toString().equals(selectedTag)) {
            selectedText = (TextView) view;
            selectedTag = view.getTag().toString();

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
        //float numVal;

        //DecimalFormat formatter = new DecimalFormat();
        //formatter.applyPattern("0.##");

        //numVal = Float.parseFloat(selectedText.getText().toString());

        if(selectedText.getText().toString().equals("0")) {
            selectedText.setText("" + view.getTag().toString());
        } else if(view.getTag().toString().equals("dec")){
            selectedText.append(".");
        } else if(view.getTag().toString().equals("back")){
            selectedText.setText(selectedText.getText().toString()
                    .substring(0, selectedText.getText().toString().length() - 1));
        } else {
            selectedText.append("" + view.getTag().toString());
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedLine = findViewById(R.id.line0);
        selectedText = findViewById(R.id.lineText0);
        selectedTag = selectedText.getTag().toString();

        selectedLine.setBackgroundColor(Color.DKGRAY);
        selectedText.setTextColor(Color.WHITE);

        Log.i("text", selectedText.getText().toString());

    }
}
