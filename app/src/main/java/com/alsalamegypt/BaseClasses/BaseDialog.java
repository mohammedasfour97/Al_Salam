package com.alsalamegypt.BaseClasses;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import com.alsalamegypt.Dialogs.AwesomeProgressDialogWithButton;
import com.alsalamegypt.R;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.alsalamegypt.Dialogs.FlatDialog;

public class BaseDialog {

    private Context context;

    private AwesomeInfoDialog awesomeInfoDialog;
    private AwesomeProgressDialog awesomeProgressDialog;
    private AwesomeSuccessDialog awesomeSuccessDialog;
    private AwesomeErrorDialog awesomeErrorDialog;
    private AwesomeWarningDialog awesomeWarningDialog;
    private AwesomeProgressDialogWithButton awesomeProgressDialogWithButton;

    private FlatDialog flatDialog;

    public BaseDialog(Context context) {
        this.context = context;
    }

    public AwesomeInfoDialog awesomeInfoWithThreeButtonsDialog(String title, String message, String positive_button_message,
                                                             String negative_text_message, String neutral_text_msg,
                                                               Closure positive_button_click, Closure negative_button_click,
                                                               Closure neutral_button_click, boolean cancelable){


        awesomeInfoDialog =  new AwesomeInfoDialog(context)
                .setTitle(getSpanningString(title, R.style.Bold_Primary_Color_TextView_style))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.purple_500)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(cancelable)
                .setPositiveButtonText(positive_button_message)
                .setPositiveButtonbackgroundColor(R.color.purple_500)
                .setPositiveButtonTextColor(R.color.white)

                .setNegativeButtonText(negative_text_message)
                .setNegativeButtonbackgroundColor(R.color.purple_500)
                .setNegativeButtonTextColor(R.color.white)

                .setNeutralButtonText(neutral_text_msg)
                .setNegativeButtonbackgroundColor(R.color.purple_500)
                .setNegativeButtonTextColor(R.color.white)

                .setPositiveButtonClick(positive_button_click)
                .setNegativeButtonClick(negative_button_click)
                .setNeutralButtonClick(neutral_button_click);

