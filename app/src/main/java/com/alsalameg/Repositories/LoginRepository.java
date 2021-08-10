package com.alsalameg.Repositories;

import android.os.AsyncTask;

import com.alsalameg.BaseClasses.BaseRepository;
import com.alsalameg.Models.Car;
import com.alsalameg.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class LoginRepository extends BaseRepository {

    private MutableLiveData<User> userMutableLiveData;


    public LoginRepository() {
        super();

    }

    public MutableLiveData<User> getUserMutableLiveData(String username, String password) {

        userMutableLiveData = new MutableLiveData<>();

        new GetUserInfoAsyncClass(username, password).execute();

        return userMutableLiveData;
    }



    private class GetUserInfoAsyncClass extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

        private String username, password;

        public GetUserInfoAsyncClass(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(Void... voids) {
            return webService.getUserInfoService(username, password);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> userInfo) {

            User user = new User();

            if (userInfo != null && !userInfo.isEmpty()) {

                HashMap<String, String> userData = userInfo.get(0);

                User user1 = new User(userData.get("ID"), userData.get("Code"), userData.get("FullName"), userData.get("USERNAME"),
                        userData.get("USERPASS"), userData.get("TYPE"), userData.get("Adress"), userData.get("Phone"),
                        userData.get("Email"), userData.get("Note"), userData.get("Img"), userData.get("ID_USER"),
                        userData.get("ID_Listen"), userData.get("DateEdite"), userData.get("MODIFICATION_DATE"), false);
                user = user1;

                userMutableLiveData.postValue(user);
            } else {

                user.setError(true);
                userMutableLiveData.postValue(user);
            }

        }

    }

}


