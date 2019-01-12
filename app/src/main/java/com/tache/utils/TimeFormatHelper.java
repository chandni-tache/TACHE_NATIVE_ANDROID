package com.tache.utils;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by mayank on 10/10/16.
 */

public class TimeFormatHelper {

    public static CharSequence getRelativeTime(String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date startDate = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            startDate = simpleDateFormat.parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return DateUtils.getRelativeTimeSpanString(startDate.getTime(), calendar.getTimeInMillis(), DateUtils.SECOND_IN_MILLIS);

    }

    public static String getDaysHoursMinutes(String date_to) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateTo = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            dateTo = simpleDateFormat.parse(date_to);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        long currentMillis = System.currentTimeMillis();
        long dateToMillis = dateTo.getTime();

        String toReturn = "";

        if (currentMillis > dateToMillis)
            toReturn = "Expired";
        else {
            long leftMillis = dateToMillis - currentMillis;

            int day = (int) TimeUnit.MILLISECONDS.toDays(leftMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(leftMillis) - (day * 24);
            long minute = TimeUnit.MILLISECONDS.toMinutes(leftMillis) - (TimeUnit.MILLISECONDS.toHours(leftMillis) * 60);

            if (day > 0)
                if (day == 1)
                    toReturn = day + " day left";
                else
                    toReturn = day + " days left";
            else if (hours > 0)
                if (hours == 1)
                    toReturn = hours + " hour left";
                else
                    toReturn = hours + " hours left";
            else
                toReturn = minute + " minute left";
        }
        return toReturn;
    }

    public static String getDateInAppropriateFormat(String dateString) {
        if (TextUtils.isEmpty(dateString)) return dateString;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString);
            return getDateInAppropriateFormat(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String getDateInAppropriateFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getDateInStandardFormat(String dateString) {
        if (TextUtils.isEmpty(dateString)) return dateString;
        try {
            Date date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(dateString);
            return getDateInStandardFormat(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String getDateInStandardFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getInDMY(String inputDate) {
        String formattedDate = null;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(inputDate);
            formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String getInDM(String inputDate) {
        String formattedDate = null;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(inputDate);
            formattedDate = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
