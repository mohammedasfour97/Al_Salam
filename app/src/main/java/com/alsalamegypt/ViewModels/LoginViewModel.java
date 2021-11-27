package com.alsalamegypt.ViewModels;

import com.alsalamegypt.Repositories.LoginRepository;
import com.alsalamegypt.Models.User;

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
