package com.tache.rest.models.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStats {

    @SerializedName("coins")
    @Expose
    private Integer coins;

    /**
     *
     * @return
     * The coins
     */
    public Integer getCoins() {
        return coins;
    }

    /**
     *
     * @param coins
     * The coins
     */
    public void setCoins(Integer coins) {
        this.coins = coins;
    }

}
