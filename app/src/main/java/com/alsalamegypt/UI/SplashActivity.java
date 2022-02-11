package com.alsalamegypt.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.alsalamegypt.BaseClasses.BaseActivity;
import com.alsalamegypt.BaseClasses.BaseDialog;
import com.alsalamegypt.Constants;
import com.alsalamegypt.MyApplication;
import com.alsalamegypt.R;
import com.alsalamegypt.Models.User;
import com.alsalamegypt.Utils;
import com.alsalamegypt.ViewModels.LoginViewModel;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class SplashActivity extends BaseActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private LoginViewModel loginViewModel;
    private Observer<User> userObserver;
    private BaseDialog baseDialog;
    private Intent intent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        baseDialog = new BaseDialog(this);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        intent = new Intent(SplashActivity.this, MainActivity.class);

        userObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {

                if (user != null) {

                    if (getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        baseDialog.hideProgress();

                        if (!user.isError()) {

                            intent.putExtra(Constants.KEY_USER_TYPE, user.getType());
                            MyApplication.getTinyDB().putString(Constants.KEY_USER_TYPE, user.getType());
                        }

                        else {

                            intent.putExtra(Constants.KEY_USER_TYPE, "no_type");

                            Toast.makeText(SplashActivity.this, getResources().getString(R.string.username_pass_err), Toast.LENGTH_SHORT).show();
                        }



                        startMainActivity();
                    }


                }

            }
        };

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//                    if (!checkPermission()) return;
//                }

                tryToLogin();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }


    private void tryToLogin(){

        if (MyApplication.getTinyDB().getString(Constants.KEY_USERID)!=null && !TextUtils.isEmpty(MyApplication.getTinyDB()
                .getString(Constants.KEY_USERID))){

            baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg),
                    false).show();

            loginViewModel.userMutableLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERNAME),
                    MyApplication.getTinyDB().getString(Constants.KEY_USER_PASSWORD),
                    MyApplication.getTinyDB().getString(Constants.KEY_UID)).observe(SplashActivity.this, userObserver);
        }

        else
            startMainActivity();
    }



    private boolean checkPermission() {

        if (!Constants.checkIdentifierPermission(this)) {

            showInfoDialogWithOneButton(getResources().getString(R.string.req_uid_perm),
                    getResources().getString(R.string.req_uid_perm_message), getResources().getString(R.string.req_loc_perm_pos_btn),
                    new Closure() {
                        @Override
                        public void exec() {

                            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                        }
                    }, false);
            return false;
        } else return true;
    }


    private void startMainActivity(){

        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    tryToLogin();
                }
                else finish();
                break;
        }
    }
}