package com.tache.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.rest.ApiUtils;
import com.tache.rest.services.LinksService;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimReferralActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Refer & Earn");
        setContentView(R.layout.activity_refer);

        editText = (EditText) findViewById(R.id.referral_code);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
                    linksService.applyReferralCode(Helper.getAuthHeader(ClaimReferralActivity.this), editText.getText().toString()).enqueue(referralCallback);
                } else
                    Toast.makeText(ClaimReferralActivity.this, "Enter valid referral code.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    Callback<JsonObject> referralCallback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            if (response.isSuccessful()) {
                Toast.makeText(ClaimReferralActivity.this, "Code applied.", Toast.LENGTH_SHORT).show();
                exitToMainActivity();
            } else {
                editText.setError("That does not seem valid!");
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to skip submitting referral code?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPrefsUtils.getInstance(ClaimReferralActivity.this).setBooleanPreference(SharedPrefsUtils.USER_REFERRAL_SKIP, true);
                exitToMainActivity();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void exitToMainActivity() {
        Intent intent = new Intent(ClaimReferralActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
