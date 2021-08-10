package com.alsalameg.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.alsalameg.BaseClasses.BaseDialog;
import com.alsalameg.Constants;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.TinyDB;
import com.alsalameg.Models.User;
import com.alsalameg.ViewModels.LoginViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class SplashActivity extends AppCompatActivity {

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

                if (MyApplication.getTinyDB().getString(Constants.KEY_USERID)!=null && !TextUtils.isEmpty(MyApplication.getTinyDB()
                        .getString(Constants.KEY_USERID))){

                    baseDialog.awesomeProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg),
                            false).show();

                    loginViewModel.userMutableLiveData(MyApplication.getTinyDB().getString(Constants.KEY_USERNAME), MyApplication.getTinyDB()
                            .getString(Constants.KEY_USER_PASSWORD))
                            .observe(SplashActivity.this, userObserver);
                }

                else
                    startMainActivity();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void startMainActivity(){

        startActivity(intent);
        finish();
    }
}