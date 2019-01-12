package com.tache.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.tache.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayank on 31/8/16.
 */
public class UploadingDialog extends BaseDialog {

    @BindView(R.id.dialog_upload_circle_loading_view)
    AnimatedCircleLoadingView circleLoadingView;

    TextView textView;
    private boolean isOk = true;
    private AnimatedCircleLoadingView.AnimationListener animationListener;

    public static UploadingDialog newInstance(AnimatedCircleLoadingView.AnimationListener animationListener) {
        UploadingDialog fragment = new UploadingDialog();
        fragment.animationListener = animationListener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_upload, null);
        textView = (TextView) view.findViewById(R.id.dialog_upload_text_view);

        builder.setView(view);
        ButterKnife.bind(this, view);
        circleLoadingView.startIndeterminate();
        if (animationListener != null) {
            circleLoadingView.setAnimationListener(animationListener);
        } else {
            circleLoadingView.setAnimationListener(new AnimatedCircleLoadingView.AnimationListener() {
                @Override
                public void onAnimationEnd(boolean success) {
                    dismiss();
                }
            });
        }
        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);

        return alertDialog;
    }

    public void setPercent(int percent) {
        circleLoadingView.setPercent(percent);
    }

    public void setMessage(String message) {
        if (textView != null) {
            textView.setText(message);
        }
    }

    public void stopOk(boolean isOk) {
        this.isOk = isOk;
        if (circleLoadingView == null) {
            dismiss();
            return;
        }
        if (isOk) {
            circleLoadingView.stopOk();
        } else {
            circleLoadingView.stopFailure();
        }
        dismiss();
    }

}
