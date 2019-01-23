package com.tache.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.events.NavigateToEvent;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.profile.ProfileInfo;
import com.tache.rest.models.response.BankDetails;
import com.tache.rest.models.response.User;
import com.tache.rest.services.LinksService;
import com.tache.rest.services.UserService;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mayank on 19/2/17.
 */

public class BasicInfoFragment extends Fragment implements View.OnClickListener {
    private Unbinder unbinder;
    private TextView countTask, countSurvey, balance;
    private SharedPrefsUtils sharedPrefsUtils;
    private Dialog dialogRedeem, dialogBank;
    private BankDetails bankDetails;

    private EditText bankName, branchName, ifscCode, accountNumber;
    private Spinner accountType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_basic_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        countTask = (TextView) view.findViewById(R.id.countTask);
        countSurvey = (TextView) view.findViewById(R.id.countSurvey);
        balance = (TextView) view.findViewById(R.id.balance);
        countTask.setOnClickListener(this);
        countSurvey.setOnClickListener(this);
        view.findViewById(R.id.taskHistory).setOnClickListener(this);
        view.findViewById(R.id.surveyHistory).setOnClickListener(this);
        view.findViewById(R.id.redeem).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences myPref = getActivity().getSharedPreferences("count", Context.MODE_PRIVATE);
       String st = myPref.getString("countValue",null);
      System.out.println("dfghjhgfdsdfghgfds========"+st);
       countSurvey.setText(String.valueOf(st));
        sharedPrefsUtils = SharedPrefsUtils.getInstance(getContext());
        System.out.println("qwertyuipoiuytrqwert======"+String.valueOf(sharedPrefsUtils.getIntegerPreference(SharedPrefsUtils.TOTAL_SURVEY_COMPLETED, 0)));
        countTask.setText(String.valueOf(sharedPrefsUtils.getIntegerPreference(SharedPrefsUtils.TOTAL_MISSIONS_COMPLETED, 0)));
        countSurvey.setText(String.valueOf(sharedPrefsUtils.getIntegerPreference(SharedPrefsUtils.TOTAL_SURVEY_COMPLETED, 0)));
        balance.setText(String.valueOf(sharedPrefsUtils.getFloatPreference(SharedPrefsUtils.TOTAL_EARNINGS, 0)));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setData(User user) {
//        SharedPreferences myPref = getActivity().getSharedPreferences("count", Context.MODE_PRIVATE);
//        String st = myPref.getString("countValue",null);
//        System.out.println("Completed Survey ===== "+user.getTotal_survey_completed());

        int panelCount= SharedPrefsUtils.getInstance(getContext()).getIntegerPreference("count_history",0);
        int auditCount = SharedPrefsUtils.getInstance(getContext()).getIntegerPreference("count_history_audit",0);
       // countTask.setText(String.valueOf(user.getTotal_missions_completed()));
        countSurvey.setText(panelCount+"");
        countTask.setText(auditCount+"");
        balance.setText(String.valueOf(user.getTotal_earnings()));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setData(ProfileInfo profileInfo) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redeem:
                if (sharedPrefsUtils.getBooleanPreference(SharedPrefsUtils.USER_HAS_BANK, false))
                    if (sharedPrefsUtils.getFloatPreference(SharedPrefsUtils.TOTAL_EARNINGS, 0) > 0)
                        dialogRedeem();
                    else
                        Toast.makeText(getContext(), "You dont have sufficient balance.", Toast.LENGTH_SHORT).show();
                else
                    dialogBank();
                break;
            case R.id.taskHistory:
                EventBus.getDefault().post(new NavigateToEvent("MissionHistory"));
                break;
            case R.id.surveyHistory:
                EventBus.getDefault().post(new NavigateToEvent("SurveyHistory"));
                break;
        }
    }

    private void dialogBank() {
        if (dialogBank == null) {
            dialogBank = new Dialog(getContext(), R.style.DialogBox);
            dialogBank.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogBank.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialogBank.setCancelable(false);
            dialogBank.setContentView(R.layout.dialog_bank_detail);

            bankName = (EditText) dialogBank.findViewById(R.id.bankName);
            branchName = (EditText) dialogBank.findViewById(R.id.branchName);
            ifscCode = (EditText) dialogBank.findViewById(R.id.ifscCode);
            accountType = (Spinner) dialogBank.findViewById(R.id.accountType);
            accountNumber = (EditText) dialogBank.findViewById(R.id.accountNumber);

            final TextView submit = (TextView) dialogBank.findViewById(R.id.submit);
            TextView skip = (TextView) dialogBank.findViewById(R.id.skip);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid = true;
                    if (TextUtils.isEmpty(bankName.getText())) {
                        bankName.setError("Give your bank name");
                        valid = false;
                    }
                    if (TextUtils.isEmpty(branchName.getText())) {
                        branchName.setError("Give your branch name");
                        valid = false;
                    }
                    if (TextUtils.isEmpty(ifscCode.getText())) {
                        ifscCode.setError("Give your IFSC code");
                        valid = false;
                    }
                    if (TextUtils.isEmpty(accountNumber.getText())) {
                        accountNumber.setError("Give your account number");
                        valid = false;
                    }

                    if (valid) {
                        if (dialogBank.getWindow().getDecorView().getWindowToken() != null) {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(dialogBank.getWindow().getDecorView().getWindowToken(), 0);
                        }

                        BankDetails bankDetails = new BankDetails();
                        bankDetails.setBank_name(bankName.getText().toString());
                        bankDetails.setBranch_name(branchName.getText().toString());
                        bankDetails.setIfsc(ifscCode.getText().toString());
                        bankDetails.setAccount_type(accountType.getSelectedItem().toString());
                        bankDetails.setAccount_number(accountNumber.getText().toString());
                        bankDetails.setAccount_name(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_NAME));

                        createUpdateBankDetails(bankDetails);
                    }
                }
            });

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBank.dismiss();
                }
            });
        }

        if (bankDetails != null) {
            bankName.setText(bankDetails.getBank_name());
            branchName.setText(bankDetails.getBranch_name());
            ifscCode.setText(bankDetails.getIfsc());
            accountNumber.setText(bankDetails.getAccount_number());
            accountType.setSelection(getIndex(accountType, bankDetails.getAccount_type()));
        }

        dialogBank.show();
    }

    private int getIndex(Spinner spinner, String string) {
        int index = 0, count = spinner.getCount();
        for (int i = 0; i < count; i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void dialogRedeem() {
        if (dialogRedeem == null) {
            dialogRedeem = new Dialog(getContext(), R.style.DialogBox);
            dialogRedeem.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogRedeem.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialogRedeem.setCancelable(false);
            dialogRedeem.setContentView(R.layout.dialog_redeem);

            TextView title = (TextView) dialogRedeem.findViewById(R.id.redeemTitle);
            TextView message = (TextView) dialogRedeem.findViewById(R.id.redeemMessage);
            final EditText amount = (EditText) dialogRedeem.findViewById(R.id.redeemAmount);
            final TextView submit = (TextView) dialogRedeem.findViewById(R.id.submit);
            final TextView editBankDetails = (TextView) dialogRedeem.findViewById(R.id.editBankDetails);
            final TextView skip = (TextView) dialogRedeem.findViewById(R.id.skip);
            final Float max = sharedPrefsUtils.getFloatPreference(SharedPrefsUtils.TOTAL_EARNINGS, 0);

            title.setText("Redeem");
            message.setText("Enter the amount below to redeem.\nYou can redeem maximum of " + max);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (amount.getText().toString().isEmpty())
                        amount.setError("Enter an amount to redeem");
                    else {
                        try {
                            float toRedeem = Float.valueOf(amount.getText().toString());
                            if (toRedeem <= max) {
                                if (amount.getWindowToken() != null) {
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(amount.getWindowToken(), 0);
                                }
                                redeemAmount(toRedeem);
                            } else
                                amount.setError("Amount cannot be more than " + max);
                        } catch (NumberFormatException ex) {
                            amount.setError("Enter valid amount to redeem");
                        }
                    }
                }
            });

            editBankDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBankDetails();
                }
            });

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogRedeem.dismiss();
                }
            });
        }
        dialogRedeem.show();
    }

    private void showBankDetails() {
        LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        linksService.getBankDetails(Helper.getAuthHeader(getContext())).enqueue(new Callback<BankDetails>() {
            @Override
            public void onResponse(Call<BankDetails> call, Response<BankDetails> response) {
                System.out.println("Bank Details = 1 "+response.toString());
                if (response.isSuccessful()) {
                    BasicInfoFragment.this.bankDetails = response.body();
                    dialogBank();
                }
                if (dialogRedeem != null)
                    dialogRedeem.dismiss();
            }

            @Override
            public void onFailure(Call<BankDetails> call, Throwable t) {
                Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                if (dialogRedeem != null)
                    dialogRedeem.dismiss();
                BasicInfoFragment.this.bankDetails = null;
            }
        });
    }

    private void createUpdateBankDetails(BankDetails bankDetails) {
        LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        if (!sharedPrefsUtils.getBooleanPreference(SharedPrefsUtils.USER_HAS_BANK, false))
            linksService.createBankDetails(Helper.getAuthHeader(getContext()), bankDetails).enqueue(callbackBankDetail);
        else
            linksService.updateBankDetail(Helper.getAuthHeader(getContext()), bankDetails).enqueue(callbackBankDetail);
    }

    Callback<BankDetails> callbackBankDetail = new Callback<BankDetails>() {
        @Override
        public void onResponse(Call<BankDetails> call, Response<BankDetails> response) {
            System.out.println("Bank Details = 2 "+response.toString());
            if (response.isSuccessful()) {
                sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_HAS_BANK, true);
                Toast.makeText(getContext(), "Bank details saved", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            if (dialogBank != null)
                dialogBank.dismiss();
        }

        @Override
        public void onFailure(Call<BankDetails> call, Throwable t) {
            Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
            if (dialogBank != null)
                dialogBank.dismiss();
        }
    };

    private void redeemAmount(float toRedeem) {
        LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        linksService.redeem(Helper.getAuthHeader(getContext()), toRedeem).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Hello One User");
                if (response.isSuccessful()) {
                    updateUser();
                    Toast.makeText(getContext(), "Amount redeemed", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                if (dialogRedeem != null)
                    dialogRedeem.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                if (dialogRedeem != null)
                    dialogRedeem.dismiss();
            }
        });
    }

    private void updateUser() {
        UserService userService = ApiUtils.retrofitInstance().create(UserService.class);
        userService.getUser(Helper.getAuthHeader(getContext())).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Helper.saveUser(response.body(), getContext(), sharedPrefsUtils);
                    EventBus.getDefault().postSticky(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
