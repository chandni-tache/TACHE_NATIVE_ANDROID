package com.tache.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.tache.R;

/**
 * Created by mayank on 19/2/17.
 */

public class RupeeTextView extends TextView {
    public RupeeTextView(Context context) {
        super(context);
    }

    public RupeeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RupeeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RupeeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text)) {
            super.setText(text, type);
        }
        else {
            int length = text.length();
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text + " " + getResources().getString(R.string.Rs));
            stringBuilder.setSpan(new SuperscriptSpan(), length, length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new RelativeSizeSpan(0.4f), length, length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new SuperscriptSpan(), length + 1, length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new RelativeSizeSpan(0.6f), length + 1, length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            super.setText(stringBuilder, type);
        }
    }
}
