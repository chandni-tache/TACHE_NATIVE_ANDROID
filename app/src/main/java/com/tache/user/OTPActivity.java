package com.tache.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tache.R;

public class OTPActivity extends AppCompatActivity {

    TextView textView1;
    EditText editText1;
    Button button1;
    String st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        textView1 = (TextView)findViewById(R.id.textSome);
        editText1 = (EditText)findViewById(R.id.otpOne);
        button1 = (Button)findViewById(R.id.verifyOtp);
        st = "By Tapping \\u0022quote Send confirmation code1\\u0022 above, Tache Technologies will send you an SMS to confirm your phone number. On the next screen, you can choose Response code to receive a new SMS or Call me to initiate a phone call to confirm your phone number. Message, call & data rates may apply.";

        textView1.setText(st);


    }
}
