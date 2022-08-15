package com.alsalamegypt.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alsalamegypt.Components.DaggerNavigationControllerComponent;
import com.alsalamegypt.Constants;
import com.alsalamegypt.Interfaces.IOnBackPressed;
import com.alsalamegypt.Modules.NavigationControllerModule;
import com.alsalamegypt.R;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    NavHostFragment navHostFragment;
    public NavController navController;
    private boolean doubleBackToExitPressedOnce;
    private List<Fragment> fragments;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_main);

        DaggerNavigationControllerComponent.builder().navigationControllerModule(new NavigationControllerModule(this)).build()
                .inject(this);

        navController = navHostFragment.getNavController();

        boolean doubleBackToExitPressedOnce = false;

        checkPreviousLoginToNavigate();
    }

    private void checkPreviousLoginToNavigate() {

        if (getIntent().getStringExtra(Constants.KEY_USER_TYPE) != null) {

            switch (getIntent().getStringExtra(Constants.KEY_USER_TYPE)) {

                case "USER":
                    navController.navigate(R.id.action_fragment_login_to_fragment_map);
                    break;

                case "Recorded":
                    navController.navigate(R.id.action_fragment_login_to_fragment_main_records);
                    break;

                case "Listener":

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("lis_rec_car", false);

                    navController.navigate(R.id.action_fragment_login_to_fragment_listen_records, bundle);
                    break;

                case "Observed":
                    navController.navigate(R.id.action_fragment_login_to_fragment_map);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (fragments == null){

            fragments = getSupportFragmentManager().getFragments();
        }

        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                currentFragment = fragment;
            }

        if (!(currentFragment instanceof IOnBackPressed) || !((IOnBackPressed) currentFragment).onBackPressed()) {

            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.please_press_back_again), Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }


    }
}