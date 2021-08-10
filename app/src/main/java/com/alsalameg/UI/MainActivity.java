package com.alsalameg.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import dagger.Module;
import dagger.Provides;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.alsalameg.Components.DaggerNavigationControllerComponent;
import com.alsalameg.Components.NavigationControllerComponent;
import com.alsalameg.Constants;
import com.alsalameg.Modules.NavigationControllerModule;
import com.alsalameg.R;
import com.alsalameg.TinyDB;

import javax.inject.Inject;

@Module
public class MainActivity extends AppCompatActivity {

    @Inject
    NavHostFragment navHostFragment;
    public NavController navController;
    private boolean doubleBackToExitPressedOnce;
    public TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_main);

        DaggerNavigationControllerComponent.builder().navigationControllerModule(new NavigationControllerModule(this)).build()
                .inject(this);

        navController = navHostFragment.getNavController();

        boolean doubleBackToExitPressedOnce = false;

        tinyDB = new TinyDB(this);

        checkPreviousLoginToNavigate();
    }

    private void checkPreviousLoginToNavigate(){

        if (getIntent().getStringExtra(Constants.KEY_USER_TYPE) != null) {

            switch (getIntent().getStringExtra(Constants.KEY_USER_TYPE)) {

                case "Observed":
                    navController.navigate(R.id.action_fragment_login_to_fragment_map);
                    break;

                case "Recorded":
                    navController.navigate
                            (R.id.action_fragment_login_to_fragment_main_records);
                    break;

                case "Listener":
                    navController.navigate
                            (R.id.action_fragment_login_to_fragment_listen_records);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {

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