package com.alsalamegypt.Repositories;

import android.os.AsyncTask;
import android.util.Log;

import com.alsalamegypt.BaseClasses.BaseRepository;
import com.alsalamegypt.Models.User;
import com.alsalamegypt.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class LoginRepository extends BaseRepository {

    private MutableLiveData<User> userMutableLiveData;


    public LoginRepository() {
        super();

    }

    public MutableLiveData<User> getUserMutableLiveData(String username, String password, String uid) {

        userMutableLiveData = new MutableLiveData<>();

        getFirebaseToken(username, password, uid);

        return userMutableLiveData;
    }


    public void getFirebaseToken(String username, String password, String uid){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            new GetUserInfoAsyncClass(username, password, uid, "error :" + task.getResult());

                            return;
                        }

                        // Get new FCM registration token
                        new GetUserInfoAsyncClass(username, password, uid, task.getResult()).execute();

                        Log.v("tokennnn", task.getResult());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                new GetUserInfoAsyncClass(username, password, uid, "error :" + e.getMessage());
            }
        });

    }



    private class GetUserInfoAsyncClass extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

        private String username, password, uid, firebaseToken;

        public GetUserInfoAsyncClass(String username, String password, String uid, String firebaseToken) {
            this.username = username;
            this.password = password;
            this.uid = uid;
            this.firebaseToken = firebaseToken;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(Void... voids) {
            return webServices.getUserInfoService(username, password, uid);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> userInfo) {

            User user;

            if (userInfo != null && !userInfo.isEmpty()) {

                HashMap<String, String> userData = userInfo.get(0);

                user = new User(userData.get("ID"), userData.get("Code"), userData.get("FullName"), userData.get("USERNAME"),
                        userData.get("USERPASS"), userData.get("TYPE"), userData.get("Adress"), userData.get("Phone"),
                        userData.get("Email"), userData.get("Note"), userData.get("Img"), userData.get("ID_USER"),
                        userData.get("ID_Listen"), userData.get("DateEdite"), userData.get("MODIFICATION_DATE"), false);

                new GetUpdateFirebaseTokenAsyncClass(user, firebaseToken).execute();

            } else {

                user = new User();
                user.setError(true);

                userMutableLiveData.postValue(user);
            }

        }

    }


    private class GetUpdateFirebaseTokenAsyncClass extends AsyncTask<Void, Void, List<HashMap<String, String>>> {

        private String firebaseToken;
        private User user;

        public GetUpdateFirebaseTokenAsyncClass(User user, String firebaseToken) {
            this.firebaseToken = firebaseToken;
            this.user = user;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(Void... voids) {
            return webServices.updateFirebaseToken(user.getId(), firebaseToken);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            userMutableLiveData.postValue(user);

            /*if (result != null && !result.get(0).get("error").equals("1")) {

                userMutableLiveData.postValue(user);

            } else {

                user.setError(true);
                userMutableLiveData.postValue(user);
            }

             */

        }

    }

}


