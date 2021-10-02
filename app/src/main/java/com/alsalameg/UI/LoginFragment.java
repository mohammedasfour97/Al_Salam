package com.alsalameg.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alsalameg.BaseClasses.BaseFragment;
import com.alsalameg.Constants;
import com.alsalameg.MyApplication;
import com.alsalameg.R;
import com.alsalameg.Models.User;
import com.alsalameg.ViewModels.LoginViewModel;
import com.alsalameg.databinding.FragmentLoginBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding fragmentLoginBinding;
    private String usernameText, passwordText;
    private LoginViewModel loginViewModel;
    private Observer<User> loginObserver;

    @Override
    public void onStart() {
        super.onStart();
        //utils.setLocale("ar");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentLoginBinding = DataBindingUtil.inflate(inflater , R.layout.fragment_login, container, false);
        return fragmentLoginBinding.getRoot();
    }


    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!TextUtils.isEmpty(MyApplication.getTinyDB().getString(Constants.KEY_USER_TYPE))){

            fragmentLoginBinding.fragmentLoginUsernameEdt.setText(MyApplication.getTinyDB().getString(Constants.KEY_USERNAME));
            fragmentLoginBinding.fragmentLoginPasswordEdt.setText(MyApplication.getTinyDB().getString(Constants.KEY_USER_PASSWORD));
        }
//        Button crashButton = new Button(getContext());
//        crashButton.setText("Test Crash");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });
//
//        getActivity().addContentView(crashButton, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        setListeners();
        initObservers();
    }


    private void setListeners(){

        fragmentLoginBinding.fragmentLoginLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getTextsFromEditTexts()) {

                    if (hasInternetConnection(getContext())) {

                        showProgressDialog(getResources().getString(R.string.loading), getResources().getString(R.string.loading_msg),
                                false);

                        //loginViewModel.userMutableLiveData(usernameText, passwordText).removeObservers(getViewLifecycleOwner());

                        loginViewModel.userMutableLiveData(usernameText, passwordText, MyApplication.getTinyDB()
                                .getString(Constants.KEY_UID)).observe(getViewLifecycleOwner(), loginObserver);
                        //((MainActivity)getActivity()).navController.navigate(R.id.action_fragment_login_to_fragment_main_records);
                    }
                }
            }
        });
    }


    private void initObservers(){

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        loginObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {

                if (user != null) {

                    if (getViewLifecycleOwner().getLifecycle().getCurrentState()== Lifecycle.State.RESUMED){

                        hideProgress();

                        if (!user.isError()) {

                            MyApplication.getTinyDB().putString(Constants.KEY_USERID, user.getId());


                            switch (user.getType()) {

                                case "Observed":
                                    ((MainActivity) getActivity()).navController.navigate(R.id.action_fragment_login_to_fragment_map);
                                    break;

                                case "Recorded":
                                    ((MainActivity) getActivity()).navController.navigate
                                            (R.id.action_fragment_login_to_fragment_main_records);
                                    break;

                                case "Listener":

                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("lis_rec_car", false);

                                    ((MainActivity) getActivity()).navController.navigate
                                            (R.id.action_fragment_login_to_fragment_listen_records, bundle);
                                    break;
                            }

                            MyApplication.getTinyDB().putString(Constants.KEY_USERID, user.getId());
                            MyApplication.getTinyDB().putString(Constants.KEY_USERNAME, fragmentLoginBinding.
                                    fragmentLoginUsernameEdt.getText().toString());
                            MyApplication.getTinyDB().putString(Constants.KEY_USER_PASSWORD, fragmentLoginBinding.
                                    fragmentLoginPasswordEdt.getText().toString());
                            MyApplication.getTinyDB().putString(Constants.KEY_USER_TYPE, user.getType());

                            hideKeyboard();
                        }

                        else {

                            showFailedDialog(getResources().getString(R.string.username_pass_err), true);
                        }
                    }


                }

            }
        };
    }


    private boolean getTextsFromEditTexts(){

        usernameText = fragmentLoginBinding.fragmentLoginUsernameEdt.getText().toString();
        passwordText = fragmentLoginBinding.fragmentLoginPasswordEdt.getText().toString();

        if (TextUtils.isEmpty(usernameText)){

            fragmentLoginBinding.fragmentLoginUsernameEdt.setError(getResources().getString(R.string.err_username));
            return false;
        }

        if (TextUtils.isEmpty(passwordText)){

            fragmentLoginBinding.fragmentLoginUsernameEdt.setError(getResources().getString(R.string.err_password));
            return false;
        }
        else return true;
    }
}
