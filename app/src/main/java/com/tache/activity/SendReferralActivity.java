package com.tache.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.tache.R;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import java.util.List;

public class SendReferralActivity extends AppCompatActivity implements View.OnClickListener {

    private String message;
    private String playStoreLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Refer & Earn");
        setContentView(R.layout.activity_send_referral);

        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(this);
        String code = sharedPrefsUtils.getStringPreference(SharedPrefsUtils.REFERRAL_CODE);
        playStoreLink = String.format(getString(R.string.play_store_link), getPackageName());
        message = String.format(getString(R.string.refer_message), code);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Referral code copied", Toast.LENGTH_SHORT).show();

        TextView referralCode = (TextView) findViewById(R.id.referral_code);
        referralCode.setText(code);

        findViewById(R.id.shareFacebook).setOnClickListener(this);
        findViewById(R.id.shareWhatsapp).setOnClickListener(this);
        findViewById(R.id.shareTwitter).setOnClickListener(this);
        findViewById(R.id.shareMore).setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareFacebook:
                dialogFacebookShare();
                break;
            case R.id.shareWhatsapp:
                whatsappShare();
                break;
            case R.id.shareTwitter:
                twitterShare();
                break;
            case R.id.shareMore:
                createChooser();
                break;
        }
    }

    private void createChooser() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message + "\n" + playStoreLink);
        startActivity(Intent.createChooser(share, "Share referral!"));
    }

    private void twitterShare() {
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s", Helper.urlEncode(message), Helper.urlEncode(playStoreLink));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }

        startActivity(intent);
    }

    private void whatsappShare() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, message + "\n" + playStoreLink);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp has not been installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialogFacebookShare() {
        ShareDialog shareDialog = new ShareDialog(this);
        ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentTitle("Tache").setContentDescription(message).setContentUrl(Uri.parse(playStoreLink)).build();
        shareDialog.show(linkContent);
    }

}
