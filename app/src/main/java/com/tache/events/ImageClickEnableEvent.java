package com.tache.events;

/**
 * Created by mayank on 1/12/16.
 */

public class ImageClickEnableEvent {

    private int postId;
    private boolean isImageClickable;

    public ImageClickEnableEvent(int postId, boolean isImageClickable) {
        this.postId = postId;
        this.isImageClickable = isImageClickable;
    }

    public int getPostId() {
        return postId;
    }

    public boolean isImageClickable() {
        return isImageClickable;
    }
}
