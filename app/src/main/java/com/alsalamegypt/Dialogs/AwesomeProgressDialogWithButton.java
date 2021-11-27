package com.alsalamegypt.Dialogs;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alsalamegypt.R;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeDialogBuilder;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AwesomeProgressDialogWithButton extends AwesomeDialogBuilder<AwesomeProgressDialogWithButton> {

    private ProgressBar progressBar;
    private RelativeLayout dialogBody;
    private Button positiveButton;

    public AwesomeProgressDialogWithButton(Context context) {
        super(context);

        setColoredCircle(R.color.dialogProgressBackgroundColor);
        setProgressBarColor(R.color.white);
        setColoredCircle(R.color.dialogInfoBackgroundColor);
        setPositiveButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
    }

    {
        progressBar = findView(R.id.dialog_progress_bar);
        dialogBody = findView(R.id.dialog_body);
        positiveButton = findView(R.id.btDialogYes);
    }

    public AwesomeProgressDialogWithButton setDialogBodyBackgroundColor(int color){
        if (dialogBody != null) {
            dialogBody.getBackground().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
        }

        return this;
    }

    public AwesomeProgressDialogWithButton setProgressBarColor(int color) {
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
        return this;
    }

    public AwesomeProgressDialogWithButton setPositiveButtonText(String text) {
        if (positiveButton != null) {
            positiveButton.setText(text);
            positiveButton.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public AwesomeProgressDialogWithButton setPositiveButtonClick(@Nullable final Closure selectedYes) {
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedYes != null) {
                    selectedYes.exec();
                }

                hide();
            }
        });

        return this;
    }

    public AwesomeProgressDialogWithButton setPositiveButtonbackgroundColor(int buttonBackground) {
        if (positiveButton != null) {
            positiveButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), buttonBackground), PorterDuff.Mode.SRC_IN);
        }

        return this;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_progress_with_button;
    }
}
