package com.tache.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.Contact;
import com.tache.rest.models.request.ContactUsRequest;
import com.tache.rest.services.LinksService;
import com.tache.rest.services.UserService;
import com.tache.utils.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Contact us");
        setContentView(R.layout.activity_contact_us);
        initUi();
    }

    private void initUi() {
        AppCompatTextView mail = (AppCompatTextView) findViewById(R.id.contactMail);

        final EditText name = (EditText) findViewById(R.id.name);
        final EditText message = (EditText) findViewById(R.id.message);
        final EditText email = (EditText) findViewById(R.id.email);

        Drawable drawable1 = ContextCompat.getDrawable(this, R.drawable.ic_email_black_24dp);
        drawable1 = DrawableCompat.wrap(drawable1);
        DrawableCompat.setTint(drawable1, Color.WHITE);
        DrawableCompat.setTintMode(drawable1, PorterDuff.Mode.SRC_ATOP);
        mail.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null);

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.support_email), null)), "Send email"));
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Name is required");
                    valid = false;
                }
                if (!PatternsCompat.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setError("Valid email is required");
                    valid = false;
                }
                if (message.getText().toString().trim().isEmpty()) {
                    message.setError("Message is required");
                    valid = false;
                }
                if (valid) {
                    contactUsRequest(new ContactUsRequest(message.getText().toString(), name.getText().toString(), email.getText().toString()));

                    InputMethodManager imm = (InputMethodManager) ContactUs.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

    }

    private void contactUsRequest(ContactUsRequest contactUsRequest) {
        LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        linksService.contactUs(Helper.getAuthHeader(this), contactUsRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ContactUs.this, "Message sent", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

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
        super.onBackPressed();
    }

}
