package com.tache.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 4/14/2017.
 */

public class BankDetails {
    @SerializedName("bank_name")
    @Expose
    String bank_name;

    @SerializedName("account_type")
    @Expose
    String account_type;

    @SerializedName("ifsc")
    @Expose
    String ifsc;

    @SerializedName("branch_name")
    @Expose
    String branch_name;

    @SerializedName("account_number")
    @Expose
    String account_number;

    @SerializedName("account_name")
    @Expose
    String account_name;

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }
}
