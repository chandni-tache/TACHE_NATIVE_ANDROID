package com.tache.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tache.R;


/**
 * Created by mayank on 7/10/16.
 */

public class MyDatePickerDialog extends DatePickerDialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int titleId = context.getResources().getIdentifier( "title_template", "id", "android" );
        if (titleId > 0) {
            LinearLayout dialogTitle = (LinearLayout) this.findViewById(titleId);
            if (dialogTitle != null) {
                dialogTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }
        titleId = context.getResources().getIdentifier( "alertTitle", "id", "android" );
        if (titleId > 0) {
            TextView dialogTitle = (TextView) this.findViewById(titleId);
            if (dialogTitle != null) {
                dialogTitle.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            }
        }
    }

    private Context context;
    private CharSequence title;

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.context = context;
    }

    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    @Override
    public void onDateChanged(@NonNull DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle(title);
    }
}
