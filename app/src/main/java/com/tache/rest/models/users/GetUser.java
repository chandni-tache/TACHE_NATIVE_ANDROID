
package com.tache.rest.models.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetUser {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("coins")
    @Expose
    private Integer coins;
    @SerializedName("is_mobile_verified")
    @Expose
    private Boolean isMobileVerified;
    @SerializedName("followers_count")
    @Expose
    private Integer followersCount;
    @SerializedName("following_count")
    @Expose
    private Integer followingCount;
    @SerializedName("posts_count")
    @Expose
    private Integer postsCount;
    @SerializedName("is_following")
    @Expose
    private Integer isFollowing;
    @SerializedName("categories")
    @Expose
    private List<Object> categories = new ArrayList<>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     *
     * @param mobile
     * The mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The profileImage
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     *
     * @param profileImage
     * The profile_image
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

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

    /**
     *
     * @return
     * The isMobileVerified
     */
    public Boolean getIsMobileVerified() {
        return isMobileVerified;
    }

    /**
     *
     * @param isMobileVerified
     * The is_mobile_verified
     */
    public void setIsMobileVerified(Boolean isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }

    /**
     *
     * @return
     * The followersCount
     */
    public Integer getFollowersCount() {
        return followersCount;
    }

    /**
     *
     * @param followersCount
     * The followers_count
     */
    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    /**
     *
     * @return
     * The followingCount
     */
    public Integer getFollowingCount() {
        return followingCount;
    }

    /**
     *
     * @param followingCount
     * The following_count
     */
    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    /**
     *
     * @return
     * The postsCount
     */
    public Integer getPostsCount() {
        return postsCount;
    }

    /**
     *
     * @param postsCount
     * The posts_count
     */
    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    /**
     *
     * @return
     * The isFollowing
     */
    public Integer getIsFollowing() {
        return isFollowing;
    }

    /**
     *
     * @param isFollowing
     * The is_following
     */
    public void setIsFollowing(Integer isFollowing) {
        this.isFollowing = isFollowing;
    }

    /**
     *
     * @return
     * The categories
     */
    public List<Object> getCategories() {
        return categories;
    }

    /**
     *
     * @param categories
     * The categories
     */
    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }

}