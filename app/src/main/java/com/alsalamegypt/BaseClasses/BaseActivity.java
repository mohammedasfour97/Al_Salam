package com.alsalamegypt.BaseClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alsalamegypt.R;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private BaseDialog baseDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseDialog = new BaseDialog(this);
    }

    public void showInfoDialogWithTwoButtons(String title, String message, String positive_button_message,
                                             String negative_text_message, Closure positive_button_click,
                                             Closure negative_button_click, boolean cancelable) {

        baseDialog.awesomeInfoWithTwoButtonsDialog(title, message, positive_button_message, negative_text_message,
                positive_button_click, negative_button_click, cancelable).show();
    }


    public void showInfoDialogWithOneButton(String title, String message, String positive_button_message,
                                            Closure positive_button_click, boolean cancelable) {

        baseDialog.awesomeInfoWithOneButtonsDialog(title, message, positive_button_message,
                positive_button_click, cancelable).show();
    }

    public void showProgressDialog(String title, String message, boolean cancelable) {

        baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), message, cancelable).show();
    }

    public void showDefaultProgressDialog() {

        baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg), false)
                .show();
    }

    public void showSuccessDialog(String message, boolean cancelable) {

        baseDialog.awesomeSuccessDialog(message, cancelable).show();
    }

    public void showFailedDialog(String message, boolean cancelable) {

        baseDialog.awesomeErrorDialog(message, cancelable).show();
    }

    public void showFlatDialogWithEditTextTwoButtons(String title, String message, String positive_button_message,
                                                     String negative_text_message, View.OnClickListener positive_button_click,
                                                     View.OnClickListener negative_button_click, boolean cancelable) {

        baseDialog.flatDialogWithEditTextTwoButtons(title, message, positive_button_message, negative_text_message,
                positive_button_click, negative_button_click, cancelable).show();

    }

    public void hideInfoDialogWithTwoButton() {

        baseDialog.hideInfoDialogWithTwoButton();
    }

    public void hideProgress() {

        baseDialog.hideProgress();
    }

    public void hideFlatDialogWithEditTextTwoButtons() {

        baseDialog.hideFlatDialogWithEditTextTwoButtons();
    }

    public void showSnackBar(int message) {

        Snackbar snackbar = Snackbar.make(BaseActivity.this.findViewById(android.R.id.content), getResources().getString(message),
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {

            showSnackBar(R.string.check_int_con);
            return false;
        }
    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) BaseActivity.this.getSystemService(BaseActivity.this.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = BaseActivity.this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(BaseActivity.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }
}
