package com.alsalameg.ViewModels;

import com.alsalameg.Models.Car;
import com.alsalameg.Repositories.LoginRepository;
import com.alsalameg.Models.User;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private LoginRepository loginRepository;

    public LoginViewModel(){

        loginRepository = new LoginRepository();
    }

    public MutableLiveData<User> userMutableLiveData(String username, String password, String uid){

        return loginRepository.getUserMutableLiveData(username, password, uid);
    }


}