        return awesomeInfoDialog;
    }

    public AwesomeInfoDialog awesomeInfoWithTwoButtonsDialog(String title, String message, String positive_button_message,
                                                      String negative_text_message, Closure positive_button_click,
                                                      Closure negative_button_click, boolean cancelable){


        awesomeInfoDialog =  new AwesomeInfoDialog(context)
                .setTitle(getSpanningString(title, R.style.Bold_Primary_Color_TextView_style))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.purple_500)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(cancelable)
                .setPositiveButtonText(positive_button_message)
                .setPositiveButtonbackgroundColor(R.color.purple_500)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonText(negative_text_message)
                .setNegativeButtonbackgroundColor(R.color.purple_500)
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonClick(positive_button_click)
                .setNegativeButtonClick(negative_button_click);

        return awesomeInfoDialog;
    }


    public AwesomeInfoDialog awesomeInfoWithOneButtonsDialog(String title, String message, String positive_button_message,
                                                      Closure positive_button_click, boolean cancelable){

        awesomeInfoDialog =  new AwesomeInfoDialog(context)
                .setTitle(getSpanningString(title, R.style.Bold_Default_TextView_style))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.purple_500)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(cancelable)
                .setPositiveButtonText(positive_button_message)
                .setPositiveButtonbackgroundColor(R.color.purple_500)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(positive_button_click);

        return awesomeInfoDialog;
    }


    public AwesomeWarningDialog awesomeWarningDialog(String title, String message, String positive_button_message,
                                                             Closure positive_button_click, boolean cancelable){

        awesomeWarningDialog =  new AwesomeWarningDialog(context)
                .setTitle(getSpanningString(title, R.style.Bold_Default_TextView_style))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(cancelable)
                .setButtonText(positive_button_message)
                .setButtonTextColor(R.color.white)
                .setWarningButtonClick(positive_button_click);

        return awesomeWarningDialog;
    }


    public AwesomeProgressDialog awesomeProgressDialog(String title, String message, boolean cancelable){

        awesomeProgressDialog =  new AwesomeProgressDialog(context)
                .setTitle(getSpanningString(title, R.style.Bold_Default_TextView_style))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.dialogProgressBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(cancelable);

        return awesomeProgressDialog;
    }


    public AwesomeProgressDialogWithButton awesomeProgressDialogWithButton(String title, String message, String buttonText,
                                                                           Closure buttonClick){

        awesomeProgressDialogWithButton =  new AwesomeProgressDialogWithButton(context)
                .setTitle(getSpanningString(title, R.style.Bold_Default_TextView_style))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setPositiveButtonClick(buttonClick)
                .setCancelable(false);

        return awesomeProgressDialogWithButton;
    }


    public AwesomeSuccessDialog awesomeSuccessDialog(String message, boolean cancelable){

         awesomeSuccessDialog =  new AwesomeSuccessDialog(context);
                awesomeSuccessDialog.setTitle(context.getResources().getString(R.string.succ))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_success, R.color.white)
                .setPositiveButtonText(context.getResources().getString(R.string.dialog_ok_button))
                .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                .setPositiveButtonTextColor(R.color.white)
                .setCancelable(cancelable).setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {

                        awesomeSuccessDialog.hide();

                    }
                });

        return awesomeSuccessDialog;
    }


    public AwesomeErrorDialog awesomeErrorDialog(String message,boolean cancelable) {

        awesomeErrorDialog =  new AwesomeErrorDialog(context);
                awesomeErrorDialog.setTitle(context.getResources().getString(R.string.error))
                .setMessage(getSpanningString(message, R.style.Regular_Default_TextView_style))
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                .setCancelable(cancelable)
                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                .setButtonText(context.getResources().getString(R.string.dialog_ok_button))
                .setButtonTextColor(R.color.white)
                .setErrorButtonClick(new Closure() {
                    @Override
                    public void exec() {

                        awesomeErrorDialog.hide();
                    }
                });

                return awesomeErrorDialog;
    }


    public FlatDialog flatDialogWithEditTextTwoButtons(String title, String message, String positive_button_message,
                                                String negative_text_message, View.OnClickListener positiveOnClickListenerButton,
                                                View.OnClickListener negativeOnClickListenerButton, boolean cancelable){


        flatDialog = new FlatDialog(context)
                .setTitle(getSpanningString(title, R.style.Bold_Default_TextView_style).toString())
                .setSubtitle(getSpanningString(message, R.style.Regular_Default_TextView_style).toString())
                .setFirstTextFieldHint(context.getResources().getString(R.string.write_here))
                .setFirstButtonText(positive_button_message)
                .setSecondButtonText(negative_text_message)
                .withFirstButtonListner(positiveOnClickListenerButton)
                .withSecondButtonListner(negativeOnClickListenerButton)
                .isCancelable(cancelable);

        return flatDialog;

    }


    public void hideInfoDialogWithTwoButton(){

        if (awesomeInfoDialog!=null) awesomeInfoDialog.hide();
    }

    public void hideWarningDialog(){

        if (awesomeWarningDialog!=null) awesomeWarningDialog.hide();
    }

    public void hideProgress(){

        if (awesomeProgressDialog!=null) awesomeProgressDialog.hide();
        else if (awesomeProgressDialogWithButton != null) awesomeProgressDialogWithButton.hide();
    }

    public void hideFlatDialogWithEditTextTwoButtons(){

        if (flatDialog!=null) flatDialog.hide();
    }


    private SpannableString getSpanningString(String s, int style){

        SpannableString span = new SpannableString(s);

        span.setSpan(new TextAppearanceSpan(context, style), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return span;
    }
}
